package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.StorageObject;
import java.io.InputStream;

public interface ObjectStoragePort {
    StorageObject uploadFile(String bucket, String objectKey, InputStream stream, String contentType, long size);
}
