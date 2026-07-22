# ADR 018: Hybrid Deterministic & AI Data Quality Architecture

## Status
Accepted

## Context
When introducing AI into an Enterprise Data Engineering pipeline for Data Quality rule generation, blindly trusting Large Language Models (LLMs) poses severe reliability and security risks. LLMs can hallucinate, return malformed JSON, suffer from rate limiting, or compromise sensitive data if raw datasets are uploaded.

## Decision
1. **Hybrid Architecture**: We implement a `DeterministicRuleEngine` that executes first, enforcing standard offline data quality checks (e.g., UUID -> Unique, Not Null enforcement). The AI serves as an *enhancement layer* that runs subsequently to suggest business rules.
2. **Metadata Only**: We use a dedicated `PromptFactory` to construct LLM prompts using only schema metadata (column names, inferred data types, null counts). Raw dataset rows are **never** sent to the LLM.
3. **Strict Validation & Fallback**: AI outputs are strictly deserialized from JSON. If the LLM hallucinates or returns malformed data, the system gracefully falls back to returning only the deterministic rules.
4. **Mock AI Default**: The `AiServicePort` is implemented via a `MockAiAdapter` by default, ensuring the platform remains fully functional for portfolio demonstrations without requiring third-party API keys.

## Consequences
- Data Quality generation is highly resilient; it will never fail completely due to an external AI service outage.
- No sensitive user data is ever exposed to third-party AI providers.
- Future LLM integrations (OpenAI, Anthropic, local LLaMA) can be hot-swapped by simply implementing the `AiServicePort` without changing any Core Domain logic.
