package com.portal.news.DAO;

import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.GetCommentDTO;
import com.portal.news.DTO.HowManyCommentsDTO;
import com.portal.news.DTO.ReturnedCommentDTO;
import com.portal.news.DataBase.Comments;
import com.portal.news.DataBase.Posts;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class CommentsDAO {
    @Autowired
    private EntityManager entityManager;
    public void addComment(AddCommentDTO addCommentDTO) throws IOException {
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, addCommentDTO.getPostId());
        Users users = session.get(Users.class, addCommentDTO.getUserId());
        if(posts == null || users == null){
            throw new Failed("No such post or user");
        }
        String fileCode = RandomStringUtils.randomAlphanumeric(25);
        List<String> parts = Arrays.asList(addCommentDTO.getComment().split("\n"));
        Path uploadPath = Paths.get("Comments");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileCode+".txt");
        Files.write(filePath, parts);
        Comments comments = new Comments(fileCode);
        posts.addComment(comments, users);
        session.save(posts);
        session.save(comments);
    }
    public ResponseEntity<HowManyCommentsDTO> howManyComments(Long id){
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, id);
        if (posts == null){
            throw new Failed("Not such post");
        }
        return new ResponseEntity<>(new HowManyCommentsDTO((long) posts.getComments().size()), HttpStatus.OK);
    }
    public ResponseEntity<List<ReturnedCommentDTO>> getComments(GetCommentDTO getCommentDTO){
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, getCommentDTO.getPostId());
        if(posts==null){
            throw new Failed("Not such post");
        }
        List<Comments> list = posts.getComments();
        if(getCommentDTO.getHowMuch() > list.size()){
            throw new Failed("Haven't such comments");
        }
        List<ReturnedCommentDTO> result = new ArrayList<>();
        for(int i = list.size()-1; i >= list.size() - getCommentDTO.getHowMuch(); i--){
            Comments in = list.get(i);
            ReturnedCommentDTO returnedCommentDTO = new ReturnedCommentDTO(in.getPost().getId(),
                    in.getUser().getId(), in.getDate());
            try {
                String content = Files.readString(Paths.get("Comments\\"+in.getHrefToComment()+".txt"));
                returnedCommentDTO.setComment(content);
                result.add(returnedCommentDTO);
            } catch (IOException e){
                throw new Failed("No such file");
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
