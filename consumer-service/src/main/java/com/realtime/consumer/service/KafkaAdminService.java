package com.realtime.consumer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAdminService {

    private final KafkaAdmin kafkaAdmin;

    public Map<String, Object> getKafkaClusterInfo() {
        Map<String, Object> clusterInfo = new HashMap<>();

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            clusterInfo.put("bootstrapServers", "localhost:9092");
            clusterInfo.put("clusterId", "kafka-cluster");
            clusterInfo.put("brokerCount", getBrokerCount(adminClient));
            clusterInfo.put("topics", getTopicsInfo(adminClient));
            clusterInfo.put("consumerGroups", getConsumerGroupsInfo(adminClient));
        } catch (Exception e) {
            log.error("Error fetching Kafka cluster info", e);
        }

        return clusterInfo;
    }

    private int getBrokerCount(AdminClient adminClient) {
        try {
            return adminClient.describeCluster().nodes().get(10, TimeUnit.SECONDS).size();
        } catch (Exception e) {
            return 1;
        }
    }

    public Map<String, Object> getTopicsInfo() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return getTopicsInfo(adminClient);
        } catch (Exception e) {
            log.error("Error getting topics info", e);
            return new HashMap<>();
        }
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
                topicInfo.put("partitions", topicDescription.partitions().size());
                topicInfo.put("replicationFactor", topicDescription.partitions().isEmpty() ? 0 : 
                    topicDescription.partitions().get(0).replicas().size());
                topicInfo.put("partitionDetails", getPartitionDetails(adminClient, topicName));
                
                topicsMap.put(topicName, topicInfo);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Error getting topics info", e);
        }

        return topicsMap;
    }

    private List<Map<String, Object>> getPartitionDetails(AdminClient adminClient, String topicName) {
        List<Map<String, Object>> partitions = new ArrayList<>();

        try {
            var describeResult = adminClient.describeTopics(List.of(topicName));
            var topicDescription = describeResult.allTopicNames().get(10, TimeUnit.SECONDS).get(topicName);

            for (var partition : topicDescription.partitions()) {
                Map<String, Object> partitionInfo = new HashMap<>();
                partitionInfo.put("partitionId", partition.partition());
                partitionInfo.put("leader", partition.leader() != null ? partition.leader().id() : -1);
                partitionInfo.put("replicas", partition.replicas().stream().map(r -> r.id()).collect(Collectors.toList()));
                partitionInfo.put("isr", partition.isr().stream().map(r -> r.id()).collect(Collectors.toList()));
                partitions.add(partitionInfo);
            }
        } catch (Exception e) {
            log.error("Error getting partition details for topic: {}", topicName, e);
        }

        return partitions;
    }

    public Map<String, Object> getConsumerGroupsInfo() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return getConsumerGroupsInfo(adminClient);
        } catch (Exception e) {
            log.error("Error getting consumer groups info", e);
            return new HashMap<>();
        }
    }

    private Map<String, Object> getConsumerGroupsInfo(AdminClient adminClient) {
        Map<String, Object> groupsMap = new HashMap<>();

        try {
            ListConsumerGroupsResult groupsResult = adminClient.listConsumerGroups();
            var groupIds = groupsResult.all().get(10, TimeUnit.SECONDS);

            if (!groupIds.isEmpty()) {
                var groupIdList = groupIds.stream().map(g -> g.groupId()).collect(Collectors.toList());
                
                var describeResult = adminClient.describeConsumerGroups(groupIdList);
                var allGroups = describeResult.all().get(10, TimeUnit.SECONDS);

                for (var entry : allGroups.entrySet()) {
                    String groupId = entry.getKey();
                    var groupDesc = entry.getValue();
                    
                    Map<String, Object> groupInfo = new HashMap<>();
                    groupInfo.put("groupId", groupId);
                    groupInfo.put("members", groupDesc.members().size());
                    groupInfo.put("state", groupDesc.state().toString());

                    Map<String, Object> partitionInfo = getConsumerGroupPartitionInfo(adminClient, groupId);
                    groupInfo.put("assignedPartitions", partitionInfo);

                    groupsMap.put(groupId, groupInfo);
                }
            }
        } catch (Exception e) {
            log.error("Error getting consumer groups info", e);
        }

        return groupsMap;
    }

