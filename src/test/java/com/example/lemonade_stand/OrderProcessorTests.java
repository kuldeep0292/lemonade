package com.example.lemonade_stand;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.lemonade_stand.order.CustomerOrder;
import com.example.lemonade_stand.order.OrderProcessor;
import com.example.lemonade_stand.order.OrderRepositoryService;

@SpringBootTest
class OrderProcessorTests {
	@Autowired
	private OrderProcessor orderProcessor;

	@Autowired
	private OrderRepositoryService orderRepositoryService;

	@BeforeEach
	void setUp() {
		// Arrange: Clear records for independent test execution and initialize bill
		// counter
		orderRepositoryService.clearAllRecords();
		orderProcessor.initializeBillCounter();
	}

	/**
	 * Test case for processing a valid single order.
	 * 
	 * Arrange: Create a new CustomerOrder with bill value $10 for 2 lemonades. Act:
	 * Process the order and get the result. Assert: Verify the result matches the
	 * expected bill remaining.
	 */
	@Test
	void testProcessOrders_ValidSingleOrder() {
		CustomerOrder order = new CustomerOrder(10, 1, 2); // Cost: 10
		List<CustomerOrder> orders = Collections.singletonList(order);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("[10]", result); // The $10 bill should remain after the order is processed.
	}

	/**
	 * Test case for processing an invalid single order due to lack of change.
	 * 
	 * Arrange: Create a new CustomerOrder with bill value $10 for 1 lemonade. Act:
	 * Process the order and get the result. Assert: Verify the result is null as
	 * the order cannot be completed due to insufficient change.
	 */
	@Test
	void testProcessOrders_InvalidSingleOrder_NoChangeAvailable() {
		CustomerOrder order = new CustomerOrder(10, 1, 1); // Cost: 5
		List<CustomerOrder> orders = Collections.singletonList(order);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // The order cannot be completed as change is needed
	}

	/**
	 * Test case for processing an invalid single order due to insufficient balance.
	 * 
	 * Arrange: Create a new CustomerOrder with bill value $10 for 5 lemonades. Act:
	 * Process the order and get the result. Assert: Verify the result is null as
	 * the order cannot be processed due to insufficient funds.
	 */
	@Test
	void testProcessOrders_InvalidSingleOrder_InsufficientBalance() {
		CustomerOrder order = new CustomerOrder(10, 1, 5); // Cost: 25
		List<CustomerOrder> orders = Collections.singletonList(order);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // Order cannot be processed due to insufficient funds.
	}

	/**
	 * Test case for processing valid multiple orders within one order.
	 * 
	 * Arrange: Create multiple valid CustomerOrders. Act: Process the orders and
	 * get the result. Assert: Verify the result matches the expected remaining
	 * bills.
	 */
	@Test
	void testProcessOrders_ValidMultipleOrders() {
		CustomerOrder order1 = new CustomerOrder(20, 1, 4); // Cost: 20
		CustomerOrder order2 = new CustomerOrder(10, 2, 2); // Cost: 10
		List<CustomerOrder> orders = Arrays.asList(order1, order2);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("[20, 10]", result); // Both bills remain after successful processing.
	}

	/**
	 * Test case for processing multiple orders where the first order cannot be
	 * processed due to lack of change.
	 * 
	 * Arrange: Create multiple CustomerOrders with the first being invalid. Act:
	 * Process the orders and get the result. Assert: Verify the result is null as
	 * the first order fails.
	 */
	@Test
	void testProcessOrders_InvalidMultipleOrders_NoChangeFirstOrder() {
		CustomerOrder order1 = new CustomerOrder(10, 1, 1); // Cost: 5
		CustomerOrder order2 = new CustomerOrder(20, 2, 2); // Cost: 10
		List<CustomerOrder> orders = Arrays.asList(order1, order2);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // First order fails
	}

	/**
	 * Test case for processing multiple orders where the third order cannot be
	 * processed due to lack of change.
	 * 
	 * Arrange: Create multiple CustomerOrders with the third being invalid. Act:
	 * Process the orders and get the result. Assert: Verify the result is null as
	 * the last order fails.
	 */
	@Test
	void testProcessOrders_InvalidMultipleOrders_NoChangeThirdOrder() {
		CustomerOrder order1 = new CustomerOrder(10, 1, 2); // Cost: 10
		CustomerOrder order2 = new CustomerOrder(10, 2, 2); // Cost: 10
		CustomerOrder order3 = new CustomerOrder(10, 3, 1); // Cost: 5
		List<CustomerOrder> orders = Arrays.asList(order1, order2, order3);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // Third order fails due to lack of change.
	}

	/**
	 * Test case for processing multiple orders where the second order has
	 * insufficient balance.
	 * 
	 * Arrange: Create multiple CustomerOrders with the second being invalid. Act:
	 * Process the orders and get the result. Assert: Verify the result is null as
	 * the second order fails.
	 */
	@Test
	void testProcessOrders_InvalidMultipleOrders_InsufficientBalanceSecondOrder() {
		CustomerOrder order1 = new CustomerOrder(10, 1, 2); // Cost: 10
		CustomerOrder order2 = new CustomerOrder(10, 2, 5); // Cost: 25
		List<CustomerOrder> orders = Arrays.asList(order1, order2);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // Second order cannot be processed due to insufficient funds.
	}

	/**
	 * Test case for processing an order where zero lemonades are requested.
	 * 
	 * Arrange: Create a CustomerOrder requesting 0 lemonades. Act: Process the
	 * order and get the result. Assert: Verify the result is null as this is an
	 * invalid order.
	 */
	@Test
	void testProcessOrders_InvalidSingleOrder_ZeroLemonadesRequested() {
		CustomerOrder order = new CustomerOrder(10, 1, 0); // Cost: 0
		List<CustomerOrder> orders = Collections.singletonList(order);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // Invalid order as no lemonades are requested.
	}

	/**
	 * Test case for processing multiple orders where one has zero lemonades
	 * requested.
	 * 
	 * Arrange: Create multiple CustomerOrders including one with zero lemonades.
	 * Act: Process the orders and get the result. Assert: Verify the result is null
	 * as the order cannot be processed.
	 */
	@Test
	void testProcessOrders_InvalidMultipleOrders_ZeroLemonadesRequested() {
		CustomerOrder order1 = new CustomerOrder(5, 1, 1); // Cost: 5
		CustomerOrder order2 = new CustomerOrder(20, 2, 0); // Cost: 0
		List<CustomerOrder> orders = Arrays.asList(order1, order2);

		// Act
		String result = orderProcessor.processOrders(orders);

		// Assert
		assertEquals("null", result); // Second order is invalid as no lemonades are requested.
	}

	/**
	 * Test case for processing orders where the list of orders is null.
	 * 
	 * Arrange: Prepare to process null orders. Act: Process the null orders and get
	 * the result. Assert: Verify the result is null as orders cannot be processed.
	 */
	@Test
	void testProcessOrders_NullOrders() {
		// Act
		String result = orderProcessor.processOrders(null);

		// Assert
		assertEquals("null", result); // Null orders cannot be processed.
	}

	/**
	 * Validate remaining bills can be utilized for next day orders.
	 * 
	 * Arrange: Create Day 1 orders and process them. Act: Create Day 2 order and
	 * process it. Assert: Verify the result matches the expected remaining bill.
	 */
	@Test
	void testProcessOrders_UtilizeRemainingBills_NextDay() {
		// Day 1 orders
		CustomerOrder orderDay1_1 = new CustomerOrder(10, 1, 2); // Cost: 10
		CustomerOrder orderDay1_2 = new CustomerOrder(10, 2, 2); // Cost: 10
		CustomerOrder orderDay1_3 = new CustomerOrder(5, 3, 1); // Cost: 5
		orderProcessor.processOrders(Arrays.asList(orderDay1_1, orderDay1_2, orderDay1_3));

		// Day 2 order
		CustomerOrder orderDay2 = new CustomerOrder(10, 1, 1); // Cost: 5
		String resultDay2 = orderProcessor.processOrders(Collections.singletonList(orderDay2));
		assertEquals("[10]", resultDay2); // Remaining bill should be 10 for day 2 order and the order should be
											// processed using 5 bill from previous day
	}

	/**
	 * Validate next day order in case of 0 remaining bills from previous day due to
	 * failed orders. Input: Day 1 orders all fail, Day 2 order.
	 */
	@Test
	void testProcessOrders_NoRemainingBills_NextDay() {
		// Arrange: Day 1 orders that will all fail due to insufficient change
		CustomerOrder orderDay1_1 = new CustomerOrder(5, 1, 1); // Cost: 5
		CustomerOrder orderDay1_2 = new CustomerOrder(5, 2, 1); // Cost: 5
		CustomerOrder orderDay1_3 = new CustomerOrder(20, 3, 1); // Cost: 20
		orderProcessor.processOrders(Arrays.asList(orderDay1_1, orderDay1_2, orderDay1_3)); // This should fail

		// Arrange: Day 2 order
		CustomerOrder orderDay2 = new CustomerOrder(10, 1, 1); // Cost: 5

		// Act: Attempt to process Day 2 order
		String resultDay2 = orderProcessor.processOrders(Collections.singletonList(orderDay2));

		// Assert: No remaining bills from Day 1 as all orders failed, hence Day 2 order
		// fails too
		assertEquals("null", resultDay2); // Order failed due to insufficient change
	}

	/**
	 * Validate bill consumption for a bill denomination of 5. Input: Customer
	 * orders for 1 lemonade with a 5 bill and another with a 20 bill. Output:
	 * Change returned as [20]. 1 denomination of 5 bill is consumed from the order,
	 * rest 2 consumed from db and the 20 bill is returned as change.
	 */
	@Test
	public void testBillsConsumedFromOrderAndDatabase_CheckForBill5() {
		// Arrange: Pre-requisite - Save 2 denominations of 5 bills in the database
		CustomerOrder orderDay1_1 = new CustomerOrder(5, 1, 1); // Cost: 5, change 0
		CustomerOrder orderDay1_2 = new CustomerOrder(5, 2, 1); // Cost: 5, change 0
		orderProcessor.processOrders(Arrays.asList(orderDay1_1, orderDay1_2)); // Process Day 1 orders

		// Arrange: Day 2 orders
		CustomerOrder orderDay2_1 = new CustomerOrder(5, 1, 1); // Cost: 5, change 0
		CustomerOrder orderDay2_2 = new CustomerOrder(20, 2, 1); // Cost: 20, change 15

		// Act: Process Day 2 orders
		String result = orderProcessor.processOrders(Arrays.asList(orderDay2_1, orderDay2_2));

		// Assert: Expected output is [20]. Only the 20 bill is returned as output.
		String expectedOutput = "[20]";
		assertEquals(expectedOutput, result); // Verify the correct change returned
	}

	/**
	 * Validate bill consumption for a bill denomination of 10. Input: Customer
	 * orders for 1 lemonade with a 10 bill, and two subsequent orders with 20
	 * bills. Output: Change returned as [20, 20]. 1 denomination of 10 bill is
	 * consumed from the order, and 1 consumed from db and 2 denominations of 20
	 * bills are returned as change.
	 */
	@Test
	public void testBillsConsumedFromOrderAndDatabase_CheckForBill10() {
		// Arrange: Pre-requisite - Save 2 denominations of 5 bills in the database
		CustomerOrder orderDay1_1 = new CustomerOrder(10, 1, 2); // Cost: 10, change 0
		orderProcessor.processOrders(Arrays.asList(orderDay1_1)); // Process Day 1 orders

		// Arrange: Day 2 orders
		CustomerOrder orderDay2_1 = new CustomerOrder(10, 1, 2); // Cost: 10, change 0
		CustomerOrder orderDay2_2 = new CustomerOrder(20, 1, 2); // Cost: 10, change 10
		CustomerOrder orderDay2_3 = new CustomerOrder(20, 2, 2); // Cost: 10, change 10

		// Act: Process Day 2 orders
		String result = orderProcessor.processOrders(Arrays.asList(orderDay2_1, orderDay2_2, orderDay2_3));

		// Assert: Expected output is [20, 20].
		String expectedOutput = "[20, 20]";
		assertEquals(expectedOutput, result); // Verify the correct change returned
	}
}
