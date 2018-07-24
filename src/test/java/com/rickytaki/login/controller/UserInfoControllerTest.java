package com.rickytaki.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickytaki.login.model.UserInfo;
import com.rickytaki.login.response.UserInfoResponse;
import com.rickytaki.login.service.UserInfoService;
import ma.glasnost.orika.MapperFacade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserInfoController.class)
public class UserInfoControllerTest {

    @MockBean
    private UserInfoService service;

    @Autowired
    MockMvc mvc;

    private ObjectMapper jsonMapper = new ObjectMapper();

    private UserInfoController controller;

    private UserInfo user;

    private UserInfoResponse response;

    @Before
    public void init(){
        this.controller = new UserInfoController(service);

        this.user = new UserInfo();
        user.setEmail("controller@controller.com");
        user.setPassword("controller123");
        user.setAge(33);
        user.setName("controller");

        user.setZipCode("333333");
        user.setStreet("Controller St");
        user.setNumber(33);

        this.response = new UserInfoResponse();
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setName(user.getName());
        response.setStreet(user.getStreet());
        response.setNumber(user.getNumber());
        response.setZipCode(user.getZipCode());;
    }

    @Test
    @WithMockUser
    public void whenNotNull_ShouldCreateUser() throws Exception{
        doNothing().when(service).save(any());
        mvc.perform(post("/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
        verify(service, times(1)).save(user);
    }

    @Test
    @WithMockUser
    public void givenUser_FindByName_ShouldRetrieve() throws Exception{
        when(service.findByName(user.getName())).thenReturn(response);
        mvc.perform(get("/findByName/controller"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("controller")));
        Assert.assertEquals(user.getName(), controller.findByName(user.getName()).getBody().getName());
    }

    @Test
    @WithMockUser
    public void givenUser_FindByEmail_ShouldRetrieve() throws Exception{
        when(service.findByEmail(user.getEmail())).thenReturn(response);
        mvc.perform(get("/findByEmail/controller@controller.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("controller")));
        Assert.assertEquals(user.getName(), controller.findByEmail(user.getEmail()).getBody().getName());
    }
}
