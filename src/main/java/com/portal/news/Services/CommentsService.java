package com.portal.news.Services;

import com.portal.news.DAO.CommentsDAO;
import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.HowManyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class CommentsService {
    @Autowired
    private CommentsDAO commentsDAO;
    @Transactional
    public void addComment(AddCommentDTO addCommentDTO) throws IOException {
        commentsDAO.addComment(addCommentDTO);
    }
    @Transactional
    public ResponseEntity<List<?>> getComments(Long from, Long howMany, Long postId){
        return commentsDAO.getComments(from, howMany, postId);
    }
    @Transactional
    public ResponseEntity<HowManyDTO> howManyComments(Long id){
        return commentsDAO.howManyComments(id);
    }
    @Transactional
    public void deleteComment(Long id){
        commentsDAO.deleteComment(id);
    }
}
