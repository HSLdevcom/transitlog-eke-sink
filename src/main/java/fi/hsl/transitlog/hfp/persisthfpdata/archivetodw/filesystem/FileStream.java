package fi.hsl.transitlog.hfp.persisthfpdata.archivetodw.filesystem;

import fi.hsl.transitlog.hfp.domain.*;
import fi.hsl.transitlog.hfp.persisthfpdata.archivetodw.*;
import org.apache.commons.io.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

public
class FileStream {
    private final String ekeFolder;
    private CSVMapper csvMapper;
    private Map<String, DWFile> dwFiles;

    public FileStream(CSVMapper format, String filePath) {
        this.csvMapper = format;
        this.dwFiles = new ConcurrentHashMap<>();
        this.ekeFolder = filePath;
    }

    public Set<DWFile> readDWFiles(String filePath) throws ParseException {
        File file = new File(filePath);
        if (!file.exists()) {
            return ConcurrentHashMap.newKeySet();
        }
        Set<DWFile> dwFiles = ConcurrentHashMap.newKeySet();
        Iterator<File> fileIterator = FileUtils.iterateFiles(new File(filePath), null, true);
        while (fileIterator.hasNext()) {
            File dwFile = fileIterator.next();
            dwFiles.add(new DWFile(dwFile));
        }
        return dwFiles;
    }
    public DWFile writeEvent(EkeData ekeData) throws IOException, ParseException {
        DWFile dwFile = new DWFile(ekeData, ekeFolder);
        String filePath = dwFile.getFilePath();
        if (dwFiles.containsKey(filePath)) {
            dwFile = dwFiles.get(filePath);
        } else {
            dwFiles.put(filePath, dwFile);
        }
        dwFile.writeEvent(ekeData, csvMapper);
        return dwFile;
    }

}
