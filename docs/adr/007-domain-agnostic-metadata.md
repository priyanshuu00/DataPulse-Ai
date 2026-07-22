# ADR 007: Domain-Agnostic Metadata Architecture

## Status
Accepted

## Context
The long-term goal of DataPulse AI is to act as a production-grade Enterprise Data Intelligence Platform (similar to Databricks Unity Catalog, Collibra, or Apache Atlas). Building the platform around a specific business vertical (like Financial Transactions) limits its applicability and doesn't showcase data cataloging skills.

## Decision
We will redesign the Core Domain around metadata management. The primary entities will be `Dataset`, `Workspace`, `DataSource`, `DatasetSchema`, and `Pipeline`. The actual raw data processing will treat data as generic formats (Avro/Parquet) while the platform manages the metadata, quality reports, and schemas.

## Consequences
- The core domain model is highly extensible and can support Retail, Banking, Healthcare, etc.
- Emphasizes Data Governance, Schema Evolution, and Data Lineage.
- Requires building generic ingestion and validation pipelines that rely on the `DatasetSchema` rather than strongly-typed POJOs for the data payload.
