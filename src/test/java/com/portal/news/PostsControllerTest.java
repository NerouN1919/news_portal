package com.portal.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.news.DAO.PostsDAO;
import com.portal.news.DTO.*;
import com.portal.news.RestControllers.PostsController;
import com.portal.news.Secuirty.JwtFilter;
import com.portal.news.Services.PostsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PostsDAO postsDAO;
    @SpyBean
    PostsService postsService;
    @MockBean
    JwtFilter jwtFilter;
    @Test
    @WithMockUser(roles = "ADMIN")
    public void AddPostTest_Status_Ok() throws Exception {
        AddPostDTO addPostDTO = new AddPostDTO("Title", "Text", "ImagePath");
        GetPostDTO getPostDTO = new GetPostDTO(1L, new Date(), 0L, "Title",
                "ImagePath", "Text");
        when(postsDAO.addPost(addPostDTO)).thenReturn(new ResponseEntity<>(getPostDTO, HttpStatus.OK));
        mockMvc.perform(post("/api/posts/addPost")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addPostDTO))
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getPostDTO)))
                .andReturn();

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void downloadImageTest_Status_Ok() throws Exception {
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"Code";
        Resource resource = null;
        ResponseEntity<Object> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
        when(postsDAO.downloadImage("Code")).thenReturn(responseEntity);
        mockMvc.perform(get("/api/posts/downloadImage/Code"))
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getPostTest_Status_Ok() throws Exception {
        GetPostDTO getPostDTO = new GetPostDTO(1L, new Date(), 0L, "Title",
                "ImagePath", "Text");
        when(postsDAO.getPost(1L)).thenReturn(new ResponseEntity<>(getPostDTO, HttpStatus.OK));
        mockMvc.perform(get("/api/posts/getPost/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getPostDTO)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getPostsTest_Status_Ok() throws Exception {
        List<Object> result = new ArrayList<>();
        result.add(new GetPostDTO(1L, new Date(), 0L, "Title",
                "ImagePath", "Text"));
        result.add(new GetPostDTO(2L, new Date(), 0L, "Title",
                "ImagePath", "Text"));
        result.add(new IdForNextDTO(3L));
        when(postsDAO.getPosts(0L, 2L)).thenReturn(new ResponseEntity<>(result, HttpStatus.OK));
        mockMvc.perform(get("/api/posts/getPosts/0/2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void howManyPostsTest_Status_Ok() throws Exception {
        HowManyDTO howManyDTO = new HowManyDTO(5L);
        when(postsDAO.howManyPosts()).thenReturn(new ResponseEntity<>(howManyDTO, HttpStatus.OK));
        mockMvc.perform(get("/api/posts/howMany"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(howManyDTO)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void likeTest_Status_Ok() throws Exception {
        LikeDTO likeDTO = new LikeDTO(1L, 2L);
        ResultLikeDTO resultLikeDTO = new ResultLikeDTO(2L, 1L);
        when(postsDAO.like(likeDTO)).thenReturn(new ResponseEntity<>(resultLikeDTO, HttpStatus.OK));
        mockMvc.perform(put("/api/posts/like")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(likeDTO))
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resultLikeDTO)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void unlikeTest_Status_Ok() throws Exception {
        LikeDTO likeDTO = new LikeDTO(1L, 2L);
        ResultLikeDTO resultLikeDTO = new ResultLikeDTO(2L, 0L);
        when(postsDAO.unlike(likeDTO)).thenReturn(new ResponseEntity<>(resultLikeDTO, HttpStatus.OK));
        mockMvc.perform(put("/api/posts/unlike")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(likeDTO))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resultLikeDTO)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void uploadImageToPostTest_Status_Ok() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some xml".getBytes());
        UploadDTO uploadDTO = new UploadDTO("Path");
        when(postsDAO.uploadImageToPost(multipartFile)).thenReturn(new ResponseEntity<>(uploadDTO, HttpStatus.OK));
        mockMvc.perform(multipart("/api/posts/uploadImageToPost").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(uploadDTO)))
                .andReturn();
    }
}
