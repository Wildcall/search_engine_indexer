package ru.malygin.indexer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.malygin.helper.enums.TaskState;
import ru.malygin.helper.model.TaskCallback;
import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.senders.LogSender;
import ru.malygin.helper.senders.TaskCallbackSender;
import ru.malygin.helper.service.DataReceiver;
import ru.malygin.helper.service.NodeMainService;
import ru.malygin.indexer.indexer.Indexer;
import ru.malygin.indexer.model.Task;
import ru.malygin.indexer.service.IndexService;
import ru.malygin.indexer.service.LemmaService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexerService implements NodeMainService<Task> {

    private final Map<Task, Indexer> currentRunningTasks = new ConcurrentHashMap<>();
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final Indexer.Builder indexerBuilder;
    private final LogSender logSender;
    private final DataReceiver dataReceiver;
    private final TaskCallbackSender taskCallbackSender;

    @Override
    public void start(Task task) {
        Long siteId = task.getSiteId();
        Long appUserId = task.getAppUserId();

        //  @formatter:off
        if (currentRunningTasks.get(task) != null) {
            publishErrorCallbackEvent(task, 6001);
            return;
        }

        if (currentRunningTasks.keySet().stream().anyMatch(t -> t.getPath().equalsIgnoreCase(task.getPath()))) {
            publishErrorCallbackEvent(task, 6002);
            return;
        }

        DataRequest pageDataRequest = dataReceiver.createPageDataRequest(task.getId(),
                                           task.getSiteId(),
                                           task.getAppUserId());
        if (!dataReceiver.requestData(pageDataRequest)) {
            publishErrorCallbackEvent(task, 6003);
            return;
        }
        //  @formatter:on

        lemmaService
                .deleteAllBySiteIdAndAppUserId(siteId, appUserId)
                .then(indexService.deleteAllBySiteIdAndAppUserId(siteId, appUserId))
                .doOnSuccess(sink -> {
                    Indexer indexer = indexerBuilder.build();
                    indexer.start(task, pageDataRequest, currentRunningTasks);
                    currentRunningTasks.put(task, indexer);
                })
                .subscribe();
    }

    @Override
    public void stop(Task task) {
        Indexer indexer = currentRunningTasks.get(task);
        if (indexer == null) {
            publishErrorCallbackEvent(task, 6004);
            return;
        }
        indexer.stop();
        currentRunningTasks.remove(task);
    }

    private void publishErrorCallbackEvent(Task task,
                                           int code) {
        logSender.error("INDEXER ERROR / Id: %s / Code: %s", task.getId(), code);
        TaskCallback callback = new TaskCallback(task.getId(), TaskState.ERROR, null, null);
        taskCallbackSender.send(callback);
    }
}
