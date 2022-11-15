package com.brights.webblog_project.controller;

import com.brights.webblog_project.model.Post;
import com.brights.webblog_project.model.PostComment;
import com.brights.webblog_project.service.PostCommentService;
import com.brights.webblog_project.service.PostService;
import com.brights.webblog_project.service.UserCredentialsService;
import com.brights.webblog_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class PostController {

    private UserService userService;
    private UserCredentialsService userCredentialsService;
    private PostService postService;
    private PostCommentService postCommentService;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/uploads";

    @Autowired
    public PostController(UserService userService, UserCredentialsService userCredentialsService, PostService postService, PostCommentService postCommentService) {
        this.userService = userService;
        this.userCredentialsService = userCredentialsService;
        this.postService = postService;
        this.postCommentService = postCommentService;
    }

    @GetMapping("/post/new")
    public String postNewForm(Model model) {
        model.addAttribute("post", new Post());

        return "/post/addNew";
    }

    @PostMapping("/post/new")
    public String postNewSave(@Valid @ModelAttribute Post post,
                              BindingResult bindingResult,
                              Model model,
                              @RequestParam(required = false) boolean published,
                              @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {
        if(bindingResult.hasErrors()){
            return "/post/addNew";
        }
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDate.now());
        }
        if (file != null) {
            StringBuilder fileNames = new StringBuilder();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
            post.setPathOfPicture("/uploads/" + file.getOriginalFilename());
        }
        post.setPublished(published);
        postService.savePost(post);

        return "redirect:/";
    }
////////////

//forma za pisanje komentara
    @PostMapping("/post/comment")
    public String saveComment(@Valid @ModelAttribute PostComment postComment,
                              BindingResult bindingComment,
                              @RequestParam(value = "idpost") long id,
                              @RequestParam(value = "username") String username)  {
        System.err.println("POST comment: " + postComment);
        if (bindingComment.hasErrors()) {
            System.err.println("Comment did not validate");
            return "/post/postCommentForm";
        } else {
            postComment.setPost(postService.getPostById(id));
            postComment.setUser(userService.getUserById(userCredentialsService.getDetails(username).getUser().getId()));
            this.postCommentService.savePostComment(postComment);
            System.err.println("SAVE comment: " + postComment);
            return "redirect:/post/" + postComment.getPost().getId();
        }
    }

    //kreiramo novi komentar -- spojen na post/Comment
   // njezin submit bi trebao pokrenuti akciju za postMapping post/comment
    @GetMapping("/showCommentForUpdate/{id}") //ovaj id je id od POSTa
    public String commentForm (@PathVariable(value = "id") long id, Model model, Principal principal){

        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("postComment", new PostComment());
        model.addAttribute("username", principal.getName());
        return "/post/postCommentForm";
    }


//vidjeti post po njegovom IDu i sve njegove komentare
    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable(value = "id")long id) {

        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("postComment", postCommentService.getAllPostCommentsByPostId(id));
        return "/post/postView";
    }




   // @GetMapping("/comment/{id}")



    //////
/*
@GetMapping("/post/{id}/comments")
public String projectsHome(Model model) {
    model.addAttribute("PostComment", PostCommentService.getAllPostComments());
    return "post/comments";
}

    @GetMapping("/comments/showNewComment")
    public String showNewProjectForm (Model model){
        model.addAttribute("postComment", new PostComment());
        model.addAttribute("getAllPostComments", PostCommentService.getAllPostComments());
        return "post/comments/new";
    }

    @PostMapping("/post/comments")
    public String saveProject (@Valid @ModelAttribute PostComment postComment,
                               BindingResult bidingResult,
                               Model model){
        if(bidingResult.hasErrors()){
            model.addAttribute("getAllPostComments", PostCommentService.getAllProjects());
            return "post/comments/new";
        }

        projectService.saveProject(project);
        return "redirect:/projects";
    }*/


}
