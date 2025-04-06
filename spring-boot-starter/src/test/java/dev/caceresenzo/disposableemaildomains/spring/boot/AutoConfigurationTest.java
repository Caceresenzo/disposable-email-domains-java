package dev.caceresenzo.disposableemaildomains.spring.boot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsAutoConfiguration;
import dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure.DisposableEmailDomainsReloadTask;

@EnableScheduling
@Import(AutoConfigurationTest.ContextConfiguration.class)
@SpringBootTest(
	classes = {
		DisposableEmailDomainsAutoConfiguration.class,
	},
	properties = {
		"disposable-email-domains.checkers.static-domains[0]=example.com",
	}
)
class AutoConfigurationTest {

	@TestConfiguration(proxyBeanMethods = false)
	static class ContextConfiguration {

		@Bean
		TaskScheduler taskScheduler() {
			return new ThreadPoolTaskScheduler();
		}

	}

	@Test
	void contextLoads(
		@Autowired DisposableEmailDomains disposableEmailDomains,
		@Autowired DisposableEmailDomainsReloadTask disposableEmailDomainsReloadTask
	) {
		assertTrue(disposableEmailDomains.testDomain("example.com"));
		assertFalse(disposableEmailDomains.testDomain("gmail.com"));
	}

}