//    private Map<String, Object> getConsumerGroupPartitionInfo(AdminClient adminClient, String groupId) {
//        Map<String, Object> partitionInfo = new HashMap<>();
//
//        try {
//            DescribeConsumerGroupsResult groupsResult = adminClient.describeConsumerGroups(List.of(groupId));
//            var groupDescription = groupsResult.all().get(10, TimeUnit.SECONDS).get(groupId);
//
//            List<Map<String, Object>> assignedPartitions = new ArrayList<>();
//
//            for (var member : groupDescription.members()) {
//                for (TopicPartition tp : member.assignment()) {
//                    Map<String, Object> partitionDetail = new HashMap<>();
//                    partitionDetail.put("topic", tp.topic());
//                    partitionDetail.put("partition", tp.partition());
//                    partitionDetail.put("consumerId", member.consumerId());
//                    partitionDetail.put("host", member.host());
//                    assignedPartitions.add(partitionDetail);
//                }
//            }
//
//            partitionInfo.put("assigned", assignedPartitions);
//        } catch (Exception e) {
//            log.debug("Could not get partition info for group: {}", groupId);
//        }
//
//        return partitionInfo;
//    }

    private Map<String, Object> getConsumerGroupPartitionInfo(AdminClient adminClient, String groupId) {
        Map<String, Object> partitionInfo = new HashMap<>();

        try {
            DescribeConsumerGroupsResult groupsResult = adminClient.describeConsumerGroups(List.of(groupId));
            var groupDescription = groupsResult.all().get(10, TimeUnit.SECONDS).get(groupId);

            List<Map<String, Object>> assignedPartitions = new ArrayList<>();

            for (var member : groupDescription.members()) {
                // Fix: Get the actual Set of TopicPartitions from MemberAssignment
                var assignment = member.assignment();
                if (assignment != null) {
                    Set<TopicPartition> topicPartitions = assignment.topicPartitions();
                    if (topicPartitions != null) {
                        for (TopicPartition tp : topicPartitions) {
                            Map<String, Object> partitionDetail = new HashMap<>();
                            partitionDetail.put("topic", tp.topic());
                            partitionDetail.put("partition", tp.partition());
                            partitionDetail.put("consumerId", member.consumerId());
                            partitionDetail.put("host", member.host());
                            assignedPartitions.add(partitionDetail);
                        }
                    }
                }
            }

            partitionInfo.put("assigned", assignedPartitions);
        } catch (Exception e) {
            log.debug("Could not get partition info for group: {}", groupId);
        }

        return partitionInfo;
    }
    public Map<String, Object> getTopicOffsets(String topicName) {
        Map<String, Object> offsetInfo = new HashMap<>();

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            var describeResult = adminClient.describeTopics(List.of(topicName));
            var topicDescription = describeResult.allTopicNames().get(10, TimeUnit.SECONDS).get(topicName);

            List<Map<String, Object>> partitionOffsets = new ArrayList<>();
            long totalMessages = 0;

            for (var partition : topicDescription.partitions()) {
                TopicPartition tp = new TopicPartition(topicName, partition.partition());
                
                var logEndOffsetResult = adminClient.listOffsets(
                    Map.of(tp, OffsetSpec.latest())
                );
                long logEndOffset = logEndOffsetResult.partitionResult(tp).get().offset();

                var currentOffsetResult = adminClient.listOffsets(
                    Map.of(tp, OffsetSpec.earliest())
                );
                long currentOffset = currentOffsetResult.partitionResult(tp).get().offset();
                
                long lag = logEndOffset - currentOffset;
                totalMessages += lag;

                Map<String, Object> partitionData = new HashMap<>();
                partitionData.put("partition", partition.partition());
                partitionData.put("leader", partition.leader() != null ? partition.leader().id() : -1);
                partitionData.put("replicas", partition.replicas().stream().map(r -> r.id()).collect(Collectors.toList()));
                partitionData.put("logEndOffset", logEndOffset);
                partitionData.put("currentOffset", currentOffset);
                partitionData.put("lag", lag);
                partitionOffsets.add(partitionData);
            }

            offsetInfo.put("topic", topicName);
            offsetInfo.put("partitions", partitionOffsets);
            offsetInfo.put("totalMessages", totalMessages);
        } catch (Exception e) {
            log.error("Error getting offsets for topic: {}", topicName, e);
        }

        return offsetInfo;
    }

