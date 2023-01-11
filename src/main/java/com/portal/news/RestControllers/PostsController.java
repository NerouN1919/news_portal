package com.portal.news.RestControllers;

import com.portal.news.DTO.*;
import com.portal.news.DataBase.Posts;
import com.portal.news.Services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;
    @PostMapping("/addPost")
    public ResponseEntity<Posts> addPost(@RequestBody AddPostDTO addPostDTO){
        return postsService.addPost(addPostDTO);
    }
    @PostMapping("/uploadImageToPost")
    public ResponseEntity<UploadDTO> uploadImageToPost(@RequestParam("file") MultipartFile multipartFile) throws IOException{
        return postsService.uploadImageToPost(multipartFile);
    }
    @GetMapping("/downloadImage/{fileCode}")
    public ResponseEntity<?> downloadImage(@PathVariable("fileCode") String fileCode) {
        return postsService.downloadImage(fileCode);
    }
    @PostMapping("/like")
    public ResponseEntity<ResultLikeDTO> like(@RequestBody LikeDTO likeDTO){
        return postsService.like(likeDTO);
    }
    @PostMapping("/unlike")
    public ResponseEntity<ResultLikeDTO> unlike(@RequestBody LikeDTO likeDTO){
        return postsService.unlike(likeDTO);
    }
    @GetMapping("/getPost/{id}")
    public GetPostDTO getPost(@PathVariable("id") Long id){
        return postsService.getPost(id);
    }
}
