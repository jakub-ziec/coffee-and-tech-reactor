package jakuzie.api;

import jakuzie.exception.ResourceNotFoundException;
import jakuzie.mongo.Comment;
import jakuzie.mongo.CommentRepository;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostMongoRepository;
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
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC4_GetPostDetails {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @GetMapping("/posts/{postId}")
  @PreAuthorize("isAuthenticated()")
  public Mono<PostDetailsResponse> getPost(@PathVariable UUID postId) {
    return Mono.zip(
        postRepository.findById(postId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException())),
        commentRepository.findByPostId(postId)
            .map(CommentResponse::new)
            .collectList(),
        PostDetailsResponse::new);
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
