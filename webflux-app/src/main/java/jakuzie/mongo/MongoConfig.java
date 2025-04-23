package jakuzie.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MongoConfig {

  @Bean
  ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory cf) {
    return new ReactiveMongoTransactionManager(cf);
  }
}
