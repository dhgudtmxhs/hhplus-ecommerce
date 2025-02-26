package kr.hhplus.be.server.common.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
//import org.springframework.kafka.config.NewTopic;

@Configuration
public class KafkaAdminConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic orderDataTopic() {
        return new NewTopic("order-data-topic", 3, (short) 1);
    }

}
