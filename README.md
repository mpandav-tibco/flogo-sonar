# Flogo SonarQube Plugin

A SonarQube plugin that provides static code analysis for **TIBCO Flogo** applications (`.flogo` files). It detects security vulnerabilities, reliability bugs, and maintainability issues across flows, triggers, connections, mappings, and properties.

## Features

- **68 built-in rules** covering security, reliability, maintainability, and test quality
- **Assertion-aware code coverage** via Flogo test files (`.flogotest`)
- **Custom metrics** — flows, activities, triggers, connections, properties, handlers
- Ships with a default **Flogo Way** quality profile with all rules enabled
- Works with SonarQube Community Edition 26.x+

## Quick Start

### Prerequisites

- Java 17+ and Maven 3.8+
- Docker (for running SonarQube)
- [SonarScanner CLI](https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner/) (or use the Docker image)

### 1. Build the Plugin

```bash
mvn clean package -DskipTests
```

This produces `target/sonar-flogo-plugin-1.0.0.jar`.

### 2. Start SonarQube with the Plugin

The included `docker-compose.yml` mounts the plugin JAR automatically:

```bash
docker compose up -d
```

Wait for SonarQube to start (~2 minutes), then open [http://localhost:9000](http://localhost:9000).

Default credentials: `admin` / `admin`

> **Tip:** Use `make docker-up` to build + start + wait in one command.

### 3. Configure Your Project

Create a `sonar-project.properties` file in your Flogo project root:

```properties
sonar.projectKey=my-flogo-project
sonar.projectName=My Flogo Project
sonar.projectVersion=1.0.0
sonar.sources=.
sonar.language=flogo
sonar.sourceEncoding=UTF-8
sonar.host.url=http://localhost:9000
sonar.token=<your-sonarqube-token>
```

Generate a token in SonarQube: **My Account → Security → Generate Token**.

Set `sonar.sources` to the directory containing your `.flogo` files.

### 4. Run the Scanner

**Using Docker (recommended):**

```bash
docker run --rm --network host \
  -v "$(pwd):/usr/src" \
  sonarsource/sonar-scanner-cli:latest
```

**Using local SonarScanner:**

```bash
sonar-scanner
```

### 5. View Results

Open your project in SonarQube at [http://localhost:9000](http://localhost:9000) to see issues, coverage, and metrics.

---

## Installing in an Existing SonarQube Instance

Copy the built JAR to your SonarQube `extensions/plugins/` directory and restart:

```bash
cp target/sonar-flogo-plugin-1.0.0.jar /path/to/sonarqube/extensions/plugins/
# Restart SonarQube
```

## Code Coverage

The plugin calculates **assertion-aware coverage** from Flogo test files (`.flogotest`). Each task/activity in a flow maps to a coverable line. A flow is only marked as covered if it has a test case **with at least one assertion** — tests that merely execute a flow without validating outputs do not count toward coverage.

Test files are auto-detected alongside `.flogo` files (e.g., `my-app.flogo` → `my-app.flogotest`).

You can create test files using the **Flogo Design Assistant** MCP tools:

```
create-test-suite  → creates a .flogotest file with a test suite
create-test-case   → adds a test case for a specific flow
add-assertion      → adds assertions to a test case
```

## Rules

### Security (21 rules)

| Severity | Rule | Description |
|----------|------|-------------|
| BLOCKER | `SecretInProperties` | Application properties should not contain plaintext secrets |
| CRITICAL | `ActivitySkipTLSVerify` | Activity should not skip TLS certificate verification |
| CRITICAL | `ConnectionCredential` | Connection has plaintext credentials |
| CRITICAL | `DisabledSSLVerification` | SSL verification should not be disabled |
| CRITICAL | `GrpcNoTLS` | gRPC trigger or activity should use TLS |
| CRITICAL | `HardcodedCredentials` | Credentials should not be hardcoded |
| CRITICAL | `HardcodedCryptoKey` | HMAC/crypto function uses hardcoded key in mapping |
| CRITICAL | `SQLConcatInMapping` | String concatenation in database activity input may enable SQL injection |
| CRITICAL | `SendMailNoTLS` | Send Mail activity should use TLS or SSL |
| CRITICAL | `SensitiveDataInLog` | Log activity may expose sensitive data |
| CRITICAL | `TriggerHardcodedCredentials` | Trigger settings contain hardcoded credentials |
| CRITICAL | `TriggerNoAuth` | REST triggers should have authentication |
| MAJOR | `CORSWildcard` | CORS should not allow all origins |
| MAJOR | `ConnectionNoTLS` | Connections should use TLS/SSL |
| MAJOR | `GraphQLIntrospection` | GraphQL introspection should be disabled in production |
| MAJOR | `InsecureConnection` | Connections should not use insecure HTTP scheme |
| MAJOR | `InsecurePropertyURL` | Property values should not use HTTP URLs |
| MAJOR | `MD5HashFunction` | Mapping uses MD5 hash which is cryptographically broken |
| MAJOR | `RegexInjectionRisk` | Mapping uses regexExtract with potentially dynamic pattern |
| MAJOR | `TriggerInsecureHTTP` | REST triggers should use HTTPS |
| MAJOR | `WebSocketNoTLS` | WebSocket trigger should use secure connection (WSS) |

### Reliability (14 rules)

| Severity | Rule | Description |
|----------|------|-------------|
| CRITICAL | `BrokenResolverReference` | Mapping references non-existent activity or flow variable |
| CRITICAL | `CircularLink` | Flow contains circular transition path |
| CRITICAL | `DuplicateFlowName` | Flow names should be unique |
| CRITICAL | `DuplicateHandlerPath` | Duplicate HTTP method and path on same trigger |
| CRITICAL | `EmptyTransitionCondition` | Conditional transition has empty expression |
| CRITICAL | `FlowUnreachableTask` | All tasks should be reachable from Start |
| CRITICAL | `MissingImport` | Activity references should have matching imports |
| CRITICAL | `OrphanHandler` | Trigger handler references non-existent flow |
| CRITICAL | `UnboundTriggerHandler` | Trigger handler must have a flow binding |
| MAJOR | `DuplicateActivityName` | Activity names should be unique within a flow |
| MAJOR | `DuplicateLink` | Duplicate transition between same activities |
| MAJOR | `FlowDeadEnd` | Task should not be a dead end |
| MAJOR | `FlowMissingReturn` | Flow should end with a Return activity |
| MAJOR | `MultipleTransitionsNoCondition` | Multiple outgoing transitions should have conditions |

### Test Quality (3 rules)

| Severity | Rule | Description |
|----------|------|-------------|
| MAJOR | `FlowNoTestCase` | Flow has no test case defined in the .flogotest file |
| MAJOR | `TestCaseNoAssertion` | Test case has no assertions to validate flow outputs |
| MINOR | `TestCaseEmptyInput` | Test case has empty or no input data |

### Maintainability (30 rules)

| Severity | Rule | Description |
|----------|------|-------------|
| MAJOR | `ConnectionTimeout` | Connections should have timeout configured |
| MAJOR | `DeprecatedFunction` | Mapping uses deprecated function |
| MAJOR | `EmptyFlow` | Flow has no meaningful activities |
| MAJOR | `FlowComplexity` | Flow has too many conditional branches |
| MAJOR | `FlowMissingErrorHandler` | Flow should have error handling |
| MAJOR | `FlowTooManyActivities` | Flow has too many activities |
| MAJOR | `HardcodedURL` | URLs should use application properties |
| MAJOR | `RestActivityTimeout` | REST activities should have a timeout configured |
| MAJOR | `RestErrorSilenced` | REST activity silently ignoring HTTP errors |
| MAJOR | `SleepActivity` | Sleep activity should not be used in production flows |
| MAJOR | `SubflowNoTimeout` | Subflow activity should have an execution timeout |
| MAJOR | `TriggerTooManyHandlers` | Trigger should not have too many handlers |
| MINOR | `AppNoDescription` | Application should have a description |
| MINOR | `ComplexMappingExpression` | Mapping expressions should not be overly complex |
| MINOR | `DefaultAppVersion` | Application should not use default version |
| MINOR | `EmptyActivityDescription` | Activities should have descriptions |
| MINOR | `EmptyActivityInput` | Activities should have input mappings configured |
| MINOR | `FlowNamingConvention` | Flow names should follow naming conventions |
| MINOR | `FlowNoDescription` | Flow should have a description |
| MINOR | `FlowNoLogging` | Flow has no logging activities |
| MINOR | `HardcodedPropertyURL` | Property values should not contain hardcoded URLs |
| MINOR | `NoRetryOnError` | External call activities should have retry configuration |
| MINOR | `PropertyNamingConvention` | Property names should follow naming conventions |
| MINOR | `TooManyProperties` | Application should not have too many properties |
| MINOR | `TransitionLabel` | Transitions should have labels |
| MINOR | `TriggerDefaultPort` | REST trigger using default port |
| MINOR | `TriggerNoDescription` | Trigger handlers should have descriptions |
| MINOR | `UnusedConnection` | Connections should be referenced by activities |
| MINOR | `UnusedFlow` | Flow is not referenced by any trigger handler |
| MINOR | `UnusedProperty` | Application properties should be referenced |

## Makefile Targets

| Target | Description |
|--------|-------------|
| `make build` | Build the plugin JAR |
| `make docker-up` | Build + start SonarQube + wait for ready |
| `make docker-down` | Stop SonarQube and remove volumes |
| `make scan` | Run SonarScanner on the project |
| `make clean` | Clean build artifacts and stop SonarQube |

## Tech Stack

- **SonarQube Plugin API** 11.4.0
- **Java** 17
- **Gson** 2.10.1
- **Maven** with sonar-packaging-maven-plugin

