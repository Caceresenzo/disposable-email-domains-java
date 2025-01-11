package dev.caceresenzo.disposableemaildomains.spring.boot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsAutoConfiguration;

@SpringBootTest(
	classes = {
		DisposableEmailDomainsAutoConfiguration.class,
	}, properties = {
		"disposableEmailDomains.checkers.static-domains[0]=example.com",
	}
)
class AutoConfigurationTest {

	@Test
	void contextLoads(
		@Autowired DisposableEmailDomains disposableEmailDomains
	) {
		assertTrue(disposableEmailDomains.testDomain("example.com"));
		assertFalse(disposableEmailDomains.testDomain("gmail.com"));
	}

}