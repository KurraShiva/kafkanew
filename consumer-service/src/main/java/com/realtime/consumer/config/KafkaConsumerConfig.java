package com.realtime.consumer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.realtime.consumer.dto.BookingEvent;
import com.realtime.consumer.dto.DashboardEvent;
import com.realtime.consumer.dto.NotificationEvent;
import com.realtime.consumer.dto.PaymentEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.booking-group}")
    private String bookingGroup;

    @Value("${spring.kafka.consumer.analytics-group}")
    private String analyticsGroup;

    @Value("${spring.kafka.consumer.dashboard-group}")
    private String dashboardGroup;

    @Value("${spring.kafka.consumer.notification-group}")
    private String notificationGroup;

    private Map<String, Object> createBaseConfig(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        return config;
    }
    
 // Add to existing KafkaConsumerConfig.java

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentKafkaListenerContainerFactory() {
        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class);
        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);
        
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                createBaseConfig("payment-consumer-group"), 
                new StringDeserializer(), 
                deserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(2);
        return factory;
    }
    
 // Add to existing KafkaConsumerConfig.java

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentKafkaListenerContainerFactory() {
//        JsonDeserializer<PaymentEvent> deserializer = new JsonDeserializer<>(PaymentEvent.class);
//        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
//        deserializer.setUseTypeHeaders(false);
//        deserializer.setRemoveTypeHeaders(false);
//        
//        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createBaseConfig("payment-consumer-group"), 
//                new StringDeserializer(), deserializer));
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        factory.setConcurrency(2);
//        return factory;
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingEvent> bookingKafkaListenerContainerFactory() {
        JsonDeserializer<BookingEvent> deserializer = new JsonDeserializer<>(BookingEvent.class);
        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);
        
        ConcurrentKafkaListenerContainerFactory<String, BookingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createBaseConfig(bookingGroup), new StringDeserializer(), deserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingEvent> analyticsKafkaListenerContainerFactory() {
        JsonDeserializer<BookingEvent> deserializer = new JsonDeserializer<>(BookingEvent.class);
        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);
        
        ConcurrentKafkaListenerContainerFactory<String, BookingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createBaseConfig(analyticsGroup), new StringDeserializer(), deserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(2);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DashboardEvent> dashboardKafkaListenerContainerFactory() {
        JsonDeserializer<DashboardEvent> deserializer = new JsonDeserializer<>(DashboardEvent.class);
        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);
        
        ConcurrentKafkaListenerContainerFactory<String, DashboardEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createBaseConfig(dashboardGroup), new StringDeserializer(), deserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(2);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> notificationKafkaListenerContainerFactory() {
        JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);
        deserializer.addTrustedPackages("com.realtime.consumer.dto", "com.realtime.producer.dto", "*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);
        
        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createBaseConfig(notificationGroup), new StringDeserializer(), deserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> analyticsProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> analyticsKafkaTemplate() {
        return new KafkaTemplate<>(analyticsProducerFactory());
    }
}
