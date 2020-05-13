package fi.hsl.transitlog.hfp.persisthfpdata;

import fi.hsl.common.pulsar.*;
import fi.hsl.eke.*;
import fi.hsl.transitlog.hfp.domain.*;
import fi.hsl.transitlog.hfp.persisthfpdata.archivetodw.*;
import lombok.extern.slf4j.*;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import javax.persistence.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class DomainMappingWriter {
    final Map<MessageId, EkeData> eventQueue;
    private final DumpService dumpTask;
    private final DWUpload DWUpload;
    ScheduledExecutorService scheduler;
    private EntityManager entityManager;
    private PulsarApplication pulsarApplication;
    private Consumer<byte[]> consumer;


    @Autowired
    DomainMappingWriter(DWUpload dwUpload, PulsarApplication pulsarApplication, EntityManager entityManager, DumpService dumpTask) {
        this.DWUpload = dwUpload;
        eventQueue = new ConcurrentHashMap<>();
        this.dumpTask = dumpTask;
        this.entityManager = entityManager;
        this.pulsarApplication = pulsarApplication;
        this.consumer = pulsarApplication.getContext().getConsumer();
    }

    void process(MessageId msgId, FiHslEke.EkeMessage data) throws IOException, ParseException {
        final EkeData ekedata = new EkeData(data);
        eventQueue.put(msgId, ekedata);
        DWUpload.uploadBlob(ekedata);
    }

    @Scheduled(fixedRateString = "${application.dumpInterval}")
    @Async
    public void attemptDump() {
        try {
            List<MessageId> dumpedMessagedIds = dumpTask.dump(eventQueue);
            ackMessages(dumpedMessagedIds);
        } catch (Exception e) {
            log.error("Failed to check results, closing application", e);
            close(true);
        }
    }

    private void ackMessages(List<MessageId> messageIds) {
        for (MessageId msgId : messageIds) {
            ack(msgId);
        }
    }

    private void ack(MessageId received) {
        consumer.acknowledgeAsync(received)
                .exceptionally(throwable -> {
                    log.error("Failed to ack Pulsar message", throwable);
                    return null;
                })
                .thenRun(() -> {
                });
    }

    void close(boolean closePulsar) {
        log.warn("Closing MessageProcessor resources");
        scheduler.shutdown();
        log.info("Scheduler shutdown finished");
        if (closePulsar && pulsarApplication != null) {
            log.info("Closing also Pulsar application");
            pulsarApplication.close();
        }
    }
}
