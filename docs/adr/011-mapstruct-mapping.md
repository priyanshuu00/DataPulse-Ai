# ADR 011: MapStruct for Entity-Domain Mapping

## Status
Accepted

## Context
We are strictly separating the JPA persistence layer (`DatasetEntity`) from the Core Domain (`Dataset` record). Passing data between these layers requires object mapping. Hand-writing these mappers is tedious and error-prone. Using reflection-based mappers (like ModelMapper) impacts performance.

## Decision
We will use **MapStruct** to generate compile-time mapper classes (e.g., `DatasetMapper`). 

## Consequences
- Guaranteed type-safety at compile time.
- Zero reflection overhead at runtime, maximizing performance.
- Clean separation between framework-heavy Entities and pure Java Domain Records.
