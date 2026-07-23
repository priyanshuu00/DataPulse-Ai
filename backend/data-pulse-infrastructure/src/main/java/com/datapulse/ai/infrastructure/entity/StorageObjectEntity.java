package com.datapulse.ai.infrastructure.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class StorageObjectEntity {
    
    private String bucket;
    private String objectKey;
    private String uri;
    private String etag;
    private Long size;
    private String contentType;
    private String checksum;
    private String provider;

    public StorageObjectEntity() {}

    public StorageObjectEntity(String bucket, String objectKey, String uri, String etag, Long size, String contentType, String checksum, String provider) {
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.uri = uri;
        this.etag = etag;
        this.size = size;
        this.contentType = contentType;
        this.checksum = checksum;
        this.provider = provider;
    }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
