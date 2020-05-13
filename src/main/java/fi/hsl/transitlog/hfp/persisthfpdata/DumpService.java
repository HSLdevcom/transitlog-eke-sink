package fi.hsl.transitlog.hfp.persisthfpdata;

import fi.hsl.transitlog.hfp.domain.*;
import fi.hsl.transitlog.hfp.domain.repositories.*;
import lombok.extern.slf4j.*;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Service
@Slf4j
public class DumpService {

    @Autowired
    private EkeRepository ekeRepository;

    @Transactional
    List<MessageId> dump(Map<MessageId, EkeData> eventQueue) {
        log.debug("Saving results");
        Map<MessageId, EkeData> eventQueueCopy;
        synchronized (eventQueue) {
            eventQueueCopy = eventQueue.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            eventQueue.clear();
        }
        log.info("To write event count: {}", eventQueueCopy.size());
        List<EkeData> events = new ArrayList<>(eventQueueCopy.values());
        ekeRepository.saveAll(events);
        return new ArrayList<>(eventQueueCopy.keySet());
    }

}
