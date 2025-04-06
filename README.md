# Disposable Email Domains for Java

Prevent users from signing up with temporary or disposable email addresses.

> [!WARNING]
> The API is in beta, expect breaking changes.

- [Disposable Email Domains for Java](#disposable-email-domains-for-java)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
  - [Test if a Domain is blacklisted](#test-if-a-domain-is-blacklisted)
  - [Test if an Email is from a blacklisted domain](#test-if-an-email-is-from-a-blacklisted-domain)
- [Spring Boot Starter](#spring-boot-starter)
  - [Validation](#validation)

# Installation

```xml
<properties>
	<disposable-email-domains.version>0.4.1</disposable-email-domains.version>
</properties>

<dependencies>
	<dependency>
		<groupId>dev.caceresenzo.disposableemaildomains</groupId>
		<artifactId>disposable-email-domains-core</artifactId>
		<version>${disposable-email-domains.version}</version>
	</dependency>
</dependencies>
```

# Configuration

```java
DisposableEmailDomains disposableEmailDomains = DisposableEmailDomains.builder()

	/* configure https://github.com/disposable/disposable-email-domains */
	.githubDailyDisposableEmailDomains()

	/* add static domains */
	.staticDomains("example.com", "bad.com")

	/* load domains from a file */
	.file(Path.of("domains.txt"))

	.build();
```

# Usage

## Test if a Domain is blacklisted

```java
boolean isBad = disposableEmailDomains.testDomain("example.com");
```

## Test if an Email is from a blacklisted domain

> [!NOTE]
> The default implementation splits the first `@` and uses the second part. <br />
> If there is no `@` in the email, the whole string is taken as the domain.

```java
boolean isBad = disposableEmailDomains.testEmail("hello@example.com");
```

# Spring Boot Starter

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
    # Automatic refresh of sources
    reload:
      enabled: true
      fixed-date: P10M  # every 10 minutes

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

## Validation

A constraint annotation is available for bean validation.

> [!WARNING]
> An `@Email` annotation is required to validate the email format itself.

> [!TIP]
> `spring-boot-starter-validation` is needed to make validation work.

```java
@Data
class RegisterForm {

	@NotBlank
	@Email
	@NonDisposableEmailDomain
	private String email;

}

@RestController
@RequestMapping(path = "auth")
public class AuthController {

	@PostMapping("register")
	public void register(
		@RequestBody @Validated RegisterForm body
	) {
		final var email = body.getEmail();

		System.out.println(email);
	}

}
```
