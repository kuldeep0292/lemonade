package com.example.lemonade_stand;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.example.lemonade_stand.order.SalesReportGenerator;

@SpringBootTest
class ReportSummaryTests {
	@Autowired
	private OrderProcessor orderProcessor;
	@Autowired
	private OrderRepositoryService orderRepositoryService;
	@Autowired
	private SalesReportGenerator salesReportGenerator;

	@BeforeEach
	void setUp() {
		orderRepositoryService.clearAllRecords(); // clear records so that each test case can be independently executed
		orderProcessor.initializeBillCounter();
	}

	@Test
	void testLemonadeCountInReport() {
		// Arrange: Create an instance of OrderProcessor and a list of orders
		List<CustomerOrder> orders = Arrays.asList(new CustomerOrder(10, 1, 2), // Bill value: $10
				new CustomerOrder(5, 2, 1) // Bill value: $5
		);

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: Check if the report contains the correct lemonade count
		assertTrue(report.contains("Total Lemonades sold so far - 3"), "Lemonade count should be 3");
	}

	@Test
	void testProfitInReport() {
		// Arrange: Create an instance of OrderProcessor and a list of orders
		List<CustomerOrder> orders = Arrays.asList(new CustomerOrder(20, 1, 4), // Bill value: $20
				new CustomerOrder(10, 2, 2) // Bill value: $10
		);
		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: Check if the report contains the correct profit made
		assertTrue(report.contains("Total Profit Made - 30"), "Profit should be calculated correctly");
	}

	@Test
	void testBillDenominationsSingleDayOrder() {
		// Arrange: Create an instance of OrderProcessor and a single day order
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(5, 1, 1)); // Bill value: $5

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: Verify that the report contains the correct bill denomination
		assertTrue(report.contains("Total 5 Bills Remaining - 1"), "Bill denominations should be correct");
	}

	@Test
	void testBillDenominationsMultiDayOrders() {
		// Arrange: Create an instance of OrderProcessor and multiple day orders
		List<CustomerOrder> orders = Arrays.asList(new CustomerOrder(10, 1, 2), // Bill value: $10
				new CustomerOrder(5, 2, 1) // Bill value: $5
		);

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: Verify that the report contains both bill denominations
		assertTrue(report.contains("Total 5 Bills Remaining - 1"), "Bill denominations should be correct");
		assertTrue(report.contains("Total 10 Bills Remaining - 1"), "Bill denominations should be correct");
	}

	@Test
	void testReportInvalidOrder() {
		// Arrange: Create an instance of OrderProcessor and an invalid order
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(5, 1, 0)); // Bill value: $5, Invalid
																							// lemonade count
		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: The report should indicate failure for the invalid order
		assertTrue(report.contains("null"), "Report should indicate failure for invalid order");
	}

	@Test
	void testNoIncrementDueToInsufficientChange() {
		// Arrange: Create an instance of OrderProcessor and an order that requires
		// change
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(5, 1, 1)); // Bill value: $5, Invalid
																							// change required

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: validate report figures have not been incremented
		assertTrue(validateEmptyReport(report), "Report should indicate correct sales figure");
	}

	@Test
	void testNoIncrementDueToInsufficientBalance() {
		// Arrange: Create an instance of OrderProcessor and an order with insufficient
		// balance
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(5, 1, 1)); // Bill value: $5, Invalid
																							// due to balance

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: validate report figures have not been incremented
		assertTrue(validateEmptyReport(report), "Report should indicate correct sales figure");
	}

	@Test
	void testNoIncrementDueToInvalidLemonadeCount() {
		// Arrange: Create an instance of OrderProcessor with an invalid order (0
		// lemonades)
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(5, 1, 0)); // Bill value: $5, Invalid
																							// lemonade count

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: validate report figures have not been incremented
		assertTrue(validateEmptyReport(report), "Report should indicate correct sales figure");
	}

	@Test
	void testNoIncrementDueToInvalidBillValue() {
		// Arrange: Create an instance of OrderProcessor and an order with an invalid
		// bill value
		List<CustomerOrder> orders = Collections.singletonList(new CustomerOrder(15, 1, 1)); // Invalid bill value: $15

		// Act: Process the orders and get the report
		orderProcessor.processOrders(orders);
		String report = salesReportGenerator.getCompleteSalesReport();

		// Assert: validate report figures have not been incremented
		assertTrue(validateEmptyReport(report), "Report should indicate correct sales figure");
	}

	@Test
	void testNoIncrementDueToNullOrder() {
		// Act: Process the orders with a null value
		orderProcessor.processOrders(null);
		String report = orderProcessor.processOrders(null); // Passing null order

		// Assert: validate report figures have not been incremented
		assertTrue(validateEmptyReport(report), "Report should indicate correct sales figure");
	}

	private boolean validateEmptyReport(String report) {
		// Check if the report contains all the required texts
		return report.contains("Total Lemonades sold so far - 0") && report.contains("Total Profit Made - 0")
				&& report.contains("Total 5 Bills Remaining - 0") && report.contains("Total 10 Bills Remaining - 0")
				&& report.contains("Total 20 Bills Remaining - 0");
	}

}
