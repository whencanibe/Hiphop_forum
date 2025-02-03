package org.whencanibe.crudforum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whencanibe.crudforum.domain.Comment;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.CommentRepository;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment createComment(Long userId, Long postId, String content){
        Comment comment = new Comment();

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("댓글 작성자를 찾을 수 없습니다."));

        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        // JPA save : 데이터를 db에 저장하고, 저장된 엔티티 객체를 반환
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("Comment not found"));
    }

    // 2. 특정 게시글의 모든 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment updateComment(Long commentId, String content){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("Comment not found"));
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId){
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}
