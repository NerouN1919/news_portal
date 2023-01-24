package com.portal.news.Services;

import com.portal.news.DAO.PostsDAO;
import com.portal.news.DTO.*;
import com.portal.news.DataBase.Posts;
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
    public ResponseEntity<Posts> addPost(AddPostDTO addPostDTO) throws IOException {
        return postsDAO.addPost(addPostDTO);
    }
    public ResponseEntity<UploadDTO> uploadImageToPost(MultipartFile multipartFile) throws IOException {
        return postsDAO.uploadImageToPost(multipartFile);
    }
    public ResponseEntity<?> downloadImage(String fileCode){
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
    public ResponseEntity<GetPostDTO> getPost(Long id){
        return postsDAO.getPost(id);
    }
    @Transactional
    public ResponseEntity<List<?>> getPosts(Long from, Long howMuch){
        return postsDAO.getPosts(from, howMuch);
    }
    @Transactional
    public ResponseEntity<HowManyDTO> howManyPosts(){
        return postsDAO.howManyPosts();
    }
}
