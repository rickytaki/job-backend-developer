package com.rickytaki.login.service;

import com.rickytaki.login.dao.UserInfoDao;
import com.rickytaki.login.model.UserInfo;
import com.rickytaki.login.response.UserInfoResponse;
import ma.glasnost.orika.MapperFacade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoDao dao;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock(name = "map")
    private MapperFacade mapper;

    @InjectMocks
    private UserInfoService service;

    private UserInfo user;

    private UserInfoResponse userResponse;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() {
        user = new UserInfo();
        user.setName("testing");
        user.setAge(22);
        user.setEmail("testing@testing.com");
        user.setZipCode("010101010");
        user.setNumber(22);
        user.setStreet("Testing street");



        userResponse = new UserInfoResponse();
        userResponse.setName(user.getName());
        userResponse.setAge(user.getAge());
        userResponse.setEmail(user.getEmail());
        userResponse.setNumber(user.getNumber());
        userResponse.setStreet(user.getStreet());
        userResponse.setZipCode(user.getZipCode());
    }

    public void finalize() {
        user = null;
    }

    @Test
    public void shouldCreateNewUser() {
        doNothing().when(dao).save(user);
        when(encoder.encode(user.getPassword())).thenReturn(user.getPassword());
        service.save(user);

        verify(dao, times(1)).save(user);
    }

    @Test
    public void whenNotNull_FindByName_ShouldReturnUserInfo() {
        when(dao.findByName(user.getName())).thenReturn(Optional.of(user));
        when(mapper.map(any(), any())).thenReturn(userResponse);
        UserInfoResponse found = service.findByName(user.getName());

        Assert.assertEquals(user.getStreet(), found.getStreet());
        verify(dao).findByName(user.getName());
    }

    @Test
    public void whenNameNotFound_ShouldReturnExceptionAndMEssage() {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("User Arlindo Not Found");
        service.findByName("Arlindo");
    }

    @Test
    public void whenNotNull_FindByEmail_ShouldReturnUserInfo() {
        when(dao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mapper.map(any(), any())).thenReturn(userResponse);
        UserInfoResponse found = service.findByEmail(user.getEmail());

        Assert.assertEquals(user.getStreet(), found.getStreet());
        verify(dao).findByEmail(user.getEmail());
    }

    @Test
    public void whenEmailNotFound_ShouldReturnExceptionAndMEssage() {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("User arlindo@arlindo.com Not Found");
        service.findByEmail("arlindo@arlindo.com");
    }
}

