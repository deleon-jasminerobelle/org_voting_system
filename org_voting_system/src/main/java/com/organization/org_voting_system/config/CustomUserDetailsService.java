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
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userService.findByUsernameOptional(username)
                .orElse(userService.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)));

        if (user.getRole() == null) {
            throw new RuntimeException("User has NO ROLE assigned");
        }

        // ✅ ROLE IS ROLE_ADMIN / ROLE_VOTER / ROLE_ELECTION_OFFICER
        String authority = user.getRole().getRoleName().name();

        // ✅ DEBUG (KEEP TEMPORARILY)
        System.out.println("==== AUTH DEBUG ====");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Enabled: " + user.getIsActive());
        System.out.println("Granted Authority: " + authority);
        System.out.println("====================");

        return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.getIsActive(),
        true,
        true,
        true,
        Collections.singletonList(
                new SimpleGrantedAuthority(authority)
        )
);
    }
}
