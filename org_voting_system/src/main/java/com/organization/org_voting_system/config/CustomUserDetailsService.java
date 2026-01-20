package com.organization.org_voting_system.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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
            .orElse(userService.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email: " + username)));

        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new UsernameNotFoundException("User account is inactive: " + username);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UsernameNotFoundException("User password is not set for user: " + username);
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new UsernameNotFoundException("User username is not set for user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User role is not set for user: " + user.getUsername());
        }
        String role = "ROLE_" + user.getRole().getRoleName().toString().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
