package ru.malygin.indexer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.service.DefaultQueueDeclareService;
import ru.malygin.helper.service.DataTransceiver;
import ru.malygin.helper.senders.LogSender;
import ru.malygin.indexer.service.LemmaService;

@Slf4j
@RequiredArgsConstructor
@Service
public class LemmaTransceiverService implements DataTransceiver {

    private final LemmaService lemmaService;
    private final LogSender logSender;
    private final RabbitTemplate rabbitTemplate;
    private final DefaultQueueDeclareService declareService;

    @Override
    @RabbitListener(queues = "#{properties.getCommon().getRequest().getLemmaRoute()}")
    public Long dataRequestListen(DataRequest dataRequest) {
        DataTransceiver.super.dataRequestReceiveLog(logSender, dataRequest);
        return lemmaService
                .getCountBySiteIdAndAppUserId(dataRequest.getSiteId(), dataRequest.getAppUserId())
                .doOnSuccess(lemmaCount -> send(lemmaCount, dataRequest))
                .block();
    }

    @Override
    public void send(Long itemCount,
                     DataRequest dataRequest) {
        declareService.createQueue(dataRequest.getDataQueue(), null);
        lemmaService
                .findAllBySiteIdAndAppUserId(dataRequest.getSiteId(), dataRequest.getAppUserId())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(page -> rabbitTemplate.convertAndSend(dataRequest.getDataQueue(), page))
                .doOnComplete(() -> {
                    DataTransceiver.super.dataSendLog(logSender, dataRequest, itemCount);
                    declareService.removeQueue(dataRequest.getDataQueue());
                })
                .subscribe();
    }
}
