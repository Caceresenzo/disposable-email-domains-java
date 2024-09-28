# Disposable Email Domains for Java

- [Disposable Email Domains for Java](#disposable-email-domains-for-java)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Usage](#usage)
    - [Test if a Domain is blacklisted](#test-if-a-domain-is-blacklisted)
    - [Test if an Email is from a blacklisted domain](#test-if-an-email-is-from-a-blacklisted-domain)

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
