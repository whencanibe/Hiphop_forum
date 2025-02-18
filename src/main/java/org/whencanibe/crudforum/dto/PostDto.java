package org.whencanibe.crudforum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;

@Getter
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private User user;
    private String createdDate; // 이미 "yyyy-MM-dd" 형태로 포맷된 문자열을 담기 위함
    private int viewCount;

    @Builder
    public PostDto(Long id, String title, String content, User user, String createdDate, int viewCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdDate = createdDate;
        this.viewCount = viewCount;
    }

    public Post toEntity(){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}
