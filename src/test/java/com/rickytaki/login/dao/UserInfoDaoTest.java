package com.rickytaki.login.dao;

import com.rickytaki.login.model.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@PropertySource("application-test.properties")
@JdbcTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ContextConfiguration(classes = {UserInfoDaoImpl.class})
public class UserInfoDaoTest {

    @Autowired
    private UserInfoDao dao;

    @Test
    public void whenNotNull_ShouldSaveAndRetrieve() {
        UserInfo user = new UserInfo();
        user.setName("testing");
        user.setAge(22);
        user.setEmail("testing@testing.com");
        user.setPassword("testPass");
        user.setStreet("Street Test");
        user.setNumber(22);
        user.setZipCode("01002622");

        dao.save(user);

        Assert.assertEquals(user.getName(), dao.findByName(user.getName()).get().getName());
    }

    @Test(expected = NullPointerException.class)
    public void whenNull_ShouldThrowNPE () {
        dao.save(null);
    }

    @Test
    public void shouldGetUserByName() {
        Assert.assertEquals("test", dao.findByName("test").get().getName());
    }

    @Test
    public void shouldGetByEmail() {
        Assert.assertEquals("test", dao.findByEmail("test@test.com").get().getName());
    }

}
