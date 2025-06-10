package com.algaworks.algacomments.comment.service.api.controller;

import com.algaworks.algacomments.comment.service.api.model.CommentInput;
import com.algaworks.algacomments.comment.service.api.model.CommentOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentOutput generateComment(@RequestBody CommentInput input) {

        return CommentOutput.builder()
                .build();
    }

}
