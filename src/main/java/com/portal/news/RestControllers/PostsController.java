package com.portal.news.RestControllers;

import com.portal.news.DTO.*;
import com.portal.news.DataBase.Posts;
import com.portal.news.Services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;
    @PostMapping("/addPost")
    public ResponseEntity<GetPostDTO> addPost(@RequestBody AddPostDTO addPostDTO) throws IOException {
        return postsService.addPost(addPostDTO);
    }
    @PostMapping("/uploadImageToPost")
    public ResponseEntity<UploadDTO> uploadImageToPost(@RequestParam("file") MultipartFile multipartFile) throws IOException{
        return postsService.uploadImageToPost(multipartFile);
    }
    @GetMapping("/downloadImage/{fileCode}")
    public ResponseEntity<Object> downloadImage(@PathVariable("fileCode") String fileCode) {
        return postsService.downloadImage(fileCode);
    }
    @PutMapping("/like")
    public ResponseEntity<ResultLikeDTO> like(@RequestBody LikeDTO likeDTO){
        return postsService.like(likeDTO);
    }
    @PutMapping("/unlike")
    public ResponseEntity<ResultLikeDTO> unlike(@RequestBody LikeDTO likeDTO){
        return postsService.unlike(likeDTO);
    }
    @GetMapping("/getPost/{id}")
    public ResponseEntity<GetPostDTO> getPost(@PathVariable("id") Long id){
        return postsService.getPost(id);
    }
    @GetMapping("/getPosts/{from}/{howMuch}")
    public ResponseEntity<List<?>> getPosts(@PathVariable("howMuch") Long howMuch, @PathVariable("from") Long from){
        return postsService.getPosts(from, howMuch);
    }
    @GetMapping("howMany")
    public ResponseEntity<HowManyDTO> howManyPosts(){
        return postsService.howManyPosts();
    }
}
