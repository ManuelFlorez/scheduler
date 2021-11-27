package colpatria.schedulermicroservice;

import colpatria.schedulermicroservice.reactive.events.CustomReactiveMessageSender;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.java.Log;
import org.reactivecommons.async.impl.communications.ReactiveMessageSender;
import org.reactivecommons.async.impl.communications.TopologyCreator;
import org.reactivecommons.async.impl.config.ConnectionFactoryProvider;
import org.reactivecommons.async.impl.config.RabbitProperties;
import org.reactivecommons.async.impl.config.props.BrokerConfigProps;
import org.reactivecommons.async.impl.converters.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;
import reactor.util.retry.Retry;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.logging.Level;

@Log
@Configuration
public class RabbitmqConfig {

    private static final String SENDER_TYPE = "sender";

    @Value("${spring.application.name}")
    private String appName;

    private final String elkHost;
    private final String elkUserName;
    private final String elkPassword;
    private final String elkVH;
    private final Integer elkPort;

    public RabbitmqConfig(@Value("${spring.application.name}") String appName,
                            @Value("${elk.rabbitmq.host}") String elkHost,
                            @Value("${elk.rabbitmq.username}") String elkUserName,
                            @Value("${elk.rabbitmq.password}") String elkPassword,
                            @Value("${elk.rabbitmq.virtual_host}") String elkVH,
                            @Value("${elk.rabbitmq.port}") Integer elkPort) {
        this.appName = appName;
        this.elkHost = elkHost;
        this.elkUserName = elkUserName;
        this.elkPassword = elkPassword;
        this.elkVH = elkVH;
        this.elkPort = elkPort;
    }

    @Bean("INTEGRATION")
    @Primary
    public ConnectionFactoryProvider appConnectionFactoryNew(RabbitProperties properties) throws KeyManagementException,
            NoSuchAlgorithmException {
        final ConnectionFactory factory = new ConnectionFactory();
        PropertyMapper map = PropertyMapper.get();
        map.from(properties::determineHost).whenNonNull().to(factory::setHost);
        map.from(properties::determinePort).to(factory::setPort);
        map.from(properties::determineUsername).whenNonNull().to(factory::setUsername);
        map.from(properties::determinePassword).whenNonNull().to(factory::setPassword);
        map.from(properties::determineVirtualHost).whenNonNull().to(factory::setVirtualHost);
        map.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds)
                .to(factory::setRequestedHeartbeat);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        return () -> factory;
    }

    @Bean("senderIntegration")
    @Primary
    public ReactiveMessageSender reactiveMessageSender(@Qualifier("INTEGRATION") ConnectionFactoryProvider provider,
                                                       MessageConverter converter,
                                                       BrokerConfigProps brokerConfigProps,
                                                       RabbitProperties rabbitProperties) {
        final Mono<Connection> senderConnection = createConnectionMonoIntegration(provider.getConnectionFactory(),
                appName, SENDER_TYPE);
        ChannelPoolOptions channelPoolOptions = new ChannelPoolOptions();
        final PropertyMapper map = PropertyMapper.get();

        map.from(rabbitProperties.getCache().getChannel()::getSize).whenNonNull()
                .to(channelPoolOptions::maxCacheSize);

        final ChannelPool channelPool = ChannelPoolFactory.createChannelPool(senderConnection, channelPoolOptions);

        final Sender sender = RabbitFlux.createSender(new SenderOptions().channelPool(channelPool)
                .resourceManagementChannelMono(channelPool.getChannelMono().transform(Utils::cache)));

        return new ReactiveMessageSender(sender, brokerConfigProps.getAppName(), converter, new TopologyCreator(sender));
    }

    Mono<Connection> createConnectionMonoIntegration(@Qualifier("INTEGRATION") ConnectionFactory factory,
                                                     String connectionPrefix, String connectionType) {
        return Mono.fromCallable(() -> factory.newConnection(connectionPrefix + " " + connectionType))
                .doOnError(err -> log.log(Level.SEVERE,
                        "Error creating connection to RabbitMq Broker. Starting retry process...", err))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(300))
                        .maxBackoff(Duration.ofMillis(3000)))
                .cache();
    }

    @Bean("ELK")
    public ConnectionFactoryProvider appConnectionFactoryELK(RabbitProperties properties) throws KeyManagementException,
            NoSuchAlgorithmException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(elkHost);
        factory.setUsername(elkUserName);
        factory.setPassword(elkPassword);
        factory.setVirtualHost(elkVH);
        factory.setPort(elkPort);
        factory.setRequestedHeartbeat(1);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        return () -> factory;
    }

    @Bean("senderElk")
    @Primary
    public CustomReactiveMessageSender customReactiveSender(@Qualifier("ELK") ConnectionFactoryProvider provider,
                                                            MessageConverter converter, BrokerConfigProps brokerConfigProps,
                                                            RabbitProperties rabbitProperties) {

        final Mono<Connection> senderConnection = createConnectionMonoElk(provider.getConnectionFactory(), appName,
                "sender");
        final ChannelPoolOptions channelPoolOptions = new ChannelPoolOptions();
        final PropertyMapper map = PropertyMapper.get();

        map.from(rabbitProperties.getCache().getChannel()::getSize).whenNonNull()
                .to(channelPoolOptions::maxCacheSize);

        final ChannelPool channelPool = ChannelPoolFactory.createChannelPool(
                senderConnection,
                channelPoolOptions
        );

        final Sender sender = RabbitFlux.createSender(new SenderOptions()
                .channelPool(channelPool)
                .resourceManagementChannelMono(channelPool.getChannelMono()
                        .transform(Utils::cache)));

        return new CustomReactiveMessageSender(sender, brokerConfigProps.getAppName(), converter,
                new TopologyCreator(sender));
    }

    Mono<Connection> createConnectionMonoElk(@Qualifier("ELK") ConnectionFactory factory, String connectionPrefix,
                                             String connectionType) {
        return Mono.fromCallable(() -> factory.newConnection(connectionPrefix + " " + connectionType
                        + " elk"))
                .doOnError(err -> log.log(Level.SEVERE,
                        "Error creating connection to RabbitMq Broker. Starting retry process...", err))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(300))
                        .maxBackoff(Duration.ofMillis(3000)))
                .cache();
    }

}
