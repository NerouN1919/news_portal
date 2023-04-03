package com.portal.news;

import com.portal.news.DAO.CommentsDAO;
import com.portal.news.DAO.PostsDAO;
import com.portal.news.DAO.UserDAO;
import com.portal.news.DTO.*;
import com.portal.news.Errors.Failed;
import com.portal.news.Secuirty.JwtFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan("com.portal.news")
public class CommentsDaoTest {
    @Autowired
    CommentsDAO commentsDAO;
    @Autowired
    PostsDAO postsDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    JwtFilter jwtFilter;
    @Test
    public void addCommentShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment"));
        List<?> list = commentsDAO.getComments(0L, 1L, 1L).getBody();
        assert list != null;
        assert list.size() == 2;
        Assertions.assertEquals(list.get(1), new IdForNextDTO(null));
    }
    @Test
    public void addCommentBadRequest() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        boolean isSuccess = false;
        try {
            commentsDAO.addComment(new AddCommentDTO(20L, 1L, "comment"));
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "No such post or user");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            commentsDAO.addComment(new AddCommentDTO(1L, 20L, "comment"));
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "No such post or user");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void howManyCommentsShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        for (int i = 0; i < 5; i++) {
            commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment"));
        }
        Assertions.assertEquals(commentsDAO.howManyComments(1L).getBody(), new HowManyDTO(5L));
    }
    @Test
    public void howManyCommentsBadRequest(){
        boolean isSuccess = false;
        try {
            commentsDAO.howManyComments(1L);
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "Doesnt have such post");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void getCommentsShouldReturnGood() throws IOException{
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        for (int i = 0; i < 5; i++) {
            commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment"));
        }
        List<?> list = commentsDAO.getComments(0L, 3L, 1L).getBody();
        assert list != null;
        assert list.size() == 4;
        Assertions.assertEquals((IdForNextDTO)list.get(3), new IdForNextDTO(1L));
    }
    @Test
    public void getCommentsBadRequest() throws IOException{
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        for (int i = 0; i < 5; i++) {
            commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment"));
        }
        boolean isSuccess = false;
        try {
            commentsDAO.getComments(-2L, 3L, 1L);
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Bad from id");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            commentsDAO.getComments(0L, -3L, 1L);
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Bad howMuch");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            commentsDAO.getComments(0L, 3L, 20L);
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "No such post");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void deleteCommentShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        for (int i = 0; i < 5; i++) {
            commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment " + i));
        }
        commentsDAO.deleteComment(1L);
        Assertions.assertEquals(Objects.requireNonNull(commentsDAO.howManyComments(1L).getBody()).getSize(),
                4);
    }
    @Test
    public void deleteCommentBadRequest() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        for (int i = 0; i < 5; i++) {
            commentsDAO.addComment(new AddCommentDTO(1L, 1L, "comment " + i));
        }
        boolean isSuccess = false;
        try {
            commentsDAO.deleteComment(6L);
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "Bad comment id");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
}
