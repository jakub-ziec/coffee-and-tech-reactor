package jakuzie.api;

import jakuzie.exception.CommandRejectedException;
import jakuzie.exception.ResourceNotFoundException;
import jakuzie.mongo.Comment;
import jakuzie.mongo.CommentRepository;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostMongoRepository;
import jakuzie.mongo.PostRepository;
import jakuzie.rest.CommentCheckerClient;
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
  private final CommentCheckerClient commentCheckerClient;

  @PostMapping("/posts/{postId}/comments")
  @PreAuthorize("isAuthenticated()")
  public Mono<Void> createPost(@PathVariable UUID postId, @RequestBody CreateCommentRequest request) {
    return postRepository.findById(postId)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException()))
        .flatMap(post -> Mono.zip(
            isSafe(post.getContent(), request.comment()),
            getLoggedInUserName().flatMap(user -> isUnlocked(post.getAuthorName(), user)),
            (isSafe, isUnlocked) -> isSafe && isUnlocked))
        .filter(Boolean::booleanValue)
        .switchIfEmpty(Mono.error(new CommandRejectedException()))
        .flatMap(canComment -> getLoggedInUserName().flatMap(
            user -> commentRepository.save(new Comment(postId, request.comment(), user))))
        .then();
  }

  private static Mono<String> getLoggedInUserName() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Principal::getName);
  }

  private Mono<Boolean> isUnlocked(String postAuthor, String commentAuthor) {
    return commentCheckerClient.isAuthorUnlocked(postAuthor, commentAuthor);
  }

  private Mono<Boolean> isSafe(String postContent, String comment) {
    return commentCheckerClient.isCommentSafe(postContent, comment);
  }

  public record CreateCommentRequest(String comment) {

  }

}
