package jakuzie.api;

import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC3_GetPosts {

  private final PostRepository postRepository;

  @GetMapping("/posts")
  @PreAuthorize("isAuthenticated()")
  public List<PostResponse> getPost() {
    return postRepository.findAll()
        .stream().map(PostResponse::new).toList();
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
