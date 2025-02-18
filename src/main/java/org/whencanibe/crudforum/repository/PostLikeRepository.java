package org.whencanibe.crudforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whencanibe.crudforum.domain.PostLike;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 특정 게시글에 user가 좋아요했는지 여부 확인
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    // 게시글의 좋아요 갯수 구하기
    // (매번 count 쿼리를 하고 싶다면)
    long countByPostId(Long postId);
}
