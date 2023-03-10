package com.portal.news.RestControllers;

import com.portal.news.DTO.AddCommentDTO;
import com.portal.news.DTO.HowManyDTO;
import com.portal.news.Services.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/comments")
@CrossOrigin
public class CommentsCotroller {
    @Autowired
    private CommentsService commentsService;
    @PostMapping("/addComment")
    public void addComment(@RequestBody AddCommentDTO addCommentDTO) throws IOException {
        commentsService.addComment(addCommentDTO);
    }
    @GetMapping("/getComments/{from}/{howMany}/{post_id}")
    public ResponseEntity<List<?>> getComments(@PathVariable("from") Long from,
                                               @PathVariable("howMany") Long howMany,
                                               @PathVariable("post_id") Long postId){
        return commentsService.getComments(from, howMany, postId);
    }
    @GetMapping("/howMany/{id}")
    public ResponseEntity<HowManyDTO> howManyComments(@PathVariable("id") Long id){
        return commentsService.howManyComments(id);
    }
    @DeleteMapping("/deleteComment/{id}")
    public void deleteComment(@PathVariable("id") Long id){
        commentsService.deleteComment(id);
    }
}
