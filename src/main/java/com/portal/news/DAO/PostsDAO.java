package com.portal.news.DAO;

import com.portal.news.DTO.*;
import com.portal.news.DataBase.Posts;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import com.portal.news.FileWork.FileDownloadUtil;
import com.portal.news.FileWork.FileUploadResponse;
import com.portal.news.FileWork.FileUploadUtil;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;

@Repository
public class PostsDAO {
    @Autowired
    private EntityManager entityManager;
    public ResponseEntity<Posts> addPost(AddPostDTO addPostDTO){
        Session session = entityManager.unwrap(Session.class);
        if(addPostDTO.getTitle().length() < 5 || addPostDTO.getTitle().length() > 1000){
            throw new Failed("Wrong length of title");
        }
        Posts posts = new Posts(addPostDTO.getImagePath(), addPostDTO.getTitle());
        session.save(posts);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    public ResponseEntity<?> downloadImage(String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode, "Images");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
    public ResponseEntity<UploadDTO> uploadImageToPost(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();
        String filecode = FileUploadUtil.saveFile(fileName, multipartFile, "Images");
        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/downloadFile/" + filecode);
        return new ResponseEntity<>(new UploadDTO(filecode), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> like(LikeDTO likeDTO){
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        for(Posts in : users.getLikedPosts()){
            if(in.equals(posts)){
                throw new Failed("Already liked");
            }
        }
        users.addLike(posts);
        posts.setLike(Long.valueOf(posts.getLikes().size()));
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> unlike(LikeDTO likeDTO){
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        boolean find = false;
        for(Posts in : users.getLikedPosts()){
            if(in.equals(posts)){
                find = true;
            }
        }
        if(!find){
            throw new Failed("No such like");
        }
        users.removeLike(posts);
        posts.setLike(Long.valueOf(posts.getLikes().size()));
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public GetPostDTO getPost(Long id){
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, id);
        return new GetPostDTO(posts.getId(), posts.getDate(), posts.getLike(), posts.getTitle(), posts.getPathToPhoto());
    }
}
