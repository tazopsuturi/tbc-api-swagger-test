# Petstore API Test Automation

BDD test framework for the [Swagger Petstore](https://petstore.swagger.io/) REST API, built with
**RestAssured**, **Cucumber 7** (JUnit Platform), **AssertJ** and **Allure**.

## Prerequisites

- **JDK 17+** on the `PATH`, or `JAVA_HOME` pointing at it (check with `java -version`)
- **Internet access** – tests run against the public `https://petstore.swagger.io/v2`
- **Maven** – not required; the bundled Maven Wrapper (`mvnw` / `mvnw.cmd`) downloads it on first run
- **Allure CLI** – optional; `allure:serve` downloads it automatically
- **IDE (optional)** – models use [Lombok](https://projectlombok.org/); IntelliJ supports it out of the box, just accept the "enable annotation processing" prompt

## Setup

```bash
git clone <repository-url>
cd tbc_swagger_api_project
./mvnw test-compile          # downloads dependencies and compiles; on Windows use mvnw.cmd
```

Defaults live in `src/main/resources/config.properties`; any key can be overridden with `-D<key>=<value>`.

## Running

On Windows (cmd/PowerShell) replace `./mvnw` with `mvnw.cmd`.

```bash
./mvnw test                                        # all scenarios
./mvnw test -Dcucumber.filter.tags="@smoke"        # by tag: @pet @store @positive @negative @create @read @update @delete @e2e
./mvnw test -Dapi.logging.enabled=true             # log full requests/responses to the console
./mvnw test -Dbase.uri=http://localhost:8080/v2    # point at another Petstore instance
```

Reports:
- Cucumber HTML: `target/cucumber-reports/cucumber.html`
- Allure (with request/response attachments): `./mvnw allure:serve`. Results go to `target/allure-results`
  (set in `src/test/resources/allure.properties`, so IDE runs land there too). The Environment widget
  shows the base URI, tag filter, Java version and OS; the file behind it is written on each run by `AllureEnvironmentHooks`.

## Coverage

| Feature | Positive | Negative |
|---|---|---|
| `pet/pet_crud.feature` | Create (incl. all statuses), get by id, find by status, update (JSON PUT and form POST), delete, full lifecycle | |
| `pet/pet_negative.feature` | | Unknown id (404), non-numeric id, malformed JSON (400), unsupported media type (415), form-update/delete of a missing pet, a pet that was already deleted |
| `store/store_order.feature` | Place, get, delete order | Unknown/non-numeric order id, deleting a missing order |

Each scenario validates the **status code**, **headers** (`Content-Type`, CORS), the **body** (field-by-field
against the expected model, or the `{code,type,message}` error envelope), and the **JSON schema**
(`src/test/resources/schemas`).

## Structure

Framework code (reusable, no Cucumber dependency) lives in `src/main`; tests live in `src/test`.

```
src/main/java/org/example/tbc_swagger_api_project
├── client/    RestAssured API clients (BaseClient holds the shared spec + retry helper)
├── config/    ApiConfig – reads config.properties, overridable via -D system properties
├── data/      TestDataFactory – builds unique test payloads
└── models/    Jackson POJOs (Lombok @Data/@Builder): Pet, Category, Tag, Order, ApiResponse
src/main/resources
└── config.properties

src/test/java/org/example/tbc_swagger_api_project
├── context/   TestContext – per-scenario state shared across steps
├── hooks/     CleanupHooks (deletes every pet/order a scenario created), WarmUpHooks (untimed first
│              request so latency checks measure the server), AllureEnvironmentHooks
├── runner/    RunCucumberTest – JUnit Platform suite entry point
└── steps/     Step definitions (Pet, Store, generic Response assertions)
src/test/resources
├── features/  Gherkin feature files
├── schemas/   JSON schemas
├── junit-platform.properties   Cucumber glue/plugins for `mvn test`
└── cucumber.properties         the same for IDE runs of a single feature/scenario
```

Step classes, hooks, `TestContext` and the API clients are all wired by constructor injection
(cucumber-picocontainer), with a fresh instance of each per scenario.


## Notes on the public Petstore

The public server is shared and load balanced across nodes that don't sync immediately, so a read right
after a write can briefly return stale data. Steps that read back a resource the scenario just wrote
re-check it (`poll.attempts` × `poll.interval.millis`) before asserting. Negative checks only use
this re-check when waiting for a deleted resource to disappear. Test data gets random high ids, and
cleanup hooks delete it after each scenario.
