package jakuzie;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Slf4j
	@RestControllerAdvice
	static class GlobalExceptionHandler {
		@ExceptionHandler(Exception.class)
		@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
		public String handle(HttpServletRequest request, Exception e) {
      log.error("Error at {} {}: {}", request.getMethod(), request.getRequestURI(), e, e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			return sw.toString(); // security breach! Don't do this in your project. It's just for easier debugging
		}
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
