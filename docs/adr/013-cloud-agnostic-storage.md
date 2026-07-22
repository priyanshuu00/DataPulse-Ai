# ADR 013: Deterministic Object Keys and Cloud-Agnostic Storage

## Status
Accepted

## Context
When ingesting data files, relying on the original filename provided by the user can lead to collisions, security vulnerabilities, and scaling issues (hot partitions in object storage). Furthermore, hardcoding AWS S3 or MinIO specifics into the business logic prevents multi-cloud deployments.

## Decision
1. **Deterministic Keys**: We will generate object keys algorithmically based on the dataset hierarchy and timestamp (e.g., `datasets/{workspaceId}/{datasetId}/{versionId}/{year}/{month}/data.ext`). The original filename is preserved purely for display metadata in the PostgreSQL database.
2. **Storage Abstraction**: We introduce an `ObjectStoragePort` in the core domain that returns a rich `StorageObject` value object. The infrastructure layer provides a `MinioStorageAdapter` using the AWS SDK.

## Consequences
- No file collisions or path traversal vulnerabilities.
- Guaranteed scalability in object storage (keys are distributed).
- Zero refactoring required in the domain logic when moving from MinIO to AWS S3, Azure Blob, or Google Cloud Storage.
