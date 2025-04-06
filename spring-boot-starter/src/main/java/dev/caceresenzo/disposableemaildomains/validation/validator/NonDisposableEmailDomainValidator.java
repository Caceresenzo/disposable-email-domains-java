package dev.caceresenzo.disposableemaildomains.validation.validator;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.validation.constraints.NonDisposableEmailDomain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NonDisposableEmailDomainValidator implements ConstraintValidator<NonDisposableEmailDomain, String> {

	private final @NonNull DisposableEmailDomains disposableEmailDomains;

	@Override
	public void initialize(NonDisposableEmailDomain annotation) {}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !disposableEmailDomains.testEmail(value);
	}

}