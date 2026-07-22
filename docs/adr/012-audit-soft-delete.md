# ADR 012: Audit Columns, Soft Deletes, and UUID Primary Keys

## Status
Accepted

## Context
Enterprise data intelligence platforms require strict tracking of metadata lifecycle events. Standard physical deletes destroy historical context, breaking downstream data lineage. Auto-increment BIGINT keys are predictable and make multi-tenant scaling harder.

## Decision
1. **UUID Primary Keys**: Every entity uses a UUID (v4) to guarantee global uniqueness and obscure counts.
2. **Audit Columns**: Every table must include `created_at`, `updated_at`, `created_by`, and `updated_by`.
3. **Soft Deletes**: We utilize a `deleted_at` timestamp. `DatasetEntity` is annotated with `@SQLRestriction("deleted_at IS NULL")` so that Hibernate automatically filters out deleted records in all read queries.

## Consequences
- Uncompromised data lineage and historical reporting.
- Simpler cross-region data replication (UUIDs don't collide).
- Requires careful index design to ensure queries scanning for `deleted_at IS NULL` remain performant.
