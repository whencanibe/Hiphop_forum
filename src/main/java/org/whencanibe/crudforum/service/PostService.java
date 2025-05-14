package org.whencanibe.crudforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.dto.PostDto;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // (1) 페이지네이션 + DTO 변환 메서드
    public Page<PostDto> getAllPostsAsDto(int page, int size) {
        // 0-based 페이지 인덱스, 정렬은 필요 시 변경 (예: id 내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());

        // DB에서 Page<Post> 조회
        Page<Post> postPage = postRepository.findAll(pageRequest);

        // Page<Post> -> Page<PostDto> 로 매핑
        // 스프링 데이터 JPA의 'map' 기능 이용 (Page.map())

        return postPage.map(this::convertToDto);
    }

    // (2) Post -> PostDto 변환 로직
    private PostDto convertToDto(Post post) {
        String userName;
        // 작성자 이름
        if (post.getUser() != null) {
            userName = post.getUser().getUsername();
        } else {
            userName = "(알 수 없음)";
        }

        String createdAt;
        // createdAt -> "yyyy-MM-dd" 포맷의 문자열
        if (post.getCreatedDate() != null) {
            createdAt = post.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            createdAt = "";
        }

        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .title(post.getTitle())
                .createdDate(createdAt)
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build();
    }

    //조회수도 +1 올라감.
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));

        post.increaseViewCount();
        // 즉시 DB 반영을 원한다면 save 호출 (JPA 영속성 컨텍스트에 의해 자동 반영되는 경우도 있음)
        postRepository.save(post);
        return post;
    }

    public Post createPost(Long userId ,String title , String content, MultipartFile imageFile) {

        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("작성자를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        if (imageFile != null && !imageFile.isEmpty()) {
            post.saveImageFile(imageFile);
        }
        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long id, String title, String content, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시물 없음"));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        post.edit(title, content);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
