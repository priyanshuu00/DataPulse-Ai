# ADR 014: Checksums and Immutable Dataset Versions

## Status
Accepted

## Context
Data engineering pipelines require reproducible, deterministic input. Ingesting raw data and allowing it to be mutated or overwritten breaks data lineage, corrupts downstream tables, and makes debugging impossible. Additionally, we need a way to verify file integrity over time.

## Decision
1. **Immutable Dataset Versions**: Every file upload creates a new, immutable `DatasetVersion` pointing to a distinct physical file in object storage. `DatasetVersions` transition through lifecycle states (e.g., `UPLOADING`, `UPLOADED`, `READY`).
2. **SHA-256 Checksums**: Every file will have a SHA-256 checksum calculated during ingestion, stored alongside the version metadata.

## Consequences
- Absolute reproducibility for downstream processing tasks (Spark/AI).
- Checksums guarantee physical data integrity and allow for duplicate file detection in future features.
- Enables event-driven asynchronous processing based on `VersionStatus`.
