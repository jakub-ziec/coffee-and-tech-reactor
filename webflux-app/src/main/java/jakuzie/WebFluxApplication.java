package jakuzie;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication(exclude = {

})
@EnableCaching
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@Slf4j
public class WebFluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxApplication.class, args);
	}

	@Bean
	public WebClient webClient(WebClient.Builder webClientBuilder) {
		return webClientBuilder.build();
	}

//	@Slf4j
//	@RestControllerAdvice
//	@Order(100)
//	static class GlobalExceptionHandler {
//		@ExceptionHandler(Exception.class)
//		@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//		public String handle(Exception e) {
//      log.error("Error: {}", e, e);
//			StringWriter sw = new StringWriter();
//			e.printStackTrace(new PrintWriter(sw));
//			return sw.toString(); // security breach! Don't do this in your project. It's just for easier debugging
//		}
//	}


	@Slf4j
	@Component
	public static class ErrorLoggerWebFilter implements WebFilter {

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			return chain.filter(exchange)
					.doOnError(e -> log.error("Error: {}", e, e));
		}
	}

}
