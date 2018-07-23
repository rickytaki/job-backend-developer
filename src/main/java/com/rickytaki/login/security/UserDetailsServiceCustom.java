package com.rickytaki.login.security;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

    private UserDetailsDao dao;

    @Override
    @Cacheable(value = "login", key = "#s")
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return dao.loadUserByUsername(s).
                orElseThrow(() -> new UsernameNotFoundException("User " + s + " not found"));
    }
}
