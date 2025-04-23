package jakuzie.api;

import static java.util.Objects.requireNonNull;

import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import jakuzie.rabbit.EventPublisher;
import jakuzie.rabbit.EventPublisher.PostCreated;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC2_CreatePost {

  private final PostRepository postRepository;
  private final EventPublisher eventPublisher;

  @PostMapping("/posts")
  @PreAuthorize("isAuthenticated()")
  @Transactional
  public Mono<CreatePostResponse> createPost(@RequestBody CreatePostRequest request) {
    return getLoggedInUserName()
        .flatMap(loggedInUser -> postRepository.save(new Post(request.title(), request.content(), loggedInUser)))
        .delayUntil(post -> eventPublisher.publish(new PostCreated(post)))
        .map(post -> new CreatePostResponse(post.getId().toString()));
  }

  private static Mono<String> getLoggedInUserName() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Principal::getName);
  }

  public record CreatePostRequest(String title, String content) {

  }

  public record CreatePostResponse(String id) {

  }

}
