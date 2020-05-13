package fi.hsl.transitlog.hfp;

import com.google.protobuf.InvalidProtocolBufferException;
import fi.hsl.common.hfp.proto.Hfp;
import fi.hsl.eke.*;
import org.springframework.stereotype.Component;

@Component
public
class EkeDataParser {
    public FiHslEke.EkeMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return FiHslEke.EkeMessage.parseFrom(data);
    }
}
