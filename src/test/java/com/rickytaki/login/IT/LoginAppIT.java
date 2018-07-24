package com.rickytaki.login.IT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickytaki.login.LoginApplication;
import com.rickytaki.login.config.WebSecurityConfig;
import com.rickytaki.login.model.UserInfo;
import com.rickytaki.login.response.UserInfoResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LoginApplication.class, WebSecurityConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginAppIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private UserInfo user;

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void init() {

        user = new UserInfo();
        user.setEmail("ittest@controller.com");
        user.setPassword("itTest123");
        user.setAge(44);
        user.setName("itTest");
        user.setZipCode("44444");
        user.setStreet("ItTest St");
        user.setNumber(44);
    }

    @Test
    @Sql(scripts = "classpath:schema.sql",
            config = @SqlConfig(transactionMode = ISOLATED),
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:finalize.sql",
            config = @SqlConfig(transactionMode = ISOLATED),
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void whenCreateUser_ShouldReturnCreated() throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonMapper.writeValueAsString(user),headers);
        Assert.assertThat(restTemplate.exchange("/create", HttpMethod.POST, request,String.class)
                .getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    @Sql(scripts = "classpath:itData.sql",
        config = @SqlConfig(transactionMode = ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:finalize.sql",
        config = @SqlConfig(transactionMode = ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void whenGetUserByName_ShoudReturnUser() {

        UserInfoResponse foundUser = restTemplate.withBasicAuth("madruguinha@login.com", "madruguinha@123")
                .getForObject("/findByName/madruguinha", UserInfoResponse.class);

        Assert.assertEquals(foundUser.getStreet(), "Vila Chaves");
        Assert.assertEquals(foundUser.getEmail(), "madruguinha@login.com");

    }

    @Test
    @Sql(scripts = "classpath:itData.sql",
            config = @SqlConfig(transactionMode = ISOLATED),
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:finalize.sql",
            config = @SqlConfig(transactionMode = ISOLATED),
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void whenGetUserByEmail_ShoudReturnUser() {

        UserInfoResponse foundUser = restTemplate.withBasicAuth("madruguinha@login.com", "madruguinha@123")
                .getForObject("/findByEmail/madruguinha@login.com", UserInfoResponse.class);

        Assert.assertEquals(foundUser.getStreet(), "Vila Chaves");
        Assert.assertEquals(foundUser.getEmail(), "madruguinha@login.com");

    }
}
