---

description: Task list for Account Report CQRS microservice implementation
---

# Tasks: Account Report CQRS

**Input**: Design documents from `/specs/001-account-report-cqrs/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Gradle project**: `services/ms-reportes/`
- **Application layer**: `services/ms-reportes/application/src/main/java/.../usecase/`
- **Infrastructure layer**: `services/ms-reportes/infrastructure/src/main/java/.../adapter/`
- **Domain layer**: `services/ms-reportes/domain/src/main/java/.../model/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create Gradle project structure `services/ms-reportes/` with settings.gradle
- [X] T002 Configure build.gradle with Spring Boot 4.x, Kafka, PostgreSQL dependencies
- [X] T003 [P] Configure application.yml with Kafka and PostgreSQL settings
- [X] T004 Create base package structure (com.zevaguillo.msreportes)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Create ReporteCliente entity in services/ms-reportes/domain/src/main/java/.../model/ReporteCliente.java
- [X] T006 Create ReporteCuenta entity in services/ms-reportes/domain/src/main/java/.../model/ReporteCuenta.java
- [X] T007 Create ReporteMovimiento entity in services/ms-reportes/domain/src/main/java/.../model/ReporteMovimiento.java
- [X] T008 Create ProcessedEvent entity in services/ms-reportes/domain/src/main/java/.../model/ProcessedEvent.java
- [X] T009 [P] Configure JPA repositories for all entities in services/ms-reportes/infrastructure/src/main/java/.../repository/
- [X] T010 Create schema initialization SQL for read model in infra/init-schema.sql
- [X] T011 Configure actuator endpoints and health checks

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Generate Account Report (Priority: P1) 🎯 MVP

**Goal**: Endpoint GET /reportes returns cliente, cuentas, movimientos with paginación

**Independent Test**: GET /reportes?clienteId=X&fechaInicio=Y&fechaFin=Z returns complete report with pagination

### Implementation for User Story 1

- [X] T012 [P] [US1] Create DTOs (ReporteResponse, ClienteDTO, CuentaDTO, MovimientoDTO, PaginationDTO) in services/ms-reportes/application/src/main/java/.../dto/
- [X] T013 [P] [US1] Create ReporteMapper in services/ms-reportes/application/src/main/java/.../mapper/
- [X] T014 [US1] Create ConsultarReporteUseCase interface in services/ms-reportes/application/src/main/java/.../port/in/
- [X] T015 [US1] Implement ConsultarReporteUseCaseImpl in services/ms-reportes/application/src/main/java/.../usecase/
- [X] T016 [US1] Create ReporteController in services/ms-reportes/infrastructure/src/main/java/.../adapter/rest/
- [X] T017 [US1] Add validation for query parameters (UUID, date range, pagination)
- [X] T018 [US1] Add error handling (400, 404 responses)
- [X] T019 [US1] Add logging for report queries

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Real-Time Report Updates (Priority: P2)

**Goal**: Kafka consumers process events and update read model incrementally

**Independent Test**: After processing MovimientoRegistradoEvent, new movement appears in GET /reportes

### Implementation for User Story 2

- [X] T020 [P] [US2] Create event DTOs (ClienteCreadoEvent, CuentaCreadaEvent, MovimientoRegistradoEvent) in services/ms-reportes/application/src/main/java/.../dto/event/
- [X] T021 [P] [US2] Create EventProcessor interface in services/ms-reportes/application/src/main/java/.../port/in/
- [X] T022 [US2] Create ClienteEventHandler in services/ms-reportes/application/src/main/java/.../usecase/handler/
- [X] T023 [US2] Create CuentaEventHandler in services/ms-reportes/application/src/main/java/.../usecase/handler/
- [X] T024 [US2] Create MovimientoEventHandler in services/ms-reportes/application/src/main/java/.../usecase/handler/
- [X] T025 [US2] Create Kafka consumer configuration in services/ms-reportes/infrastructure/src/main/java/.../adapter/kafka/
- [X] T026 [US2] Implement idempotency check in event processing
- [X] T027 [US2] Add retry and DLQ configuration for failed events
- [X] T028 [US2] Add logging for event processing

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - High Volume Report Queries (Priority: P3)

**Goal**: System handles 1000+ concurrent queries with <500ms p95 latency

**Independent Test**: Load test with 1000 concurrent requests completes within SLA

### Implementation for User Story 3

- [X] T029 [P] [US3] Add database indexes for query performance
- [X] T030 [US3] Add connection pool configuration (HikariCP)
- [-] T031 [US3] Add caching layer for frequently accessed reports (SKIPPED - no caching)
- [-] T032 [US3] Add Prometheus metrics for query latency (SKIPPED - no Prometheus)
- [X] T033 [US3] Add correlation ID propagation
- [-] T034 [US3] Add load testing configuration (SKIPPED by user)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T035 [P] Add integration tests for full flow
- [X] T036 Update quickstart.md with testing instructions
- [X] T037 Add API documentation (OpenAPI)
- [-] T038 Run performance validation tests (SKIPPED)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- All user story tasks marked [P] can run in parallel
- Entities can be created in parallel (T005-T008)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence