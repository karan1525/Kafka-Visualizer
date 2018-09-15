package karan.kafkavisualizer;

import karan.kafkavisualizer.api.RestResource;
import karan.kafkavisualizer.domain.CommandLineArgs;
import karan.kafkavisualizer.service.*;
import karan.kafkavisualizer.util.HttpResponseFactory;
import kafka.admin.AdminClient;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppBootstrapper {

    @Bean
    public CommandLineArgs commandLineArgs(ApplicationArguments applicationArguments) {
        return new CommandLineArgs(applicationArguments.getSourceArgs());
    }

    @Bean
    public HttpResponseFactory httpResponseFactory() {
        return new HttpResponseFactory();
    }

    @Bean(destroyMethod = "close")
    public ZkClient zkClient(CommandLineArgs commandLineArgs) {
        var zkClient = new ZkClient(commandLineArgs.zookeeperServers, 10000, 10000,
                new ZkSerializer() {
                    public byte[] serialize(Object data) throws ZkMarshallingError {
                        return String.valueOf(data).getBytes();
                    }

                    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                        return new String(bytes);
                    }
                });
        zkClient.waitUntilConnected(10, TimeUnit.SECONDS);
        return zkClient;
    }

    @Bean(destroyMethod = "close")
    public AdminClient adminClient(CommandLineArgs commandLineArgs) {
        return AdminClient.createSimplePlaintext(commandLineArgs.kafkaServers);
    }

    @Bean
    public KafkaUtils kafkaAdmin(ZkClient zkClient, AdminClient adminClient) {
        return new KafkaUtils(zkClient, adminClient);
    }

    @Bean(destroyMethod = "close")
    public KafkaAllTopicsConsumer kafkaConsumerWrapper(CommandLineArgs commandLineArgs) {
        return new KafkaAllTopicsConsumer(commandLineArgs.kafkaServers);
    }

    @Bean(destroyMethod = "close")
    public KafkaProducerWrapper kafkaProducerWrapper(CommandLineArgs commandLineArgs) {
        return new KafkaProducerWrapper(commandLineArgs.kafkaServers);
    }

    @Bean
    public KafkaTopicsDataTracker kafkaTopicsDataTracker(KafkaAllTopicsConsumer kafkaAllTopicsConsumer,
                                                         CommandLineArgs commandLineArgs) {
        var kafkaTopicsDataTracker = new KafkaTopicsDataTracker(kafkaAllTopicsConsumer, commandLineArgs.maxTopicMessagesCount);
        kafkaTopicsDataTracker.start();
        return kafkaTopicsDataTracker;
    }

    @Bean(destroyMethod = "close")
    public KafkaBrokersTracker kafkaBrokersTracker(ZkClient zkClient) {
        var kafkaBrokersTracker = new KafkaBrokersTracker(zkClient);
        kafkaBrokersTracker.start();
        return kafkaBrokersTracker;
    }

    @Bean(destroyMethod = "close")
    public KafkaTopicsTracker kafkaTopicsTracker(ZkClient zkClient) {
        var kafkaTopicsTracker = new KafkaTopicsTracker(zkClient);
        kafkaTopicsTracker.start();
        return kafkaTopicsTracker;
    }

    @Bean
    public RestResource mainResource(CommandLineArgs commandLineArgs,
                                     HttpResponseFactory httpResponseFactory,
                                     KafkaProducerWrapper kafkaProducerWrapper,
                                     KafkaBrokersTracker kafkaBrokersTracker,
                                     KafkaTopicsTracker kafkaTopicsTracker,
                                     KafkaTopicsDataTracker kafkaTopicsDataTracker,
                                     KafkaUtils kafkaUtils) {
        return new RestResource(commandLineArgs.environment, httpResponseFactory, kafkaProducerWrapper,
                kafkaBrokersTracker, kafkaTopicsTracker, kafkaTopicsDataTracker, kafkaUtils);
    }
}
