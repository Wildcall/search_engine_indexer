package ru.malygin.indexer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.senders.LogSender;
import ru.malygin.helper.service.*;
import ru.malygin.indexer.model.Page;
import ru.malygin.indexer.model.Task;
import ru.malygin.indexer.model.entity.Index;
import ru.malygin.indexer.model.entity.Lemma;
import ru.malygin.indexer.service.impl.IndexerService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public Map<String, Class<?>> idClassMap() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("NodeTask", Task.class);
        map.put("Page", Page.class);
        map.put("Lemma", Lemma.class);
        map.put("Index", Index.class);
        map.put("DataRequest", DataRequest.class);
        log.info("[o] Configurate idClassMap in application");
        return map;
    }

    @Bean
    public boolean declareQueue(DefaultQueueDeclareService defaultQueueDeclareService,
                                SearchEngineProperties properties) {
        SearchEngineProperties.Common.Request request = properties
                .getCommon()
                .getRequest();
        SearchEngineProperties.Common.Task taskProp = properties
                .getCommon()
                .getTask();
        defaultQueueDeclareService.createQueue(taskProp.getRoute(), taskProp.getExchange());
        defaultQueueDeclareService.createQueue(request.getPageRoute(), request.getExchange());
        defaultQueueDeclareService.createQueue(request.getLemmaRoute(), request.getExchange());
        defaultQueueDeclareService.createQueue(request.getIndexRoute(), request.getExchange());
        return true;
    }

    @Bean
    public TaskReceiver<Task> taskReceiver(LogSender logSender,
                                           IndexerService indexerService) {
        log.info("[o] Create TaskReceiver in application");
        return new DefaultTaskReceiver<>(logSender, indexerService);
    }

    @Bean
    public DataReceiver dateReceiver(RabbitTemplate rabbitTemplate,
                                     LogSender logSender,
                                     DefaultTempListenerContainerFactory defaultTempListenerContainerFactory,
                                     ObjectMapper objectMapper,
                                     SearchEngineProperties properties) {
        log.info("[o] Create DefaultDataReceiver in application");
        return new DefaultDataReceiver(rabbitTemplate, logSender, defaultTempListenerContainerFactory, objectMapper,
                                       properties);
    }

    @Bean
    public DefaultTempListenerContainerFactory defaultTempListenerContainerFactory(SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory,
                                                                                   DefaultQueueDeclareService defaultQueueDeclareService) {
        RabbitListenerEndpointRegistry registry = new RabbitListenerEndpointRegistry();
        log.info("[o] Create DefaultTempListenerContainerFactory in application");
        return new DefaultTempListenerContainerFactory(simpleRabbitListenerContainerFactory, registry,
                                                       defaultQueueDeclareService);
    }
}
