package fi.hsl.transitlog.hfp.domain;

import fi.hsl.eke.*;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
public class EkeData {
    private final byte[] data;
    //Seconds since UNIX epoch
    private final long capturedTime;
    private final FiHslEke.EkeMessage.CapturedType capturedType;

    public EkeData(FiHslEke.EkeMessage data) {
        this.data = data.getCapturedData().toByteArray();
        this.capturedTime = data.getCaptureTime().getSeconds();
        this.capturedType = data.getCapturedType();
    }
}
