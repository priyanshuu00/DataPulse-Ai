# DataPulse AI

**Tagline:** AI-Powered Enterprise Data Intelligence Platform

## Overview
DataPulse AI is a production-grade enterprise data platform designed to ingest, process, and analyze massive volumes of heterogeneous data in real-time and batch modes. It bridges the gap between raw data streams and actionable AI-driven insights.

## Architecture
The platform is built using a Clean Architecture modular monolith in Java 21 and Spring Boot 3.3.x, backed by Apache Kafka (KRaft), PostgreSQL, and MinIO.

For detailed architectural decisions, refer to our [Architecture Decision Records (ADRs)](adr/).

## Getting Started
Please see the [Development Setup Guide](setup.md) for local infrastructure initialization using Docker Compose.

### Quick Start
```bash
make up
make build
```
