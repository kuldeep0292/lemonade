package com.example.lemonade_stand;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

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

	/**
	 * Test case for generating a report after processing valid orders. Expects the
	 * report to show the correct total lemonades sold and total profit made.
	 */
	@Test
	void testPrintReport_AfterProcessingOrders() {
		// Processing a valid order
		CustomerOrder order = new CustomerOrder(10, 1, 2); // Cost: 10
		orderProcessor.processOrders(Collections.singletonList(order));
		String report = salesReportGenerator.getCompleteSalesReport();
		assertTrue(report.contains("Total Lemonades sold so far - 2")); // Should show correct number of lemonades sold.
		assertTrue(report.contains("Total Profit Made - 10")); // Should show correct profit made.
	}

	/**
	 * Test case for generating a report before any orders have been processed.
	 * Expects the report to show totals as zero.
	 */
	@Test
	void testPrintReport_BeforeProcessingOrders() {
		String report = salesReportGenerator.getCompleteSalesReport();
		assertTrue(report.contains("Total Lemonades sold so far - 0")); // Should show zero sold before any processing.
		assertTrue(report.contains("Total Profit Made - 0")); // Should show zero profit before any processing.
	}
}
