package org.whencanibe.crudforum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.PostRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private static Post firstPost;
    @BeforeEach
    void setUp(){
        createUserAndHisPost("email@gmail.com","pw123456", "David","title"
                ,"content");
    }

    //현재 DataInitializer class에서 만든 data들도 카운트되는듯함.
    @Test
    void getAllPosts() {
        //Given
        createUserAndHisPost("email2@gmail.com", "pwpw1234",
                "James" , "title2", "Hello");

        //When
        List<Post> result = postService.getAllPosts();

        //Then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getPostById() {
        //Given
        Long RepoId = postRepository.findByTitle("title").get().getId();
        //When
        Post post = postService.getPostById(RepoId);
        //Then
        assertThat(post).isEqualTo(firstPost);
    }

    @Test
    void getPostByIdFailure() {
        //When / Then
        assertThatThrownBy(()-> postService.getPostById(-1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found");
    }

    @Test
    void createPost() {
        //Then
        assertThat(postRepository.findByTitle("title").get()).isEqualTo(firstPost);
    }

    @Test
    void deletePost() {
        //Given
        Long id = firstPost.getId();
        //When
        postService.deletePost(id);

        //Then
        assertThat(postRepository.findById(id)).isEmpty();
    }

    private void createUserAndHisPost(String email, String password, String username,String title, String content){
        User user = userService.registerUser
                (email, password , username);
        firstPost = postService.createPost(user.getId(), title , content,null);
    }

}