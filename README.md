# Disposable Email Domains for Java

> [!WARNING]
> The API is in beta, expect breaking changes.

- [Disposable Email Domains for Java](#disposable-email-domains-for-java)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Usage](#usage)
    - [Test if a Domain is blacklisted](#test-if-a-domain-is-blacklisted)
    - [Test if an Email is from a blacklisted domain](#test-if-an-email-is-from-a-blacklisted-domain)
  - [Spring Boot Starter](#spring-boot-starter)

## Installation

```xml
<properties>
    <disposable-email-domains.version>0.1.0</disposable-email-domains.version>
</properties>

<dependencies>
    <dependency>
        <groupId>dev.caceresenzo.disposableemaildomains</groupId>
        <artifactId>disposable-email-domains-core</artifactId>
        <version>${disposable-email-domains.version}</version>
    </dependency>
</dependencies>
```

## Configuration

```java
DisposableEmailDomains disposableEmailDomains = DisposableEmailDomains.builder()
    .githubDailyDisposableEmailDomains()
    .staticDomains("example.com")
    .build();
```

## Usage

### Test if a Domain is blacklisted

```java
boolean isBad = disposableEmailDomains.testDomain("example.com");
```

### Test if an Email is from a blacklisted domain

```java
boolean isBad = disposableEmailDomains.testEmail("hello@example.com");
```

## Spring Boot Starter

There is a Spring Boot auto-configuration available.

```xml
<dependencies>
    <dependency>
        <groupId>dev.caceresenzo.disposableemaildomains</groupId>
        <artifactId>disposable-email-domains-spring-boot-starter</artifactId>
        <version>${disposable-email-domains.version}</version>
    </dependency>
</dependencies>
```

Which can be configured:

```yml
disposable-email-domains:
  checkers:
    # Automatically configure: https://github.com/disposable/disposable-email-domains
    daily-updated-domains: true

    # Add external HTTP sources
    http:
      - uri: https://raw.githubusercontent.com/disposable-email-domains/disposable-email-domains/refs/heads/main/disposable_email_blocklist.conf
        cache-path: disposable_email_blocklist.conf

    # Add local file sources
    file:
      - path: domains.txt

    # Add static domains
    static-domains:
      - example.com
```
