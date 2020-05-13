package fi.hsl.transitlog.hfp.persisthfpdata;

import fi.hsl.common.hfp.proto.*;
import fi.hsl.common.pulsar.*;
import fi.hsl.eke.*;
import fi.hsl.transitlog.hfp.*;
import org.apache.pulsar.client.api.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

@Component
public class MessageProcessor implements IMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageProcessor.class);
    private final TransitDataSchemaWrapper transitdataSchemaWrapper;
    private DomainMappingWriter domainMappingWriter;

    public MessageProcessor(DomainMappingWriter writer, TransitDataSchemaWrapper transitdataSchemaWrapper) {
        this.domainMappingWriter = writer;
        this.transitdataSchemaWrapper = transitdataSchemaWrapper;
    }

    @Override
    public void handleMessage(Message message) throws Exception {
        if (transitdataSchemaWrapper.hasProtobufSchema(message)) {
            FiHslEke.EkeMessage data = EkeParser.newInstance().parseEkeMessage(message.getData());
            domainMappingWriter.process(message.getMessageId(), data);
        } else {
            log.warn("Invalid protobuf schema, expecting HfpData");
        }
        // Messages should not be acked already here but only after successful write
    }
}
