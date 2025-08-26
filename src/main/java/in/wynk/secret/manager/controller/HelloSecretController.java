package in.wynk.secret.manager.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HelloSecretController {

    @Value("${test.data1}")
    String test;


    @PostMapping
    public void init(){
        System.out.println("Kafka password" + test);
    }
}