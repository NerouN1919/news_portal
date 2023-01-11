package com.portal.news.RestControllers;

import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.GetCommentDTO;
import com.portal.news.DTO.HowManyCommentsDTO;
import com.portal.news.DTO.ReturnedCommentDTO;
import com.portal.news.Services.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("/api/comments")
public class CommentsCotroller {
    @Autowired
    private CommentsService commentsService;
    @PostMapping("/addComment")
    public void addComment(@RequestBody AddCommentDTO addCommentDTO) throws IOException {
        commentsService.addComment(addCommentDTO);
    }
    @PostMapping("/getComments")
    public ResponseEntity<List<ReturnedCommentDTO>> getComments(@RequestBody GetCommentDTO getCommentDTO){
        return commentsService.getComments(getCommentDTO);
    }
    @GetMapping("/howMany/{id}")
    public ResponseEntity<HowManyCommentsDTO> howManyComments(@PathVariable("id") Long id){
        return commentsService.howManyComments(id);
    }
}
