package com.portal.news;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.news.DAO.UserDAO;
import com.portal.news.DTO.*;
import com.portal.news.RestControllers.UserCotroller;
import com.portal.news.Secuirty.JwtFilter;
import com.portal.news.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(UserCotroller.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	UserDAO userDAO;
	@SpyBean
	UserService userService;
	@MockBean
	JwtFilter jwtFilter;
	@Test
	@WithMockUser(roles = "ADMIN")
	public void RegistrationTest_Status_Ok() throws Exception {
		RegDTO regDTO = new RegDTO("String", "String", "string@mail.ru", "password");
		when(userDAO.registration(regDTO)).thenReturn(new ResponseEntity<>(new IdDTO(1L), HttpStatus.OK));
		mockMvc.perform(post("/api/users/reg")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(regDTO))
						.characterEncoding("utf-8"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(new IdDTO(1L))))
				.andReturn();
	}
	@Test
	@WithMockUser(roles = "ADMIN")
	public void LoginTest_Status_Ok() throws Exception {
		LoginDTO loginDTO = new LoginDTO("Email", "Password");
		when(userDAO.login(loginDTO)).thenReturn(new ResponseEntity<>(new IdDTO(1L), HttpStatus.OK));
		mockMvc.perform(post("/api/users/login")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loginDTO))
						.characterEncoding("utf-8"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(new IdDTO(1L))))
				.andReturn();
	}
	@Test
	@WithMockUser(roles = "ADMIN")
	public void GetUserInfo_Status_OK() throws Exception {
		Long id = 1L;
		UserInfoDTO userInfoDTO = new UserInfoDTO(id, "Email", "Name", "Surname");
		when(userDAO.getUserInfo(1L)).thenReturn(new ResponseEntity<>(userInfoDTO, HttpStatus.OK));
		mockMvc.perform(get("/api/users/getInfo/" + id))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(userInfoDTO)))
				.andReturn();
	}
}