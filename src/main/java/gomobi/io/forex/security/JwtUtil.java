package gomobi.io.forex.security;

import java.util.Date;

// A cryptographic key used in symmetric algorithms like HMAC
import javax.crypto.SecretKey; //symmetric encryption key!

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private static final String SECRET_KEY_STRING = "k6DpnRwn8NLBaBEGuAJAaaix15mGq9VH";
	
	private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());//Utility to convert your string key into a SecretKey instance
	
	//util method to generate token
	public String generateToken(UserDetails userDetails) {
		return Jwts.builder() //this creates the token!
				.subject(userDetails.getUsername()) //set username in JWT
				.issuedAt(new Date()) //current date
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //1sec * 60 = 60 sec(1 min) * 60 = 1hr 
				.signWith(SECRET_KEY,Jwts.SIG.HS256) //That signs the JWT using HMAC with SHA-256.
				.compact(); //compiles it into a String (final token)
	}
	
	//util to validate generated token
	public boolean validateToken(String token, UserDetails userDetails) {
		return extractUsername(token).equals(userDetails.getUsername());
	}
	
	//util to extract username from token
	public String extractUsername(String token) {
		return Jwts.parser()
		.verifyWith(SECRET_KEY)
		.build()
		.parseSignedClaims(token)
		.getPayload() //to get all data
		.getSubject(); //to get data (the one given in subject)
	}
	
}
