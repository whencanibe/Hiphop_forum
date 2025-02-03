package org.whencanibe.crudforum.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.whencanibe.crudforum.config.CustomUserDetails;
import org.whencanibe.crudforum.domain.Comment;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.repository.UserRepository;
import org.whencanibe.crudforum.service.CommentService;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String createComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                Long postId, String content){

        commentService.createComment(userDetails.getId(),postId,content);
        return "redirect:/posts/" + postId;
    }

    // 3. 댓글 수정
    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable Long commentId,
                                @RequestParam String content) {
        Comment updatedComment = commentService.updateComment(commentId, content);
        return "redirect:/posts/" + updatedComment.getPost().getId(); // 게시글 상세 페이지로 리다이렉트
    }

    // 4. 댓글 삭제
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId) {
        Long postId = commentService.getCommentById(commentId).getPost().getId();
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId; // 게시글 상세 페이지로 리다이렉트
    }
}
