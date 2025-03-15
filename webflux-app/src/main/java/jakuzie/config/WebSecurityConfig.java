package jakuzie.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableReactiveMethodSecurity
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
        .authorizeExchange(authz -> authz
            .anyExchange().permitAll()
        )
        .httpBasic(withDefaults())
        .formLogin(withDefaults())
        .csrf(csrf -> csrf.disable());
    return http.build();
  }
}
