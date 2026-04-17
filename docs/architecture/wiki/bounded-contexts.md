# Bounded contexts and data models

This page defines **domain boundaries** and **core aggregates** for the four stateless microservices in scope. Names align with the EWACP program and the OpenAPI / gRPC contracts in this repository.

## Product-aligned requirements (summary)

| Context | Primary responsibility | Consumers |
|---------|------------------------|-----------|
| **Workflow Engine** | Define and execute directed workflows (states, transitions, compensations); emit domain events for observers | Task Scheduler, Persistence (definitions), UI, audit |
| **Task Scheduler** | Schedule, delay, cancel, and retry units of work; bridge workflow intent to execution | Workflow Engine, workers, Persistence (execution records) |
| **Persistence Service** | Durable storage API for workflow definitions, execution snapshots, schedules, and audit metadata | All services; governed by retention and compliance policies |
| **Business Rules Engine** | Evaluate policies and rules (inputs → decisions) used by workflows and tasks | Workflow Engine, Task Scheduler |

Cross-cutting concerns (authentication, RBAC, rate limits) are enforced at the **API gateway**; services trust **OAuth 2.0 bearer tokens** (OpenID Connect) as described in each OpenAPI file.

---

## Workflow Engine (bounded context)

**Purpose:** Orchestrate business processes as versioned workflow definitions and running instances.

**Boundaries:**

- Owns workflow **definition** lifecycle (create, version, deprecate) and **instance** lifecycle (start, signal, complete, fail).
- Does **not** own low-level job queues or cron tables (delegated to Task Scheduler).
- Does **not** own raw blob storage for large artifacts (Persistence may store references only).

**Core aggregates (conceptual):**

- `WorkflowDefinition` — id, version, graph (nodes/edges), metadata.
- `WorkflowInstance` — definition ref, correlation id, current state, variables (references), status.
- `Transition` / `Signal` — external stimuli that advance or branch the instance.

**Upstream/downstream:**

- **Calls:** Task Scheduler (schedule work), Business Rules Engine (decision nodes), Persistence (load/save definitions and instance projections as needed).
- **Called by:** API clients, automation adapters.

---

## Task Scheduler (bounded context)

**Purpose:** Time-based and retry-based execution of tasks with idempotency and observability.

**Boundaries:**

- Owns **schedule** records, **run** attempts, delays, and cancellation.
- Does **not** define business workflow graphs (Workflow Engine).
- Does **not** implement rule evaluation (Business Rules Engine).

**Core aggregates:**

- `Schedule` — cron or one-shot trigger, timezone, payload reference, owner service.
- `TaskRun` — schedule ref, attempt count, next run, last result, dead-letter state.

**Upstream/downstream:**

- **Calls:** Workers (via callbacks/webhooks), Persistence for execution logs when required.
- **Called by:** Workflow Engine, operational tools.

---

## Persistence Service (bounded context)

**Purpose:** Centralized persistence API for workflow and scheduling data with tenant isolation and audit trails.

**Boundaries:**

- Owns **storage contracts** (schemas, versioning, query patterns) for shared platform data.
- Does **not** embed orchestration or scheduling logic.
- Enforces **tenant scoping** and optional **PII tagging** on stored entities.

**Core aggregates (storage-oriented):**

- `TenantScopedEntity` — base pattern for all persisted rows.
- `WorkflowDefinitionRecord`, `WorkflowInstanceSnapshot`, `ScheduleRecord`, `AuditEvent` — physical projections (exact tables are implementation detail).

**Upstream/downstream:**

- **Called by:** Workflow Engine, Task Scheduler, Business Rules Engine (for decision audit), compliance jobs.

---

## Business Rules Engine (bounded context)

**Purpose:** Evaluate declarative rules and return structured decisions for workflows and tasks.

**Boundaries:**

- Owns **rule sets**, **versions**, and **evaluation** semantics.
- Does **not** persist workflow instance state (Workflow Engine / Persistence).
- Stateless per request; optional caching is an implementation detail.

**Core aggregates:**

- `RuleSet` — id, version, input schema, output schema.
- `EvaluationRequest` / `EvaluationResult` — traceable decision with optional explanation.

**Upstream/downstream:**

- **Called by:** Workflow Engine (gateways), Task Scheduler (pre-flight checks), integration adapters.

---

## Shared concepts

- **Correlation ID** — propagated across REST/gRPC calls for tracing.
- **Tenant ID** — required on mutating APIs unless noted otherwise.
- **Idempotency-Key** — supported on schedule and workflow start operations.

See `docs/architecture/plantuml/` for visual component and data-model diagrams derived from this model.
