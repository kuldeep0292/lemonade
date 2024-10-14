package com.example.lemonade_stand;
//package com.example.lemonade_stand;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import com.example.lemonade_stand.order.CustomerOrder;
//import com.example.lemonade_stand.order.OrderProcessorMultiDay;
//
//class LemonadeStandAppTestsMultiDaySupport {
//	private OrderProcessorMultiDay orderProcessor;
//
//	@BeforeEach
//	void setUp() {
//		orderProcessor = new OrderProcessorMultiDay();
//	}
//
//	/**
//	 * Test case for processing a valid single order. Input: new CustomerOrder(10,
//	 * 1, 2); where customer pays $10 for 2 lemonades (Cost: $10). Output: [10]
//	 */
//	@Test
//	void testProcessOrders_ValidSingleOrder() {
//		CustomerOrder order = new CustomerOrder(10, 1, 2); // Cost: 10
//		List<CustomerOrder> orders = Collections.singletonList(order);
//		String result = orderProcessor.processOrders(orders);
//		assertEquals("[10]", result); // The $10 bill should remain after the order is processed.
//	}
//
//	/**
//	 * Test case for processing an invalid single order due to lack of change.
//	 * Input: new CustomerOrder(10, 1, 1); where customer pays $10 for 1 lemonade
//	 * (Cost: $5). Output: [10]
//	 */
//	@Test
//	void testProcessOrders_InvalidSingleOrder_NoChangeAvailable() {
//		CustomerOrder order = new CustomerOrder(10, 1, 1); // Cost: 5
//		List<CustomerOrder> orders = Collections.singletonList(order);
//		String result = orderProcessor.processOrders(orders);
//		assertEquals("null", result); // The order cannot be completed as change is needed
//	}
//
//	/**
//	 * Test case for processing an invalid single order due to insufficient balance.
//	 * Input: new CustomerOrder(10, 1, 5); where customer pays $10 for 5 lemonades
//	 * (Cost: $25). Output: null (Insufficient funds to cover the cost).
//	 */
//	@Test
//	void testProcessOrders_InvalidSingleOrder_InsufficientBalance() {
//		CustomerOrder order = new CustomerOrder(10, 1, 5); // Cost: 25
//		List<CustomerOrder> orders = Collections.singletonList(order);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // Order cannot be processed due to insufficient funds.
//	}
//
//	/**
//	 * Test case for processing valid multiple orders within one order. Input:
//	 * Multiple orders processed sequentially. Output: The remaining bills after
//	 * processing.
//	 */
//	@Test
//	void testProcessOrders_ValidMultipleOrders() {
//		CustomerOrder order1 = new CustomerOrder(20, 1, 4); // Cost: 20
//		CustomerOrder order2 = new CustomerOrder(10, 2, 2); // Cost: 10
//		List<CustomerOrder> orders = Arrays.asList(order1, order2);
//		String result = orderProcessor.processOrders(orders);
//		assertEquals("[20, 10]", result); // Both bills remain after successful processing.
//	}
//
//	/**
//	 * Test case for processing multiple orders where the first order cannot be
//	 * processed due to lack of change. Input: Multiple orders with the first one
//	 * being invalid. Output: null
//	 */
//	@Test
//	void testProcessOrders_InvalidMultipleOrders_NoChangeFirstOrder() {
//		CustomerOrder order1 = new CustomerOrder(10, 1, 1); // Cost: 5
//		CustomerOrder order2 = new CustomerOrder(20, 2, 2); // Cost: 10
//		List<CustomerOrder> orders = Arrays.asList(order1, order2);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // First order fails
//	}
//
//	/**
//	 * Test case for processing multiple orders where the third order cannot be
//	 * processed due to lack of change. Input: Multiple orders where the third order
//	 * fails. Output: null (Last order cannot be processed).
//	 */
//	@Test
//	void testProcessOrders_InvalidMultipleOrders_NoChangeThirdOrder() {
//		CustomerOrder order1 = new CustomerOrder(10, 1, 2); // Cost: 10
//		CustomerOrder order2 = new CustomerOrder(10, 2, 2); // Cost: 10
//		CustomerOrder order3 = new CustomerOrder(10, 3, 1); // Cost: 5
//		List<CustomerOrder> orders = Arrays.asList(order1, order2, order3);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // Third order fails due to lack of change.
//	}
//
//	/**
//	 * Test case for processing multiple orders where the second order has
//	 * insufficient balance. Input: Multiple orders where the second order cannot be
//	 * processed. Output: null (Second order cannot be processed).
//	 */
//	@Test
//	void testProcessOrders_InvalidMultipleOrders_InsufficientBalanceSecondOrder() {
//		CustomerOrder order1 = new CustomerOrder(10, 1, 2); // Cost: 10
//		CustomerOrder order2 = new CustomerOrder(10, 2, 5); // Cost: 25
//		List<CustomerOrder> orders = Arrays.asList(order1, order2);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // Second order cannot be processed due to insufficient funds.
//	}
//
//	/**
//	 * Test case for processing an order where zero lemonades are requested. Input:
//	 * CustomerOrder(10, 1, 0) where customer pays $10 but requests no lemonades.
//	 * Output: null (Invalid order).
//	 */
//	@Test
//	void testProcessOrders_InvalidSingleOrder_ZeroLemonadesRequested() {
//		CustomerOrder order = new CustomerOrder(10, 1, 0); // Cost: 0
//		List<CustomerOrder> orders = Collections.singletonList(order);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // Invalid order as no lemonades are requested.
//	}
//
//	/**
//	 * Test case for processing multiple orders where one has zero lemonades
//	 * requested. Input: Multiple orders including one with zero lemonades. Output:
//	 * null (Order cannot be processed).
//	 */
//	@Test
//	void testProcessOrders_InvalidMultipleOrders_ZeroLemonadesRequested() {
//		CustomerOrder order1 = new CustomerOrder(5, 1, 1); // Cost: 5
//		CustomerOrder order2 = new CustomerOrder(20, 2, 0); // Cost: 0
//		List<CustomerOrder> orders = Arrays.asList(order1, order2);
//		String result = orderProcessor.processOrders(orders);
//		assertNull(result); // Second order is invalid as no lemonades are requested.
//	}
//
//	/**
//	 * Test case for processing orders where the list of orders is null. Input: null
//	 * Output: null (Cannot process).
//	 */
//	@Test
//	void testProcessOrders_NullOrders() {
//		String result = orderProcessor.processOrders(null);
//		assertEquals("null", result); // Null orders cannot be processed.
//	}
//
//	/**
//	 * Test case for generating a report after processing valid orders. Expects the
//	 * report to show the correct total lemonades sold and total profit made.
//	 */
//	@Test
//	void testPrintReport_AfterProcessingOrders() {
//		// Processing a valid order
//		CustomerOrder order = new CustomerOrder(10, 1, 2); // Cost: 10
//		orderProcessor.processOrders(Collections.singletonList(order));
//		String report = orderProcessor.printReport();
//		assertTrue(report.contains("Total Lemonades sold so far - 2")); // Should show correct number of lemonades sold.
//		assertTrue(report.contains("Total Profit Made - 10")); // Should show correct profit made.
//	}
//
//	/**
//	 * Test case for generating a report before any orders have been processed.
//	 * Expects the report to show totals as zero.
//	 */
//	@Test
//	void testPrintReport_BeforeProcessingOrders() {
//		String report = orderProcessor.printReport();
//		assertTrue(report.contains("Total Lemonades sold so far - 0")); // Should show zero sold before any processing.
//		assertTrue(report.contains("Total Profit Made - 0")); // Should show zero profit before any processing.
//	}
//
//	/**
//	 * Validate remaining bills can be utilized for next day orders. Input: Day 1
//	 * orders with remaining bills, Day 2 orders that utilize those bills. Output:
//	 * [5]
//	 */
//	@Test
//	void testProcessOrders_UtilizeRemainingBills_NextDay() {
//		// Day 1 orders
//		CustomerOrder orderDay1_1 = new CustomerOrder(10, 1, 2); // Cost: 10
//		CustomerOrder orderDay1_2 = new CustomerOrder(10, 2, 2); // Cost: 10
//		CustomerOrder orderDay1_3 = new CustomerOrder(5, 3, 1); // Cost: 5
//		orderProcessor.processOrders(Arrays.asList(orderDay1_1, orderDay1_2, orderDay1_3));
//
//		// Day 2 order
//		CustomerOrder orderDay2 = new CustomerOrder(10, 1, 1); // Cost: 5
//		String resultDay2 = orderProcessor.processOrders(Collections.singletonList(orderDay2));
//		assertEquals("[5]", resultDay2); // Remaining bill should be 5 after day 2 order.
//	}
//
//	/**
//	 * Validate next day order in case of 0 remaining bills from previous day due to
//	 * failed orders. Input: Day 1 orders all fail, Day 2 order. Output: null
//	 */
//	@Test
//	void testProcessOrders_NoRemainingBills_NextDay() {
//		// Day 1 orders
//		CustomerOrder orderDay1_1 = new CustomerOrder(5, 1, 1); // Cost: 5
//		CustomerOrder orderDay1_2 = new CustomerOrder(5, 2, 1); // Cost: 5
//		CustomerOrder orderDay1_3 = new CustomerOrder(20, 3, 1); // Cost: 20
//		orderProcessor.processOrders(Arrays.asList(orderDay1_1, orderDay1_2, orderDay1_3)); // All invalid
//
//		// Day 2 order
//		CustomerOrder orderDay2 = new CustomerOrder(10, 1, 1); // Cost: 5
//		String resultDay2 = orderProcessor.processOrders(Collections.singletonList(orderDay2));
//		assertNull(resultDay2); // No remaining bills to process Day 2 order.
//	}
//}
