package org.whencanibe.crudforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.PostLike;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.PostLikeRepository;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean toggleLike(Long userId, Long postId) {
        // 1) userId, postId로 기존 좋아요 여부 확인
        Optional<PostLike> optionalLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        // 2) 만약 이미 눌러져 있다면 -> delete
        if (optionalLike.isPresent()) {
            PostLike postLike = optionalLike.get();

            postLike.unlike();
            postLike.getPost().removeLike(postLike);

            postLikeRepository.delete(postLike);

            return false; // false는 "좋아요가 취소됨"을 의미
        }
        // 3) 없으면 새로 생성 -> save
        else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            PostLike newLike = PostLike.builder()
                                    .user(user)
                                    .post(post)
                                    .build();
            // Post의 likeCount 증가 처리
            newLike.like();
            post.addLike(newLike);
            postLikeRepository.save(newLike);
            return true; // true는 "좋아요가 추가됨"을 의미
        }
    }
}