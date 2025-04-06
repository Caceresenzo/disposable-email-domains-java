package dev.caceresenzo.disposableemaildomains.spring.boot;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsAutoConfiguration;
import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsReloadTask;

@SpringBootTest(
	classes = {
		DisposableEmailDomainsAutoConfiguration.class,
	},
	properties = {
		"disposable-email-domains.checkers.reload.enabled=false",
	}
)
class AutoConfigurationNoReloadTest {

	@Test
	void contextLoads(
		@Autowired(required = false) DisposableEmailDomainsReloadTask disposableEmailDomainsReloadTask
	) {
		assertNull(disposableEmailDomainsReloadTask);
	}

}