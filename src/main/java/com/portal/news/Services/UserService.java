package com.portal.news.Services;

import com.portal.news.DAO.UserDAO;
import com.portal.news.DTO.IdDTO;
import com.portal.news.DTO.LoginDTO;
import com.portal.news.DTO.RegDTO;
import com.portal.news.DTO.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Transactional
    public ResponseEntity<IdDTO> registration(RegDTO regDTO){
        return userDAO.registration(regDTO);
    }
    @Transactional
    public ResponseEntity<IdDTO> login(LoginDTO loginDTO){
        return userDAO.login(loginDTO);
    }
    @Transactional
    public ResponseEntity<UserInfoDTO> getUserInfo(Long id){
        return userDAO.getUserInfo(id);
    }
}
