package gomobi.io.forex.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import gomobi.io.forex.service.CustomUserDetailsService;  // Replace UserService with CustomUserDetailsService
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;  // Inject CustomUserDetailsService
    
    //helps to skip JWT validations!
    private static final Set<String> PUBLIC_PATHS = Set.of(
    	    "/api/auth/register"
    	);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    	//using authorization filter
		//String authHeader = request.getHeader("Authorization");
		//
		//if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		//    filterChain.doFilter(request, response);
		//  	  return;
		//    }
		//
		//String token = authHeader.substring(7);
    	
    	String path = request.getServletPath();
    	String token = null;
    	
    	// no JWT check! directly go for controller!
    	//skip token validation for public paths (even if token is present)
        if (PUBLIC_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get JWT token from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // No token found in cookies
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
        	//IMPORTANT!
        	/*
        	 * If the JWT is expired, malformed, tampered, or invalid in any way, 
        	 * this line will throw an exception — specifically a subclass of io.jsonwebtoken.JwtException like:
				ExpiredJwtException
				UnsupportedJwtException
				MalformedJwtException
				SignatureException
        	*/
            String username = jwtUtil.extractUsername(token); 

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Use the CustomUserDetailsService to load the user by username
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);  // Use CustomUserDetailsService instead of UserService

                if (jwtUtil.validateToken(token, userDetails)) {
                	
                	//Create a special Spring Security object that says:
                	//“Yes, I’m authenticated and here’s my info (roles, etc.)”
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,  // principal (contains username, authorities etc.)
                                null,         // credentials (do not expose password again)
                                userDetails.getAuthorities());

                    //Optional but useful: attach some request-related metadata (like IP, session ID).
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 

                    //It tells Spring Security:
                    //"The user is now authenticated!"
                    //Now, any controller or service can call SecurityContextHolder.getContext().getAuthentication() and get the logged-in user's info.
                    
                    //Each thread has seperate secContextHolder! this is like global variable that holds authen user info.
                    //this entry is created each time when user logs in.
                    //so in other pages, we can use context holder to get loggedin user info.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

        } catch (Exception e) {
        	//the token is invalid!
        	
        	//TOKEN CHECK IS FAILED!
        	//CREDS CHECK (IF FOUND IN BODY)!
        	System.out.println("JWT Filter Path: " + path);
        	
        	/*
        	 * if user hits login path with invalid creds(already checked, but valid creds)
        	 * we check current path...if login, then we skip that "returning jwt error!"
        	 * the control then moves to dispatcher servlet, eventually hits login controller of POST
        	 * thus user gets new token.
        	*/
        	if (!path.equals("/api/auth/login")) {
        	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	    response.setContentType("application/json");
        	    response.setCharacterEncoding("UTF-8");

        	    Map<String, String> errorMap = new HashMap<>();
        	    errorMap.put("error", "Token has expired");

        	    new ObjectMapper().writeValue(response.getWriter(), errorMap);
        	    return;
        	}

        }

        //control goes to the my login controller!
        filterChain.doFilter(request, response);
    }
}
