package jakuzie.api;

import jakuzie.mongo.Post;
import jakuzie.mongo.PostMongoRepository;
import jakuzie.mongo.PostRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC3_GetPosts {

  private final PostRepository postRepository;

  @GetMapping("/posts")
  @PreAuthorize("isAuthenticated()")
  public Flux<PostResponse> getPost() {
    return postRepository.findAll()
        .map(PostResponse::new);
  }

  public record PostResponse(
      String id,
      String title,
      String content,
      String author,
      Instant createdAt) {

    public PostResponse(Post post) {
      this(post.getId().toString(), post.getTitle(), post.getContent(), post.getAuthorName(), post.getCreatedAt());
    }
  }

}
