package com.example.lemonade_stand.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.example.lemonade_stand.order.CustomerOrder;
import com.example.lemonade_stand.order.OrderProcessor;
import com.example.lemonade_stand.order.OrderProcessorMultiDay;

import java.util.List;

/**
 * Controller for handling lemonade orders.
 * Provides endpoints for processing orders and retrieving sales reports.
 */
@RestController
@RequestMapping("/api/orders")
@Component
@Scope("prototype") // Ensures a new instance is created for each request
public class LemonadeController {

    @Autowired
    private OrderProcessor orderProcessor; // Single-day order processor service
    
    @Autowired
    private OrderProcessorMultiDay orderProcessorMultiDay; // Multi-day order processor service

    /**
     * Endpoint to process a list of customer orders.
     * 
     * @param orders A list of CustomerOrder objects received via POST request body.
     * @return A string representing the result of the order processing (e.g., bills remaining).
     * 
     * This method uses the multi-day processor to handle orders.
     */
    @PostMapping("/process")
    public String processOrders(@RequestBody List<CustomerOrder> orders) {
        // Process customer orders using the multi-day order processor.
        return orderProcessorMultiDay.processOrders(orders);
    }

    /**
     * Endpoint to retrieve the sales report.
     * 
     * @return A string report detailing total sales, profit, and remaining bills.
     * 
     * This method retrieves the sales report for multiple days of operation.
     */
    @GetMapping("/report")
    public String getReport() {
        // Return the sales report from the multi-day order processor.
        return orderProcessorMultiDay.printReport();
    }
}
