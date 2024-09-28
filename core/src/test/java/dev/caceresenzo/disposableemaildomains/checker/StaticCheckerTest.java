package dev.caceresenzo.disposableemaildomains.checker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StaticCheckerTest {

	static StaticChecker checker;

	@BeforeAll
	static void setUp() {
		checker = new StaticChecker(Collections.singletonList("a.com"));
	}

	@Test
	void test() {
		assertTrue(checker.test("a.com"));
		assertTrue(checker.test("A.com"));
		assertFalse(checker.test("b.com"));
	}

}