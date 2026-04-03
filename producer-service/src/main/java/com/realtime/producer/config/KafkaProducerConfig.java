package com.realtime.producer.config;

import com.realtime.producer.dto.BookingEvent;
import com.realtime.producer.dto.DashboardEvent;
import com.realtime.producer.dto.NotificationEvent;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.booking-events}")
    private String bookingEventsTopic;

    @Value("${kafka.topic.dashboard-events}")
    private String dashboardEventsTopic;

    @Value("${kafka.topic.notification-events}")
    private String notificationEventsTopic;

    private Map<String, Object> getProducerProps() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        return config;
    }

    private ProducerFactory<String, Object> createProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerProps());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(createProducerFactory());
    }

    @Bean
    public NewTopic bookingEventsTopic() {
        return new NewTopic(bookingEventsTopic, 3, (short) 2);
    }

    @Bean
    public NewTopic dashboardEventsTopic() {
        return new NewTopic(dashboardEventsTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return new NewTopic(notificationEventsTopic, 2, (short) 1);
    }

    @Bean
    public KafkaAdminInfo kafkaAdminInfo() {
        return new KafkaAdminInfo(bootstrapServers, bookingEventsTopic, notificationEventsTopic, dashboardEventsTopic);
    }

    public static class KafkaAdminInfo {
        private final String bootstrapServers;
        private final String bookingEventsTopic;
        private final String notificationEventsTopic;
        private final String dashboardEventsTopic;

        public KafkaAdminInfo(String bootstrapServers, String bookingEventsTopic, 
                              String notificationEventsTopic, String dashboardEventsTopic) {
            this.bootstrapServers = bootstrapServers;
            this.bookingEventsTopic = bookingEventsTopic;
            this.notificationEventsTopic = notificationEventsTopic;
            this.dashboardEventsTopic = dashboardEventsTopic;
        }

        public Map<String, Object> getClusterInfo() {
            Map<String, Object> info = new HashMap<>();
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            
            try (AdminClient adminClient = AdminClient.create(props)) {
                info.put("bootstrapServers", bootstrapServers);
                info.put("clusterId", "kafka-cluster");
                
                var nodes = adminClient.describeCluster().nodes().get(10, TimeUnit.SECONDS);
                info.put("brokerCount", nodes != null ? nodes.size() : 1);
                
                Map<String, Object> topicsInfo = getTopicsInfo(adminClient);
                info.put("topics", topicsInfo);
                info.put("topicsCount", topicsInfo.size());
                
                Map<String, Object> bookingTopic = (Map<String, Object>) topicsInfo.get(bookingEventsTopic);
                info.put("bookingTopic", bookingTopic);
                
                Map<String, Object> notificationTopic = (Map<String, Object>) topicsInfo.get(notificationEventsTopic);
                info.put("notificationTopic", notificationTopic);
                
                Map<String, Object> dashboardTopic = (Map<String, Object>) topicsInfo.get(dashboardEventsTopic);
                info.put("dashboardTopic", dashboardTopic);
                
            } catch (Exception e) {
                info.put("error", e.getMessage());
            }
            return info;
        }

        private Map<String, Object> getTopicsInfo(AdminClient adminClient) {
            Map<String, Object> topicsMap = new HashMap<>();
            try {
                ListTopicsResult topicsResult = adminClient.listTopics();
                Set<String> topicNames = topicsResult.names().get(10, TimeUnit.SECONDS);

                for (String topicName : topicNames) {
                    if (topicName.startsWith("__")) continue;

                    var describeResult = adminClient.describeTopics(List.of(topicName));
                    var topicDescription = describeResult.allTopicNames().get(10, TimeUnit.SECONDS).get(topicName);

                    Map<String, Object> topicInfo = new HashMap<>();
                    topicInfo.put("name", topicName);
                    topicInfo.put("partitions", topicDescription.partitions().size());
                    topicInfo.put("replicationFactor", topicDescription.partitions().isEmpty() ? 0 : 
                        topicDescription.partitions().get(0).replicas().size());
                    topicInfo.put("partitionDetails", getPartitionDetails(topicDescription.partitions()));
                    
                    topicsMap.put(topicName, topicInfo);
                }
            } catch (Exception e) {
                // log error
            }
            return topicsMap;
        }

        private java.util.List<Map<String, Object>> getPartitionDetails(java.util.List<TopicPartitionInfo> partitions) {
            return partitions.stream().map(p -> {
                Map<String, Object> info = new HashMap<>();
                info.put("partitionId", p.partition());
                info.put("leader", p.leader() != null ? p.leader().id() : -1);
                info.put("replicas", p.replicas().stream().map(r -> r.id()).collect(Collectors.toList()));
                info.put("isr", p.isr().stream().map(r -> r.id()).collect(Collectors.toList()));
                return info;
            }).collect(Collectors.toList());
        }
    }
}
