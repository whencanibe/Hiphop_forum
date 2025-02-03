package org.whencanibe.crudforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByTitle(String title);
}