package jakuzie.user;

import jakuzie.user.blocking.BlockingUserService;
import jakuzie.user.nonblocking.NonBlockingAvatarService;
import jakuzie.user.nonblocking.NonBlockingUserService;
import jakuzie.user.parallel.ParallelUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

  private final BlockingUserService blockingUserService;
  private final NonBlockingUserService nonBlockingUserService;
  private final ParallelUserService parallelUserService;

  @PostMapping("/blocking/users")
  public User blockingCreateUser(@RequestBody CreateUserRequest request,
      @RequestParam(defaultValue = "200") Long delay) {
    return blockingUserService.createUser(request.email(), delay);
  }

  @PostMapping("/non-blocking/users")
  public Mono<User> nonBlockingCreateUser(@RequestBody CreateUserRequest request,
      @RequestParam(defaultValue = "200") Long delay) {
    return nonBlockingUserService.createUser(request.email(), delay);
  }

  @PostMapping("/parallel/users")
  public Mono<User> parallelCreateUser(@RequestBody CreateUserRequest request,
      @RequestParam(defaultValue = "200") Long delay) {
    return parallelUserService.createUser(request.email(), delay);
  }

  public record CreateUserRequest(String email) {

  }

}
