package org.whencanibe.crudforum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final String uploadDir = System.getenv("UPLOAD_DIR");

    public PostService(PostRepository postRepository,UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Post createPost(Long userId ,String title , String content, MultipartFile imageFile) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);

        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("작성자를 찾을 수 없습니다."));
        post.setUser(user);

        if (imageFile != null && !imageFile.isEmpty()) {
            // 파일 저장 로직
            String storedFileName = saveImageFile(imageFile);
            // Post 엔티티에 이미지 경로 or 파일명 저장
            post.setImagePath(storedFileName);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private String saveImageFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 확장자 추출
        String ext = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);
        // 중복 방지용 UUID 사용
        String newFileName = UUID.randomUUID().toString() + "." + ext;

        // 실제 파일 경로
        File destination = new File(uploadDir, newFileName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            // 예외 처리(적절히 로그/에러처리)
            e.printStackTrace();
        }
        // DB에는 "newFileName" 또는 "static/img/newFileName"을 저장할 수도 있음
        return newFileName;
    }
}
