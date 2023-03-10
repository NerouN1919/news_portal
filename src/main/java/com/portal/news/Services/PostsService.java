package com.portal.news.Services;

import com.portal.news.DAO.PostsDAO;
import com.portal.news.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PostsService {
    @Autowired
    private PostsDAO postsDAO;
    public ResponseEntity<GetPostDTO> addPost(AddPostDTO addPostDTO) throws IOException {
        return postsDAO.addPost(addPostDTO);
    }
    public ResponseEntity<UploadDTO> uploadImageToPost(MultipartFile multipartFile) throws IOException {
        return postsDAO.uploadImageToPost(multipartFile);
    }
    public ResponseEntity<Object> downloadImage(String fileCode){
        return postsDAO.downloadImage(fileCode);
    }
    @Transactional
    public ResponseEntity<ResultLikeDTO> like(LikeDTO likeDTO){
        return postsDAO.like(likeDTO);
    }
    @Transactional
    public ResponseEntity<ResultLikeDTO> unlike(LikeDTO likeDTO){
        return postsDAO.unlike(likeDTO);
    }
    @Transactional
    public ResponseEntity<GetPostDTO> getPost(Long post_id, Long user_id){
        return postsDAO.getPost(post_id, user_id);
    }
    @Transactional
    public ResponseEntity<List<?>> getPosts(Long from, Long howMuch, Long user_id){
        return postsDAO.getPosts(from, howMuch, user_id);
    }
    @Transactional
    public ResponseEntity<HowManyDTO> howManyPosts(){
        return postsDAO.howManyPosts();
    }
    @Transactional
    public void deletePost(Long id){
        postsDAO.deletePost(id);
    }
    @Transactional
    public void updatePost(EditPostDTO editPostDTO){
        postsDAO.updatePost(editPostDTO);
    }
}
