package com.organization.org_voting_system.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsernameOptional(username)
                .orElse(userService.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)));

        // Get role safely and normalize
        String roleName = (user.getRole() != null) ? user.getRole().getRoleName().name() : "voter";
        roleName = roleName.toUpperCase().replace("-", "_"); // for ELECTION_OFFICER

        // Debug logs (optional)
        System.out.println("==== AUTH DEBUG ====");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Active: " + user.getIsActive());
        System.out.println("Role from DB: " + roleName);
        System.out.println("Granted Authority: " + roleName);
        System.out.println("====================");

        // Build Spring Security user
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),       // hashed password
                user.getIsActive(),           // enabled
                true,                         // accountNonExpired
                true,                         // credentialsNonExpired
                true,                         // accountNonLocked
                Collections.singletonList(
                        new SimpleGrantedAuthority(roleName)
                )
        );
    }
}
