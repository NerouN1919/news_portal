package com.portal.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.portal.news.Secuirty.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JwtControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @SpyBean
    AuthService authService;
    @Autowired
    JwtFilter jwtFilter;
    @SpyBean
    UserServiceJwt userServiceJwt;
    @SpyBean
    JwtProvider jwtProvider;
    @Test
    public void loginShouldReturnGood() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("SviridenkoAdmin", "12345");
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtRequest))
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn();
    }
    @Test
    public void loginBadRequest() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("SviridenkoAdmin", "BadPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(jwtRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
    @Test
    public void tokenShouldReturnGood() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("SviridenkoAdmin", "12345");
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(jwtRequest))
                        .characterEncoding("utf-8"))
                .andReturn();
        String refresh = JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.refreshToken");
        mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new RefreshJwtRequest(refresh)))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isEmpty())
                .andReturn();
    }
    @Test
    public void refreshShouldReturnGood() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("SviridenkoAdmin", "12345");
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(jwtRequest))
                        .characterEncoding("utf-8"))
                .andReturn();
        String refresh = JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.refreshToken");
        String access = JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.accessToken");
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new RefreshJwtRequest(refresh)))
                        .characterEncoding("utf-8")
                        .header("Authorization","Bearer " + access))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn();
    }
}
