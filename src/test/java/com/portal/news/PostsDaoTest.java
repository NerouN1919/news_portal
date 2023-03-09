package com.portal.news;

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
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan("com.portal.news")
public class PostsDaoTest {
    @Autowired
    PostsDAO postsDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    JwtFilter jwtFilter;
    @Test
    public void addPostShouldReturnGood() throws IOException {
        GetPostDTO getPostDTO = postsDAO.addPost(
                new AddPostDTO("title", "text", "imagePath")).getBody();
        assert getPostDTO != null;
        Assertions.assertEquals(getPostDTO.getId(), 1L);
        Assertions.assertEquals(getPostDTO.getLikes(), 0L);
        Assertions.assertEquals(getPostDTO.getTitle(), "title");
        Assertions.assertEquals(getPostDTO.getText(), "text");
        Assertions.assertEquals(getPostDTO.getPathToPhoto(), "imagePath");
    }
    @Test
    public void addPostBadRequest() throws Exception {
        boolean isSuccess = false;
        try {
            postsDAO.addPost(new AddPostDTO("tt", "text", "imagePath"));
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "Wrong length of title");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void likeShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        ResultLikeDTO resultLikeDTO = postsDAO.like(new LikeDTO(1L, 1L)).getBody();
        assert resultLikeDTO != null;
        Assertions.assertEquals(resultLikeDTO.getLikes(), 1L);
        Assertions.assertEquals(resultLikeDTO.getPost_id(), 1L);
    }
    @Test
    public void likeBadRequest() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        boolean isSuccess = false;
        try {
            postsDAO.like(new LikeDTO(1L, 1L));
            postsDAO.like(new LikeDTO(1L, 1L));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Already liked");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            postsDAO.like(new LikeDTO(0L, 1L));
        } catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Doesnt have such user");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            postsDAO.like(new LikeDTO(1L, 0L));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Doesnt have such post");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void unlikeShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        postsDAO.like(new LikeDTO(1L, 1L));
        ResultLikeDTO resultLikeDTO = postsDAO.unlike(new LikeDTO(1L, 1L)).getBody();
        assert resultLikeDTO != null;
        Assertions.assertEquals(resultLikeDTO.getLikes(), 0L);
        Assertions.assertEquals(resultLikeDTO.getPost_id(), 1L);
    }
    @Test
    public void unlikeBadRequest() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        boolean isSuccess = false;
        try {
            postsDAO.unlike(new LikeDTO(1L, 1L));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "No such like");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            postsDAO.unlike(new LikeDTO(0L, 1L));
        } catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Doesnt have such user");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
        isSuccess = false;
        try {
            postsDAO.unlike(new LikeDTO(1L, 0L));
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "Doesnt have such post");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void getPostShouldReturnGood() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        userDAO.registration(new RegDTO("Name", "Surname", "email@mail.ru", "password"));
        postsDAO.like(new LikeDTO(1L, 1L));
        GetPostDTO getPostDTO = postsDAO.getPost(1L, 1L).getBody();
        assert getPostDTO != null;
        Assertions.assertEquals(getPostDTO.getText(), "text");
        Assertions.assertEquals(getPostDTO.getPathToPhoto(), "imagePath");
        Assertions.assertEquals(getPostDTO.getId(), 1L);
        Assertions.assertEquals(getPostDTO.getTitle(), "title");
        Assertions.assertEquals(getPostDTO.getLikes(), 1L);
        Assertions.assertTrue(getPostDTO.getIsLiked());
    }
    @Test
    public void getPostBadRequest() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        boolean isSuccess = false;
        try {
            postsDAO.getPost(4L, 0L);
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "Doesnt have such post");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
    @Test
    public void howManyPosts() throws IOException {
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        postsDAO.addPost(new AddPostDTO("title", "text", "imagePath"));
        HowManyDTO howManyDTO = postsDAO.howManyPosts().getBody();
        assert howManyDTO != null;
        Assertions.assertEquals(howManyDTO.getSize(), 3L);
    }
    @Test
    public void getPostsShouldReturnGood() throws IOException {
        AddPostDTO addPostDTO = new AddPostDTO("title", "text", "imagePath");
        postsDAO.addPost(addPostDTO);
        postsDAO.addPost(addPostDTO);
        postsDAO.addPost(addPostDTO);
        List<?> list = postsDAO.getPosts(0L, 2L, 0L).getBody();
        assert list != null;
        assert list.size() == 3;
        Assertions.assertEquals((GetPostDTO)list.get(0), postsDAO.getPost(1L, 0L).getBody());
        Assertions.assertEquals((IdForNextDTO)list.get(2), new IdForNextDTO(3L));
    }
    @Test
    public void getPostsBadRequest() throws IOException {
        AddPostDTO addPostDTO = new AddPostDTO("title", "text", "imagePath");
        postsDAO.addPost(addPostDTO);
        postsDAO.addPost(addPostDTO);
        postsDAO.addPost(addPostDTO);
        boolean isSuccess = false;
        try {
            postsDAO.getPosts(0L, 10L, 0L);
        } catch (Failed e) {
            Assertions.assertEquals(e.getMessage(), "No have so many posts");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
         isSuccess = false;
         try {
             postsDAO.getPosts(-1L, 2L, 0L);
         } catch (Failed e) {
             Assertions.assertEquals(e.getMessage(), "Bad from id");
             isSuccess = true;
         }
         Assertions.assertTrue(isSuccess);
    }
    @Test
    public void uploadImageToPostShouldReturnGood() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some xml".getBytes());
        UploadDTO uploadDTO = postsDAO.uploadImageToPost(multipartFile).getBody();
        Path uploadPath = Paths.get("Images");
        assert uploadDTO != null;
        Path filePath = uploadPath.resolve(uploadDTO.getPath()+"-filename.jpg");
        System.out.println(filePath);
        Assertions.assertTrue(Files.exists(filePath));
    }
    @Test
    public void downloadImageShouldReturnGood() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some xml".getBytes());
        UploadDTO uploadDTO = postsDAO.uploadImageToPost(multipartFile).getBody();
        Path uploadPath = Paths.get("Images");
        assert uploadDTO != null;
        Path filePath = uploadPath.resolve(uploadDTO.getPath()+"-filename.jpg");
        Resource resource = (Resource) postsDAO.downloadImage(uploadDTO.getPath()).getBody();
        assert resource != null;
        Assertions.assertEquals(filePath.getFileName().toString(), resource.getFilename());
    }
    @Test
    public void downloadImageBadRequest() throws IOException {
        boolean isSuccess = false;
        try {
            postsDAO.downloadImage("failedCode");
        } catch (Failed e){
            Assertions.assertEquals(e.getMessage(), "File not found");
            isSuccess = true;
        }
        Assertions.assertTrue(isSuccess);
    }
}
