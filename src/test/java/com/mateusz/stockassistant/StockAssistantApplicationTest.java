package com.mateusz.stockassistant;

import com.mateusz.stockassistant.utils.TestListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@SpringBootTest
@Listeners(TestListener.class)
public class StockAssistantApplicationTest extends AbstractTestNGSpringContextTests {

	@Test
	public void testConextLoad() {

	}
}