//    public Map<String, Object> getConsumerGroupOffsets(String groupId) {
//        Map<String, Object> groupOffsets = new HashMap<>();
//
//        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
//            DescribeConsumerGroupsResult result = adminClient.describeConsumerGroups(List.of(groupId));
//            var groupDescription = result.all().get(10, TimeUnit.SECONDS).get(groupId);
//
//            List<Map<String, Object>> partitionDataList = new ArrayList<>();
//
//            for (var member : groupDescription.members()) {
//                for (TopicPartition tp : member.assignment()) {
//                    Map<String, Object> partitionData = new HashMap<>();
//                    partitionData.put("topic", tp.topic());
//                    partitionData.put("partition", tp.partition());
//                    partitionData.put("consumerId", member.consumerId());
//                    partitionData.put("host", member.host());
//
//                    try {
//                        var offsetResult = adminClient.listOffsets(
//                            Map.of(tp, OffsetSpec.latest())
//                        );
//                        long logEndOffset = offsetResult.partitionResult(tp).get().offset();
//                        partitionData.put("logEndOffset", logEndOffset);
//                        partitionData.put("currentOffset", "unknown");
//                        partitionData.put("lag", "unknown");
//                    } catch (Exception ex) {
//                        partitionData.put("logEndOffset", 0L);
//                        partitionData.put("currentOffset", 0L);
//                        partitionData.put("lag", 0L);
//                    }
//
//                    partitionDataList.add(partitionData);
//                }
//            }
//
//            groupOffsets.put("groupId", groupId);
//            groupOffsets.put("state", groupDescription.state().toString());
//            groupOffsets.put("partitions", partitionDataList);
//        } catch (Exception e) {
//            log.error("Error getting consumer group offsets for: {}", groupId, e);
//        }
//
//        return groupOffsets;
//    }
    
    public Map<String, Object> getConsumerGroupOffsets(String groupId) {
        Map<String, Object> groupOffsets = new HashMap<>();

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeConsumerGroupsResult result = adminClient.describeConsumerGroups(List.of(groupId));
            var groupDescription = result.all().get(10, TimeUnit.SECONDS).get(groupId);

            List<Map<String, Object>> partitionDataList = new ArrayList<>();

            for (var member : groupDescription.members()) {
                // Fix: Get the actual Set of TopicPartitions from MemberAssignment
                var assignment = member.assignment();
                if (assignment != null) {
                    Set<TopicPartition> topicPartitions = assignment.topicPartitions();
                    if (topicPartitions != null) {
                        for (TopicPartition tp : topicPartitions) {
                            Map<String, Object> partitionData = new HashMap<>();
                            partitionData.put("topic", tp.topic());
                            partitionData.put("partition", tp.partition());
                            partitionData.put("consumerId", member.consumerId());
                            partitionData.put("host", member.host());

                            try {
                                var offsetResult = adminClient.listOffsets(
                                    Map.of(tp, OffsetSpec.latest())
                                );
                                long logEndOffset = offsetResult.partitionResult(tp).get().offset();
                                partitionData.put("logEndOffset", logEndOffset);
                                partitionData.put("currentOffset", "unknown");
                                partitionData.put("lag", "unknown");
                            } catch (Exception ex) {
                                partitionData.put("logEndOffset", 0L);
                                partitionData.put("currentOffset", 0L);
                                partitionData.put("lag", 0L);
                            }

                            partitionDataList.add(partitionData);
                        }
                    }
                }
            }

            groupOffsets.put("groupId", groupId);
            groupOffsets.put("state", groupDescription.state().toString());
            groupOffsets.put("partitions", partitionDataList);
        } catch (Exception e) {
            log.error("Error getting consumer group offsets for: {}", groupId, e);
        }

        return groupOffsets;
    }
    
    
    
}