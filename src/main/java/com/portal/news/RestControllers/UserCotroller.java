package com.portal.news.RestControllers;

import com.portal.news.DTO.IdDTO;
import com.portal.news.DTO.LoginDTO;
import com.portal.news.DTO.RegDTO;
import com.portal.news.DTO.UserInfoDTO;
import com.portal.news.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/users")
public class UserCotroller {
    @Autowired
    private UserService userService;
    @PostMapping("/reg")
    public ResponseEntity<IdDTO> registration(@RequestBody RegDTO regDTO){
        return userService.registration(regDTO);
    }
    @PostMapping("/login")
    public ResponseEntity<IdDTO> login(@RequestBody LoginDTO loginDTO){
        return userService.login(loginDTO);
    }
    @GetMapping("/getInfo/{id}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable("id") Long id){
        return userService.getUserInfo(id);
    }
}
