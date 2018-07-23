package com.rickytaki.login.service;

import com.rickytaki.login.security.UserDetailsDao;
import com.rickytaki.login.security.UserDetailsCustom;
import com.rickytaki.login.security.UserDetailsServiceCustom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserDetailsServiceCustomTest {

    @Mock
    private UserDetailsDao dao;

    @InjectMocks
    private UserDetailsServiceCustom service;

    private UserDetailsCustom user;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() {
        user = new UserDetailsCustom();
        user.setUsername("details@details.com");
        user.setPassword("TestPass");
    }

    @Test
    public void whenNotNull_LoadUser_ShouldReturnUserDetails() {
        when(dao.loadUserByUsername("details@details.com")).thenReturn(Optional.of(user));
        UserDetails found = service.loadUserByUsername(user.getUsername());

        Assert.assertEquals(user.getUsername(), found.getUsername());
        verify(dao).loadUserByUsername(user.getUsername());
    }

    @Test
    public void whenLoadUserNotFound_ShouldReturnExceptionAndMEssage() {
        exceptionRule.expect(UsernameNotFoundException.class);
        exceptionRule.expectMessage("User arlindo@arlindo.com not found");
        service.loadUserByUsername("arlindo@arlindo.com");
    }
}

