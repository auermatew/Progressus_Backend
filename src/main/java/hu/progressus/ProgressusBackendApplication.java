package hu.progressus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProgressusBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProgressusBackendApplication.class, args);
  }

}
