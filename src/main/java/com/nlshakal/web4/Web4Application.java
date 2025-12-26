package com.nlshakal.web4;

import com.nlshakal.web4.config.JwtProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration.class
})
@EnableConfigurationProperties(JwtProperties.class)
public class Web4Application {

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("Environment variables loaded from .env file");

        } catch (Exception e) {
            System.err.println("Warning: .env file not found, using system environment variables");
            System.err.println("Make sure all required environment variables are set!");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Web4Application.class, args);
    }
}
