package org.whencanibe.crudforum.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int viewCount;

    private int likeCount;

    private String imagePath;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Builder
    public Post(String title, String content, User user){
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void increaseViewCount(){
        this.viewCount += 1;
    }
    public void increaseLike(){this.likeCount += 1;}
    public void decreaseLike(){this.likeCount -= 1;}

    public String saveImageFile(MultipartFile file) {
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

        this.imagePath = uploadDir + newFileName;
        // 저장 경로
        return newFileName;
    }
}
