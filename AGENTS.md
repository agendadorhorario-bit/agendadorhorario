# AGENTS Guide

## Project Snapshot
- Language: Java (single-source style).
- Build system: none configured (`pom.xml`, `build.gradle`, and Gradle wrapper are absent).
- Entry point: `src/Main.java` uses a top-level `void main()`.
- IDE metadata: `agendador.iml` defines `src/` as the only source root.

## Current Architecture
- The codebase is currently a single-file app (`src/Main.java`) with linear console output flow:
  1. print welcome message
  2. iterate from 1 to 5
  3. print each value
- There are no service layers, modules, persistence, HTTP endpoints, or external integrations yet.

## Run and Debug Workflow
- Primary verified run command from project root:
  - `java src/Main.java`
- IntelliJ workflow is valid for this repo shape (single source root in `agendador.iml`).
- If you introduce a build tool (Maven/Gradle), add and document its commands here and treat that as the new default workflow.

## Code Conventions Observed
- Keep Java source files under `src/` (matches module source root in `agendador.iml`).
- Console output in existing code is done with `IO.println(...)` in `src/Main.java`; keep behavior changes explicit when modifying output style.
- Existing code uses the default-package style (no `package` declaration).

## Repository Conventions
- Respect ignore rules in `.gitignore` for IDE/build artifacts (for example `out/`, `.idea` metadata, `.vscode/`).
- This repository currently has no test directory or test runner; if tests are added, co-locate and document the exact command used to run them.

