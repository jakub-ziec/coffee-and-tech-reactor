package jakuzie.api;

import jakuzie.exception.CommandRejectedException;
import jakuzie.exception.ResourceNotFoundException;
import jakuzie.mongo.Comment;
import jakuzie.mongo.CommentRepository;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC5_CreateComment {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @PostMapping("/posts/{postId}/comments")
  @PreAuthorize("isAuthenticated()")
  public Mono<Void> getPost(@PathVariable UUID postId, @RequestBody CreateCommentRequest request) {
    Mono<Post> postMono = postRepository.findById(postId)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException()));

    Mono<Boolean> isUnlockedMono = postMono.flatMap(
        post -> getLoggedInUserName().flatMap(user -> isUnlocked(post.getAuthorName(), user)));
    Mono<Boolean> isSafeMono = postMono.flatMap(post -> isSafe(post.getContent(), request.comment()));

    Mono<Boolean> isSafeAndUnlockedMono = Mono.zip(isSafeMono, isUnlockedMono, (isSafe, isUnlocked) -> isSafe && isUnlocked);

    Mono<Comment> commentMono = getLoggedInUserName().flatMap(
        user -> commentRepository.save(new Comment(postId, request.comment(), user)));

    return isSafeAndUnlockedMono
        .filter(Boolean::booleanValue)
        .switchIfEmpty(Mono.error(new CommandRejectedException()))
        .flatMap(aBoolean -> commentMono)
        .then();
  }

  private static Mono<String> getLoggedInUserName() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Principal::getName);
  }

  private Mono<Boolean> isUnlocked(String postAuthor, String commentAuthor) {
    return Mono.just(true);
  }

  private Mono<Boolean> isSafe(String postContent, String comment) {
    return Mono.just(true);
  }

  public record CreateCommentRequest(String comment, String author) {
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
