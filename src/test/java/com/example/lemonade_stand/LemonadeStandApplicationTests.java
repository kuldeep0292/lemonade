package com.example.lemonade_stand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.lemonade_stand.order.CustomerOrder;
import com.example.lemonade_stand.order.OrderProcessor;

@SpringBootTest
class LemonadeStandApplicationTests {

	private OrderProcessor orderProcessor;

	@BeforeEach
	void setUp() {
		orderProcessor = new OrderProcessor();
	}

	@Test
	void testProcessOrders_Success() {
		// Prepare test data
		CustomerOrder order1 = new CustomerOrder(5, 1, 1); // positionInLine = 1, billValue = 10, requestedLemonades =
															// 2
		CustomerOrder order2 = new CustomerOrder(5, 1, 1); // positionInLine = 2, billValue = 20, requestedLemonades =
															// 1
		List<CustomerOrder> orders = Arrays.asList(order1, order2);

		// Call method under test
		String result = orderProcessor.processOrders(orders);

		// Assertions
		assertNotNull(result);
		assertFalse(result.equals("null")); // Ensure result is not "null"
		assertEquals("[5, 5]", result); // Check remaining bills
	}

	@Test
	void testProcessOrders_InsufficientFunds() {
		// Prepare test data
		CustomerOrder order1 = new CustomerOrder(10, 1, 3); // Insufficient funds, lemonade costs 10
		List<CustomerOrder> orders = Arrays.asList(order1);

		// Call method under test
		String result = orderProcessor.processOrders(orders);

		// Assertions
		assertEquals("null", result); // Expect "null" as the result
	}

	@Test
	void testProcessOrders_NoOrders() {
		// Call method with an empty order list
		String result = orderProcessor.processOrders(null);

		// Assertions
		assertEquals("null", result); // Expect "null" for null or empty input
	}

}
