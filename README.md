# CsvToJsonConverter

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://github.com/yourusername/CsvToJsonConverter/workflows/Java%20CI/badge.svg)](https://github.com/yourusername/CsvToJsonConverter/actions)
[![Java Version](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.org/projects/jdk/17/)

## Overview

`CsvToJsonConverter` is a production‑ready Java library / CLI tool that converts CSV files to JSON while performing schema validation. 
Key features:

* **Schema validation** – enforce required columns, data types, and custom rules.
* **Streaming conversion** – low memory footprint for large files.
* **Configurable** – all settings are externalised in `application.properties` and can be overridden by environment variables.
* **Robust error handling** – detailed logs, custom exceptions, and graceful shutdown.
* **Tested** – JUnit 5 test suite covering happy path and error scenarios.
* **Modern Java** – Java 17, SLF4J, Jackson, OpenCSV, Builder pattern, immutable DTOs.

## Project Structure

<!-- tiny readability tweak -->
