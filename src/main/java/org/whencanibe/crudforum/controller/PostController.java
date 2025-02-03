package org.whencanibe.crudforum.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.whencanibe.crudforum.config.CustomUserDetails;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.UserRepository;
import org.whencanibe.crudforum.service.PostService;
import org.whencanibe.crudforum.service.UserService;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "post/list";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments());
        return "post/detail";
    }

    @GetMapping("/new")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/form";
    }

    @PostMapping
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        //Service로 파일과 함께 전달
        postService.createPost(userDetails.getId(),title, content, imageFile);
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}