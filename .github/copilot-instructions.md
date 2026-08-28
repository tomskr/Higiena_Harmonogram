# Copilot instructions for Higiena Harmonogram

## Project architecture

This repository is a two-part system:

- Frontend: `HigienaHarmonogramFrontend`
  - Angular application
  - Responsible for UI, calendar, employee list, shift management, routing, forms, and API calls
  - Uses Angular services for backend communication
- Backend: `HigienaHarmonogramBackend`
  - Spring Boot application in Java
  - Exposes REST APIs under `/api`
  - Main modules are employees and shifts
- Data model:
  - `HgEmployee` for employees
  - `HgShifts` for work shifts
  - Each shift belongs to an employee and contains date, type, duration, and holiday flag
- Integration pattern:
  - Frontend calls backend REST endpoints
  - Backend validates and persists data
  - Frontend should not assume backend logic exists unless it is already implemented

## Working rules

1. Use the latest appropriate patterns and APIs for Angular and TypeScript.
2. Prefer modern Angular patterns over legacy or deprecated solutions.
3. Do not add or modify tests unless the user explicitly asks for them.
4. Do not write backend Java changes unless the user explicitly requests it or clearly asks for backend work.
5. If a frontend issue depends on backend support, do not silently implement a backend workaround. State clearly:
   - what does not work,
   - why it requires backend changes,
   - which backend endpoint or logic is missing or needs to be added.
6. Keep changes focused on the requested frontend task.
7. If a feature is partially implemented in frontend but the backend is missing, describe the exact backend change required instead of changing Java code without approval.
8. Do not invent API contracts or endpoints that are not already present.
9. If the app is currently not fully functional due to missing backend logic, say so explicitly and describe the expected backend behavior.

## Default behavior

- Prefer frontend-only changes in `HigienaHarmonogramFrontend`.
- Treat Java files in `HigienaHarmonogramBackend` as read-only unless the user directly asks for backend implementation.
- When reporting an issue, explain whether it is caused by frontend logic, backend API contract, or missing backend support.

## Communication style

- Be explicit when something requires backend support.
- Say: "This does not work because the backend currently does not expose/accept X" instead of changing the backend without permission.
- If asked to fix a bug without backend permission, provide a frontend-safe diagnosis and a precise list of required backend changes.

## Examples

- If a form cannot save because the API endpoint is missing: say which endpoint is needed and what payload it should accept.
- If a calendar entry cannot be edited because the backend does not allow updating shift data: explain that the frontend is waiting on `PUT /api/shifts/{id}` or equivalent backend support.
- Do not implement Java code by default just to make the UI appear functional.
