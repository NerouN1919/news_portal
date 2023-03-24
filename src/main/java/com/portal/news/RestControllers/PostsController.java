package com.portal.news.RestControllers;

import com.portal.news.DTO.*;
import com.portal.news.Services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;import org.springframework.http.MediaType;


@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/posts")
@CrossOrigin
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
    @GetMapping(value = "/downloadImage/{fileCode}", produces = {MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE})
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
    @GetMapping("/getPost/{post_id}/{user_id}")
    public ResponseEntity<GetPostDTO> getPost(@PathVariable("post_id") Long post_id,
                                              @PathVariable("user_id") Long user_id){
        return postsService.getPost(post_id, user_id);
    }
    @GetMapping("/getPosts/{from}/{howMuch}/{user_id}")
    public ResponseEntity<List<?>> getPosts(@PathVariable("howMuch") Long howMuch, @PathVariable("from") Long from,
                                            @PathVariable("user_id") Long user_id){
        return postsService.getPosts(from, howMuch, user_id);
    }
    @GetMapping("howMany")
    public ResponseEntity<HowManyDTO> howManyPosts(){
        return postsService.howManyPosts();
    }
    @DeleteMapping("/deletePost/{id}")
    public void deletePost(@PathVariable("id") Long id){
        postsService.deletePost(id);
    }
    @PutMapping("/editPost")
    public void updatePost(@RequestBody EditPostDTO editPostDTO){
        postsService.updatePost(editPostDTO);
    }
}
