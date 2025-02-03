package org.whencanibe.crudforum.repository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.service.PostService;
import org.whencanibe.crudforum.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final PostService postService;

    public DataInitializer(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @Override
    public void run(String... args){
        User user1 = userService.registerUser("abcd@gmail.com" , "qwer1234" , "davecho");
        userService.registerUser("efgh@gmail.com" , "qwer1234" , "minjikim");

        postService.createPost(user1.getId(), "인사드립니다.","안녕하세요. 반갑습니다.",null);
    }
}
