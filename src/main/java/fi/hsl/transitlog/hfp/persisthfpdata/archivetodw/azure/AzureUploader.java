package fi.hsl.transitlog.hfp.persisthfpdata.archivetodw.azure;


import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public
class AzureUploader {
    final AzureBlobClient azureBlobClient;

    @Autowired
    public AzureUploader(AzureBlobClient azureBlobClient) {
        this.azureBlobClient = azureBlobClient;
    }

    public AzureUploadTask uploadBlob(String filePath) {
        //Register as task for the asynchronous uploader
        return new AzureUploadTask(azureBlobClient, filePath).run();
    }


}
