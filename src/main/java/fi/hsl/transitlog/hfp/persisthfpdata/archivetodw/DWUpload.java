package fi.hsl.transitlog.hfp.persisthfpdata.archivetodw;

import fi.hsl.transitlog.hfp.domain.*;

import java.io.*;
import java.text.*;

/**
 * Stores filesystem collected csv dumps to Azure blobstorage
 */
public interface DWUpload {
    String uploadBlob(EkeData event) throws IOException, ParseException;
}
