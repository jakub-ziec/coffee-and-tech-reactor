package jakuzie.api;

import jakuzie.exception.ResourceNotFoundException;
import jakuzie.mongo.Comment;
import jakuzie.mongo.CommentRepository;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC4_GetPostDetails {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @GetMapping("/posts/{postId}")
  @PreAuthorize("isAuthenticated()")
  public PostDetailsResponse getPost(@PathVariable UUID postId) {
    Post post = postRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
    List<CommentResponse> comments = commentRepository.findByPostId(postId).stream()
        .map(CommentResponse::new).toList();
    return new PostDetailsResponse(post, comments);
  }

  public record PostDetailsResponse(
      String id,
      String title,
      String content,
      String author,
      Instant createdAt,
      List<CommentResponse> comments) {

    public PostDetailsResponse(Post post, List<CommentResponse> comments) {
      this(post.getId().toString(), post.getTitle(), post.getContent(), post.getAuthorName(), post.getCreatedAt(),
          comments);
    }
  }

  public record CommentResponse(String content, String author, String createdAt) {

    public CommentResponse(Comment comment) {
      this(comment.getContent(), comment.getAuthorName(), comment.getCreatedAt().toString());
    }

  }

}
