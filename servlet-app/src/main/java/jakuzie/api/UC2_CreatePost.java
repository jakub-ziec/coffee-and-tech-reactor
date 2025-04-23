package jakuzie.api;

import static java.util.Objects.*;

import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import jakuzie.rabbit.EventPublisher;
import jakuzie.rabbit.EventPublisher.PostCreated;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC2_CreatePost {

  private final PostRepository postRepository;
  private final EventPublisher eventPublisher;

  @PostMapping("/posts")
  @PreAuthorize("isAuthenticated()")
  @Transactional
  public CreatePostResponse createPost(@RequestBody CreatePostRequest request) {
    String loggedInUser = getLoggedInUserName();
    Post post = postRepository.save(new Post(request.title(), request.content(), loggedInUser));
    eventPublisher.publish(new PostCreated(post));
    return new CreatePostResponse(post.getId().toString());
  }

  private static String getLoggedInUserName() {
    return requireNonNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication must not be null")
        .getName();
  }

  public record CreatePostRequest(String title, String content) {

  }

  public record CreatePostResponse(String id) {

  }

}
