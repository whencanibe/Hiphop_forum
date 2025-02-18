package org.whencanibe.crudforum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.whencanibe.crudforum.domain.Comment;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private User user;
    private Post post;
    private String createdDate; //"yyyy-MM-dd" 형태로 포맷된 문자열을 담기 위함

    @Builder
    public CommentDto(Comment entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.user = entity.getUser();
        this.post = entity.getPost();
        this.createdDate = entity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Comment toEntity(){
        return Comment.builder()
                .content(content)
                .post(post)
                .user(user)
                .build();
    }
}
