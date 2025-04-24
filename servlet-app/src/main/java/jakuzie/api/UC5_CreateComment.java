package jakuzie.api;

import static java.util.Objects.requireNonNull;

import jakuzie.exception.CommandRejectedException;
import jakuzie.exception.ResourceNotFoundException;
import jakuzie.mongo.Comment;
import jakuzie.mongo.CommentRepository;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import jakuzie.rest.CommentCheckerClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC5_CreateComment {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final CommentCheckerClient commentCheckerClient;

  @PostMapping("/posts/{postId}/comments")
  @PreAuthorize("isAuthenticated()")
  public void createPost(@PathVariable UUID postId, @RequestBody CreateCommentRequest request) {
    Post post = postRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
    String user = getLoggedInUserName();
    if (isSafe(post.getContent(), request.comment()) && isUnlocked(post.getAuthorName(), user)) {
      commentRepository.save(new Comment(postId, request.comment(), user));
    } else {
      throw new CommandRejectedException();
    }
  }

  private static String getLoggedInUserName() {
    return requireNonNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication must not be null")
        .getName();
  }

  private boolean isUnlocked(String postAuthor, String commentAuthor) {
    return commentCheckerClient.isAuthorUnlocked(postAuthor, commentAuthor);
  }

  private boolean isSafe(String postContent, String comment) {
    return commentCheckerClient.isCommentSafe(postContent, comment);
  }

  public record CreateCommentRequest(String comment) {

  }

}
