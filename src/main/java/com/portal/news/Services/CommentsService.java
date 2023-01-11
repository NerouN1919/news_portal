package com.portal.news.Services;

import com.portal.news.DAO.CommentsDAO;
import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.GetCommentDTO;
import com.portal.news.DTO.HowManyCommentsDTO;
import com.portal.news.DTO.ReturnedCommentDTO;
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
    public ResponseEntity<List<ReturnedCommentDTO>> getComments(GetCommentDTO getCommentDTO){
        return commentsDAO.getComments(getCommentDTO);
    }
    @Transactional
    public ResponseEntity<HowManyCommentsDTO> howManyComments(Long id){
        return commentsDAO.howManyComments(id);
    }
}
