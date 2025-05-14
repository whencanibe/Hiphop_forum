package org.whencanibe.crudforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.whencanibe.crudforum.config.CustomUserDetails;
import org.whencanibe.crudforum.domain.Post;
import org.whencanibe.crudforum.dto.CommentDto;
import org.whencanibe.crudforum.dto.PostDto;
import org.whencanibe.crudforum.repository.PostRepository;
import org.whencanibe.crudforum.service.CommentService;
import org.whencanibe.crudforum.service.PostLikeService;
import org.whencanibe.crudforum.service.PostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostRepository postRepository;
    private final CommentService commentService;

    // 글 목록 + 페이징
    @GetMapping
    public String getPostList(
            @RequestParam(defaultValue="1") int page,  // 기본값: 1
            Model model
    ) {
        // 사용자가 1 미만을 넣으면 0으로 보정 (에러 방지)
        int zeroBasedPage = (page < 1) ? 0 : page - 1;
        int size = 20; // 한 페이지에 보여줄 게시글 수 (원하는 값으로)

        // Service에서 Page<PostDto> 조회
        Page<PostDto> postDtoPage = postService.getAllPostsAsDto(zeroBasedPage, size);

        // 이제 postDtoPage에는 getContent(), getTotalPages(), getNumber() 등이 있음
        model.addAttribute("posts", postDtoPage.getContent());   // 현재 페이지의 게시글 목록 (List<PostDto>)

        // 화면에서 페이지네이션 표시를 위해
        model.addAttribute("currentPage", page);                 // 1-based 현재 페이지
        model.addAttribute("totalPages", postDtoPage.getTotalPages()); // 전체 페이지 수

        return "post/list"; // Thymeleaf 템플릿: /templates/post/list.html
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          Model model) {
        Post post = postService.getPostById(id);
        List<CommentDto> commentDtos = commentService.getCommentsByPostId(id);

        boolean isAuthor = false;
        if(userDetails != null && post.getUser().getId().equals(userDetails.getId())){
            isAuthor = true;
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", commentDtos);
        model.addAttribute("isAuthor", isAuthor);
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

    // 좋아요 토글 API (AJAX로 호출)
    @PostMapping("/{id}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId(); // 로그인한 사용자의 ID
        boolean isLiked = postLikeService.toggleLike(userId, id);

        // 현재 게시글 likeCount를 다시 가져와 응답해줄 수도 있음
        long likeCount = postRepository.findById(id)
                .map(Post::getLikeCount)
                .orElse(0);

        // JSON 형태로 응답
        Map<String, Object> result = new HashMap<>();
        result.put("liked", isLiked);       // true=좋아요 ON, false=OFF
        result.put("likeCount", likeCount); // 현재 좋아요 수

        return result;
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {

        if (userDetails == null) {
            throw new IllegalArgumentException("로그인 후 수정할 수 있습니다.");
        }

        Post post = postService.getPostById(id); // 예외 발생 가능

        if (!post.getUser().getId().equals(userDetails.getId())) {
            throw new IllegalArgumentException("본인의 글만 수정할 수 있습니다.");
        }

        model.addAttribute("post", post);
        return "post/edit";
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postService.getPostById(id);

        // 권한 체크
        if (!post.getUser().getId().equals(userDetails.getId())) {
            throw new IllegalArgumentException("본인 글만 수정 가능");
        }

        // post 내용 수정
        postService.updatePost(id, title, content, userDetails.getId());

        return "redirect:/posts/" + id; // 수정 후 상세 페이지로 이동
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postService.getPostById(id);
        // 권한 체크
        if (!post.getUser().getId().equals(userDetails.getId())) {
            throw new IllegalArgumentException("본인 글만 삭제 가능");
        }

        postService.deletePost(id);
        return "redirect:/posts";
    }
}