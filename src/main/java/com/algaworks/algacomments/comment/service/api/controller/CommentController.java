package com.algaworks.algacomments.comment.service.api.controller;

import com.algaworks.algacomments.comment.service.api.client.ModerationClient;
import com.algaworks.algacomments.comment.service.api.model.CommentInput;
import com.algaworks.algacomments.comment.service.api.model.CommentOutput;
import com.algaworks.algacomments.comment.service.api.model.ModerationInput;
import com.algaworks.algacomments.comment.service.api.model.ModerationOutput;
import com.algaworks.algacomments.comment.service.api.model.PageResponseOutput;
import com.algaworks.algacomments.comment.service.domain.model.Comment;
import com.algaworks.algacomments.comment.service.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final ModerationClient moderationClient;
    private final CommentRepository commentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentOutput generateComment(@RequestBody CommentInput input) {

        UUID uuid = UUID.randomUUID();

        ModerationInput moderationInput = ModerationInput.builder()
                .commentId(uuid.toString())
                .text(input.getText())
                .build();

        ModerationOutput moderationOutput = moderationClient.validateComment(moderationInput);

        log.info("Moderation result for comment was approved?: {}", moderationOutput.getApproved());
        Comment commentSaved = null;
        if (Boolean.TRUE.equals(moderationOutput.getApproved())) {
            Comment comment = Comment.builder()
                    .id(moderationOutput.getCommentId())
                    .text(input.getText())
                    .author(input.getAuthor())
                    .registeredAt(OffsetDateTime.now())
                    .build();
            commentSaved = commentRepository.saveAndFlush(comment);
        } else {
            log.warn("Comment with id {} was not approved by moderation", uuid);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, moderationOutput.getReason());
        }

        return CommentOutput.builder()
                .id(commentSaved.getId())
                .author(commentSaved.getAuthor())
                .text(commentSaved.getText())
                .createdAt(commentSaved.getRegisteredAt())
                .build();
    }

    @GetMapping("{id}")
    public CommentOutput getOne(@PathVariable UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        return CommentOutput.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .text(comment.getText())
                .createdAt(comment.getRegisteredAt())
                .build();
    }

    @GetMapping
    public PageResponseOutput<CommentOutput> search(@PageableDefault Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);
        Page<CommentOutput> page = comments.map(this::convertToModel);

        return new PageResponseOutput<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private CommentOutput convertToModel(Comment comment) {
        return CommentOutput.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .text(comment.getText())
                .createdAt(comment.getRegisteredAt())
                .build();
    }
}
