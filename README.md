# Infiterra QA Challenge — SauceDemo Checkout Automation

Automated UI tests for the SauceDemo checkout flow using **Java 17 + Selenium 4 + Cucumber (BDD) + TestNG**.  
The suite validates required-field errors on Checkout Step One and product/price/totals consistency on Checkout Step Two, including order completion checks. It also captures **logs**, **screenshots on failure**, and **local desktop videos** of each scenario.

---

## Table of Contents
- [Stack](#stack)
- [Architecture](#architecture)
- [Scenarios Implemented](#scenarios-implemented)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [Reports & Artifacts](#reports--artifacts)
- [Troubleshooting](#troubleshooting)
- [CI Notes](#ci-notes)

---

## Stack

- **Language:** Java 17 (Temurin)
- **Build:** Maven 3.9+
- **Test Runner:** Cucumber 7.18 + TestNG 7.10
- **Automation:** Selenium 4.24, WebDriverManager 5.9
- **Logging:** Log4j2 2.23
- **Recording:** Monte Screen Recorder 0.7.7 (AVI, disabled in headless)
- **IDE:** VS Code (optional)

Versions are declared in `pom.xml` properties.

---

## Architecture

- **BDD + POM:** Gherkin features drive tests; Page Object Model encapsulates UI behaviors.
- **Runner:** `infiterra.runner.TestRunner` (TestNG + Cucumber).
- **Config:** `src/test/resources/cucumber.properties` (committed). You can override with JVM flags (`-Dkey=value`).
- **Stability:** Explicit waits in `BasePage`, optional `slowmo` between actions, and Incognito mode by default.

**Key design points**
- Strict use of `data-test` attributes where available (stable locators).
- Reusable utilities: `DriverFactory`, `Hooks`, `VideoRecorder`, `ScreenshotUtil`, `Config`.
- Clean logging at each step for traceability.

---

## Scenarios Implemented

### 1) Checkout Step One — Error Validation
- Navigate to SauceDemo and log in.
- Add any product, open the cart, click **Checkout**.
- Submit Step One with one **missing** required field (First Name, Last Name, or Postal Code).
- **Validate:**
  - The correct error message is displayed.
  - User **remains** on checkout step one page.

Feature file: `src/test/resources/features/checkout_step_one.feature`

### 2) Checkout Step Two — Product & Totals Validation
- Log in, add **2 random** products.
- Validate **cart badge count** equals items added.
- Open cart, click **Checkout**, complete Step One with valid data.
- **Validate on Step Two:**
  - Products transferred correctly (names and prices match the cart).
  - `Item total` equals **sum of individual product prices**.
  - `Total` equals **Item total + Tax**.
- Click **Finish**.
- **Validate completion:**
  - Header: `Thank you for your order!`
  - Body contains: `will arrive just as fast as the pony`.
  - Cart badge resets to **0**.

Feature file: `src/test/resources/features/checkout_step_two.feature`

---

## Project Structure

```
src/test/java
  ├─ infiterra/pages
  │   ├─ BasePage.java
  │   ├─ LoginPage.java
  │   ├─ InventoryPage.java
  │   ├─ CartPage.java
  │   ├─ CheckoutStepOnePage.java
  │   ├─ CheckoutStepTwoPage.java
  │   └─ CheckoutCompletePage.java
  ├─ infiterra/support
  │   ├─ DriverFactory.java
  │   ├─ Config.java
  │   ├─ Hooks.java
  │   ├─ VideoRecorder.java
  ├─ infiterra/runner
  │   └─ TestRunner.java
  └─ steps
      └─ CheckoutSteps.java

src/test/resources
  ├─ features
  │   ├─ checkout_step_one.feature
  │   └─ checkout_step_two.feature
  └─ config.properties
  └─ cucumber.properties
  └─ log4j2.xml

target/
  ├─ logs/
  ├─ screenshots/
  ├─ videos/
  └─ surefire-reports/
```

---

## Prerequisites

- **Google Chrome** (latest stable)
- **Java 17**, **Maven 3.9+**, **Git**

Install via `winget` (Windows 11):

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
winget install Git.Git
```

Verify:

```powershell
java -version
mvn -v
git --version
```

---

## Configuration

Credentials and toggles live in **`src/test/resources/cucumber.properties`**:

```properties
swag.user=standard_user
swag.pass=secret_sauce
headless=false
incognito=true
video.enabled=true
slowmo.ms=150
```

**Override at runtime (JVM flags):**

```bash
mvn clean test -Dheadless=true -Dvideo.enabled=false -Dslowmo.ms=0
```

> Note: Video is automatically disabled in headless runs.

---

## How to Run

Clone and enter the project:

```bash
git clone <your-public-repo-url> infiterra
cd infiterra
```

Run the full suite (headed):

```bash
mvn clean test
```

Headless / CI-safe:

```bash
mvn clean test -Dheadless=true -Dvideo.enabled=false
```

Filter by **name** (regex) or **tags** (if you add tags later):

```bash
mvn test -Dcucumber.filter.name="Products and prices are consistent.*"
mvn test -Dcucumber.filter.tags="@step2"
```

---

## Reports & Artifacts

- **Logs (Log4j2):** `target/logs/test-run.log`
- **Screenshots on failure:** `target/screenshots/<scenario>_<timestamp>.png`
- **Videos (local, non-headless):** `target/videos/<scenario>.avi`
- **Cucumber/TestNG outputs:** `target/surefire-reports` (and `target/cucumber-report.html` if enabled)

---

## Troubleshooting

- **Null WebDriver / NPE in pages** → Ensure `infiterra.support.Hooks` (Before/After) is in the glue and `DriverFactory` initializes before steps.
- **Timeouts waiting for elements** → Make sure you’re on the expected page; locators prefer `data-test` attributes.
- **Unexpected Chrome banners/overlays** → Incognito is enabled by default; an ESC is sent after login to dismiss overlays.
- **Video errors in headless/CI** → Disable recording: `-Dvideo.enabled=false`.
- **CDP warnings** → Informational with newest Chrome; safe to ignore for this suite.

---

## CI Notes

- Run **headless** with video disabled.
- Archive `target/**` artifacts (logs, screenshots, reports) as build outputs.
- Cache Maven repository between runs for faster builds.

---

Happy testing! 
