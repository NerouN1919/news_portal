package com.portal.news;

import com.portal.news.DAO.UserDAO;
import com.portal.news.DTO.LoginDTO;
import com.portal.news.DTO.RegDTO;
import com.portal.news.DTO.UserInfoDTO;
import com.portal.news.Errors.Failed;
import com.portal.news.Secuirty.JwtFilter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan("com.portal.news")

public class UserDaoTest {
    @Autowired
    UserDAO userDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    JwtFilter jwtFilter;
    @Test
    public void getInfoNoSuchUser() {
        userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
        userDAO.registration(new RegDTO("Second", "User", "second_user@mail.ru", "Password"));
        boolean isSuccess = false;
        try {
            userDAO.getUserInfo(10L);
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "No such id");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void getInfoShouldReturnGood() {
        userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
        userDAO.registration(new RegDTO("Second", "User", "second_user@mail.ru", "Password"));
        UserInfoDTO userInfoDTO = userDAO.getUserInfo(1L).getBody();
        assert userInfoDTO != null;
        Assertions.assertEquals(userInfoDTO.getName(), "First");
        Assertions.assertEquals(userInfoDTO.getEmail(), "first_user@mail.ru");
        Assertions.assertEquals(userInfoDTO.getSurname(), "User");
        Assertions.assertEquals(userInfoDTO.getId(), 1L);
        userInfoDTO = userDAO.getUserInfo(2L).getBody();
        assert userInfoDTO != null;
        Assertions.assertNotEquals(userInfoDTO.getName(), "First");
        Assertions.assertNotEquals(userInfoDTO.getEmail(), "first_user@mail.ru");
        Assertions.assertEquals(userInfoDTO.getSurname(), "User");
        Assertions.assertNotEquals(userInfoDTO.getId(), 1L);
    }
    @Test
    public void registrationShouldReturnGood() {
        Assertions.assertEquals(userDAO.registration(
                new RegDTO("First", "User", "first_user@mail.ru", "Password"))
                .getBody().getId(), 1L);
    }
    @Test
    public void registrationBadRequest(){
        boolean isSuccess = false;
        try{
            userDAO.registration(new RegDTO("first", "User", "first_user@mail.ru", "Password"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Bad name");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try{
            userDAO.registration(new RegDTO("First", "user", "first_user@mail.ru", "Password"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Bad surname");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try{
            userDAO.registration(new RegDTO("First", "User", "first_user", "Password"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Bad email");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try{
            userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
            userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Exists this email");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void loginShouldReturnGood(){
        userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
        Assertions.assertEquals(Objects.requireNonNull(userDAO.login(new LoginDTO("first_user@mail.ru", "Password"))
                .getBody()).getId(), 1L);
    }
    @Test
    public void loginBadLogin(){
        userDAO.registration(new RegDTO("First", "User", "first_user@mail.ru", "Password"));
        boolean isSuccess = false;
        try{
            userDAO.login(new LoginDTO("bad_login@mail.ru", "Password"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Dont have such email");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try{
            userDAO.login(new LoginDTO("first_user@mail.ru", "BadPassword"));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Wrong password");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
}
