package gomobi.io.forex.service;

import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.repository.UserRepository;

//The "fetcher"
//has only one method!

@Component
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // This method will be called by the AuthenticationProvider to load the user based on the username or email.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from database based on username or email
        UserEntity userEntity = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));

        // Return a UserDetails object with the user's credentials and role(s)
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),  // Ensure this password is stored hashed
                Collections.singleton(new SimpleGrantedAuthority(userEntity.getRole()))
        );
        //return new CustomUserDetails(userEntity);
    }
}
