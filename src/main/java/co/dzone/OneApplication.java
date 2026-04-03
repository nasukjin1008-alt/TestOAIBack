package co.dzone;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@EnableEncryptableProperties
public class OneApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "oneai");
        System.setProperty("spring.config.location",
                "file:./config/application.yml,classpath:/config/application-oneai.yml");
        SpringApplication.run(OneApplication.class, args);
    }
}
