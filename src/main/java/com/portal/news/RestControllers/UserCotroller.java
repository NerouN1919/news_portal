package com.portal.news.RestControllers;

import com.portal.news.DTO.*;
import com.portal.news.Services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/users")
@CrossOrigin
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
    @GetMapping("/isAdmin/{id}")
    public ResponseEntity<IsAdminDTO> checkIsAdmin(@PathVariable("id") Long id){
        return userService.checkIsAdmin(id);
    }
}
