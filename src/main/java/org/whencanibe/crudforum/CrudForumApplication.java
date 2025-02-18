package org.whencanibe.crudforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CrudForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudForumApplication.class, args);
    }

}
