package in.wynk.secret.manager;

import in.wynk.secret.manager.maxmind.MaxMindService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SecretManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecretManagerApplication.class, args);
    }

}
