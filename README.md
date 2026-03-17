# SE333 Assignment 5

![Build Status](https://github.com/davalen25/SE333-Assignment5/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview
This project contains automated tests for the Amazon shopping cart package, including unit tests, integration tests, and UI tests using Playwright.

## Part 1 - Integration & Unit Tests
- `AmazonUnitTest.java` - Unit tests using mocks/stubs with JUnit 5 and Mockito
- `AmazonIntegrationTest.java` - Integration tests using an in-memory HSQLDB database

## Workflow
All GitHub Actions have passed successfully including:
- Checkstyle static analysis
- JUnit tests
- JaCoCo coverage report

## Actions
[View Workflow](https://github.com/davalen25/SE333-Assignment5/actions/workflows/SE333_CI.yml)

## Part 2 - Playwright UI Testing

### Task 2 - Manual Playwright Tests (playwrightTraditional)
7 test cases automating the DePaul bookstore purchase flow including search, filtering, cart, checkout, and cart deletion.

### Task 4 - AI-Assisted Playwright Tests (playwrightLLM)
Tests generated using GitHub Copilot with Playwright MCP server.

## Reflection: Manual vs AI-Assisted UI Testing

**Ease of Writing and Running Tests**
Writing manual Playwright tests in Java required a deep understanding of the page structure, locators, and assertion APIs. Each test had to be crafted to handle timing issues and hidden elements. AI-assisted testing with Playwright MCP was faster to get started. Simply describing the workflow in natural language prompted the agent to navigate the live browser and generate Java code automatically. However, the generated code still required manual fixes such as replacing the search box interaction with a direct URL navigation to avoid timeout issues.

**Accuracy and Reliability of Generated Tests**
The manually written tests were reliable and consistent across runs. Every assertion was intentionally placed and verified against the actual page behavior. The AI-generated tests were a good starting point but were not immediately runnable since the agent used a search button click approach that timed out, whereas the working solution required navigating directly to the search URL. This shows that AI-generated tests reflect what a human might do visually, but may miss optimizations that come from understanding the underlying web application.

**Maintenance**
Manual tests require more upfront effort but are easier to maintain because every line of code is understood by the developer. When the website changes, it is clear exactly what needs to be updated. AI-generated tests can be harder to maintain because the developer may not fully understand every locator or assertion that was generated, making debugging more difficult when tests break.

**Limitations and Issues Encountered**
The main limitation of AI-assisted testing was that the Playwright MCP agent required manual approval for every browser action, slowing down the generation process. Additionally, the generated code assumed a search box interaction that caused timeouts in practice. The AI also did not account for timing issues such as waiting for cart updates, which required adding waitForTimeout calls manually. Overall, AI assistance is best used as a starting point that still requires human review and refinement.
