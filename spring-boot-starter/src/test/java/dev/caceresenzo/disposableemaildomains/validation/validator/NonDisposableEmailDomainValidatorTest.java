package dev.caceresenzo.disposableemaildomains.validation.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsAutoConfiguration;
import dev.caceresenzo.disposableemaildomains.validation.constraints.NonDisposableEmailDomain;
import lombok.AllArgsConstructor;
import lombok.Data;

@SpringBootTest(
	classes = {
		DisposableEmailDomainsAutoConfiguration.class,
		ValidationAutoConfiguration.class
	},
	properties = {
		"disposable-email-domains.checkers.daily-updated-domains=false",
		"disposable-email-domains.checkers.reload.enabled=false",
		"disposable-email-domains.checkers.static-domains[0]=example.com",
	}
)
class NonDisposableEmailDomainValidatorTest {

	@Test
	void testValidation(
		@Autowired SmartValidator validator
	) {
		assertFalse(isNotPassing(validator, "gmail.com"));
		assertFalse(isNotPassing(validator, "hello@gmail.com"));

		assertTrue(isNotPassing(validator, "example.com"));
		assertTrue(isNotPassing(validator, "hello@example.com"));
	}

	private boolean isNotPassing(SmartValidator validator, String email) {
		final var form = new RegisterForm(email);
		final var errors = new BeanPropertyBindingResult(form, "registerForm");

		validator.validate(form, errors);

		return errors.hasErrors();
	}

	@Test
	void testMessage(
		@Autowired SmartValidator validator
	) {
		final var form = new RegisterForm("hello@example.com");
		final var errors = new BeanPropertyBindingResult(form, "registerForm");

		validator.validate(form, errors);

		final var error = errors.getFieldError();
		assertNotNull(error);
		assertEquals("email", error.getField());
		assertEquals("email domain is not allowed", error.getDefaultMessage());
	}

}

@Data
@AllArgsConstructor
class RegisterForm {

	@NonDisposableEmailDomain
	private String email;

}