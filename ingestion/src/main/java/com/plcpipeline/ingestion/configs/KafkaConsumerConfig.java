// package com.plcpipeline.ingestion.configs;

// import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.core.ConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.support.serializer.JsonDeserializer;

// import java.util.HashMap;
// import java.util.Map;

// @EnableKafka
// @Configuration
// public class KafkaConsumerConfig {
//     @Value("${spring.kafka.bootstrap-servers}")
//     private String bootstrapServers;
//     @Value("${spring.kafka.consumer.group-id}")
//     private String groupId;

//     @Bean
//     public ConsumerFactory<String, TelemetryDataDto> telemetryConsumerFactory() {
        
//         Map<String, Object> props = new HashMap<>();
//         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//         props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//         props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//         props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Allow all packages for deserialization
//         // Alternatively, specify the package explicitly for security
//         //props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.plcpipeline.ingestion.dtos");


//         return new DefaultKafkaConsumerFactory<>(props);
//     }

//     @Bean
//     public ConcurrentKafkaListenerContainerFactory<String, TelemetryDataDto> telemetryKafkaListenerFactory() {
//         ConcurrentKafkaListenerContainerFactory<String, TelemetryDataDto> factory =
//                 new ConcurrentKafkaListenerContainerFactory<>();
//         factory.setConsumerFactory(telemetryConsumerFactory());
//         return factory;
//     }
// }
