package dev.caceresenzo.disposableemaildomains.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.validation.constraints.NonDisposableEmailDomain.List;
import dev.caceresenzo.disposableemaildomains.validation.validator.NonDisposableEmailDomainValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The annotated element must not be a disposable email domain. Accepts (nullable) {@code String}.
 *
 * @see DisposableEmailDomains#testEmail(String)
 */
@Documented
@Constraint(validatedBy = NonDisposableEmailDomainValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface NonDisposableEmailDomain {

	String message() default "{dev.caceresenzo.disposableemaildomains.validation.constraints.NonDisposableEmailDomain.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * Defines several {@code @NonDisposableEmailDomain} constraints on the same element.
	 *
	 * @see NonDisposableEmailDomain
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	public @interface List {

		NonDisposableEmailDomain[] value();

	}

}