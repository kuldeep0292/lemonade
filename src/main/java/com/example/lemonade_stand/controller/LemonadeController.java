package com.example.lemonade_stand.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lemonade_stand.order.CustomerOrder;
import com.example.lemonade_stand.order.OrderProcessor;

/**
 * Controller for handling lemonade stand operations, including processing
 * orders and generating reports.
 */
@RestController
@RequestMapping("api/orders")
public class LemonadeController {

	@Autowired
	private OrderProcessor orderProcessor;

	/**
	 * Endpoint for processing lemonade orders.
	 * 
	 * @param orders List of customer orders.
	 * @return String representing the remaining bills or "null" if failed.
	 */
	@PostMapping("/process")
	public String processOrder(@RequestBody List<CustomerOrder> orders) {
		String result = orderProcessor.processOrders(orders);
		StringBuilder response = new StringBuilder();
		response = response.append(result).append("\n\n").append(orderProcessor.getCompleteSalesReport());
		return response.toString();
	}

	/**
	 * Endpoint for generating a report of total lemonades sold and remaining bills.
	 * 
	 * @return Report string with sales and bill information.
	 */
	@GetMapping("/report")
	public String generateReport() {
		return orderProcessor.getCompleteSalesReport();
	}
}
