package com.portal.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.news.DAO.CommentsDAO;
import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.HowManyDTO;
import com.portal.news.DTO.IdForNextDTO;
import com.portal.news.DTO.ReturnedCommentDTO;
import com.portal.news.RestControllers.CommentsCotroller;
import com.portal.news.Secuirty.JwtFilter;
import com.portal.news.Services.CommentsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsCotroller.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    JwtFilter jwtFilter;
    @MockBean
    CommentsDAO commentsDAO;
    @SpyBean
    CommentsService commentsService;
    @Test
    @WithMockUser(roles = "ADMIN")
    public void AddCommentTest_Status_Ok() throws Exception {
        AddCommentDTO addCommentDTO = new AddCommentDTO(1L, 1L, "Comment");
        doNothing().when(commentsDAO).addComment(addCommentDTO);
        mockMvc.perform(post("/api/comments/addComment")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(addCommentDTO))
                    .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void GetCommentsTest_Status_Ok() throws Exception {
        List<?> response = Arrays.asList(new ReturnedCommentDTO("CommentFirst", 1L, 1L, new Date()),
                new ReturnedCommentDTO("CommentSecond", 1L, 2L, new Date()),
                new IdForNextDTO(1L));
        when(commentsDAO.getComments(0L, 2L, 1L)).thenReturn(
                new ResponseEntity<>(response, HttpStatus.OK));
        mockMvc.perform(get("/api/comments/getComments/0/2/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void HowManyComments_Status_Ok() throws Exception {
        HowManyDTO howMany = new HowManyDTO(4L);
        when(commentsDAO.howManyComments(1L))
                .thenReturn(new ResponseEntity<>(howMany, HttpStatus.OK));
        mockMvc.perform(get("/api/comments/howMany/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(howMany)))
                .andReturn();
    }
}
