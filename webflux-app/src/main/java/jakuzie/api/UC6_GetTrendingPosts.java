package jakuzie.api;

import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.groupingBy;

import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import jakuzie.rabbit.EventPublisher.PostLiked;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UC6_GetTrendingPosts {

  private final PostRepository postRepository;
  private final AtomicReference<TrendingPosts> trendingPosts = new AtomicReference<>();
  private final Sinks.Many<TrendingPosts> trendingPostsSink = Sinks.many().multicast().directBestEffort();


  @GetMapping("/trending-posts")
  public TrendingPosts getTrendingPosts() {
    return trendingPosts.get();
  }

  @GetMapping(path = "trending-posts-live", produces = "text/event-stream")
  public Flux<TrendingPosts> getTrendingPostsLive() {
    return trendingPostsSink.asFlux();
  }

  @Bean
  public Function<Flux<PostLiked>, Flux<TrendingPosts>> onLikeEvent() {
    return this::getTrendingPosts;
  }

  private Flux<TrendingPosts> getTrendingPosts(Flux<PostLiked> postLikedFlux) {
    return postLikedFlux
        .buffer(ofSeconds(1))
        .flatMap(events -> postRepository.findAllById(events.stream()
                .map(PostLiked::postId)
                .map(UUID::fromString)
                .toList())
            .collectList()
            .map(posts -> toTrendingPosts(posts, events))
        )
        .map(TrendingPosts::new)
        .doOnNext(trendingPosts::set)
        .doOnNext(trendingPostsSink::tryEmitNext)
        .onErrorContinue(
            (exception, element) -> log.error("Pretend nothing happened, to NOT disconnect from Rabbit {} {}",
                exception.getMessage(), element, exception))
        .doOnNext(message -> log.info("Sending: {}", message));
  }

  private List<TrendingPost> toTrendingPosts(List<Post> posts, List<PostLiked> events) {
    Map<String, List<PostLiked>> eventByPostId = events.stream().collect(groupingBy(PostLiked::postId));
    return posts.stream()
        .map(post -> new TrendingPost(post,
            eventByPostId.getOrDefault(post.getId().toString(), List.of()).stream().mapToInt(PostLiked::likes).sum()))
        .limit(10)
        .toList();
  }

  public record TrendingPosts(List<TrendingPost> posts) {

  }

  public record TrendingPost(String title, int likes) {

    public TrendingPost(Post post, int likes) {
      this(post.getTitle(), likes);
    }
  }

}
