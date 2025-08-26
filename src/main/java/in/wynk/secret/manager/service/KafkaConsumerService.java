package in.wynk.secret.manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class KafkaConsumerService {


    @Value("${test.data1}")
    String test;


    @PostMapping
    public void init(){
        System.out.println("Kafka password" + test);
    }

    //@KafkaListener(topics = "Topic01", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}

