package com.example.lemonade_stand.order;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Class responsible for processing lemonade orders and managing the bill
 * inventory.
 */
@Component
@Service
@Scope("prototype") // Creates a new instance for each request
public class OrderProcessor {

	private static final int LEMONADE_COST = 5; // Cost of one lemonade
	private Map<Integer, Integer> billCounter = new HashMap<>(); // Counter for bill denominations
	private int totalMoneyMade = 0; // Total revenue generated from sales
	private int totalLemonadesSold = 0; // Total number of lemonades sold

	/**
	 * Constructor to initialize the bill counter with 0 counts for each
	 * denomination.
	 */
	public OrderProcessor() {
		// Initialize bill counter for specific denominations
		billCounter.put(5, 0);
		billCounter.put(10, 0);
		billCounter.put(20, 0);
	}

	/**
	 * Processes a list of customer orders by checking bill values and requested
	 * quantities.
	 * 
	 * @param orders List of customer orders to process.
	 * @return A string representing the remaining bills or "null" if processing
	 *         fails.
	 */
	public String processOrders(List<CustomerOrder> orders) {
		if (orders == null || orders.isEmpty()) {
			return "null"; // Return "null" if the order list is empty
		}

		// Sort orders by their position in line
		orders.sort(Comparator.comparingInt(CustomerOrder::getPositionInLine));

		// Process each order sequentially
		for (CustomerOrder order : orders) {
			// If processing a single order fails, return "null"
			if (processSingleOrder(order.getBillValue(), order.getRequestedLemonades()) == -1) {
				return "null"; // Order processing failed
			}
		}

		// Return the string representation of remaining bills
		return printBillsRemaining();
	}

	/**
	 * Processes a single customer order.
	 * 
	 * @param billValue          The value of the bill used by the customer.
	 * @param requestedLemonades The number of lemonades requested by the customer.
	 * @return 0 if successfully processed, -1 if there is an error.
	 */
	private int processSingleOrder(int billValue, int requestedLemonades) {
		if (requestedLemonades == 0) {
			return -1; // Invalid order if no lemonades are requested
		}

		int lemonadeCost = requestedLemonades * LEMONADE_COST;

		if (billValue < lemonadeCost) {
			return -1; // Insufficient bill value to cover the lemonade cost
		}

		int changeRequired = billValue - lemonadeCost;

		// Process change if required
		if (changeRequired > 0 && processChange(changeRequired) == -1) {
			return -1; // Unable to provide sufficient change
		}

		addBillsToCounter(billValue); // Update the bill counter with the received bill
		updateMoneyMade(lemonadeCost); // Update the total revenue
		totalLemonadesSold += requestedLemonades; // Increment total lemonades sold
		return 0; // Order processed successfully
	}

	/**
	 * Processes the required change for a transaction.
	 * 
	 * @param changeRequired The amount of change that needs to be returned.
	 * @return 0 if the change is successfully provided, -1 otherwise.
	 */
	private int processChange(int changeRequired) {
		// Attempt to provide change using available bills
		while (changeRequired >= 10 && billCounter.get(10) > 0) {
			billCounter.put(10, billCounter.get(10) - 1); // Decrease count of $10 bills
			changeRequired -= 10; // Deduct the bill value from change required
		}
		while (changeRequired >= 5 && billCounter.get(5) > 0) {
			billCounter.put(5, billCounter.get(5) - 1); // Decrease count of $5 bills
			changeRequired -= 5; // Deduct the bill value from change required
		}

		return (changeRequired > 0) ? -1 : 0; // Return -1 if change cannot be provided
	}

	/**
	 * Increments the count of a specific bill denomination in the bill counter.
	 * 
	 * @param billValue The value of the bill to be added to the counter.
	 */
	private void addBillsToCounter(int billValue) {
		billCounter.put(billValue, billCounter.get(billValue) + 1); // Increment bill count
	}

	/**
	 * Updates the total money made from sales.
	 * 
	 * @param moneyMade The amount of money earned from the latest sale.
	 */
	private void updateMoneyMade(int moneyMade) {
		totalMoneyMade += moneyMade; // Increment total money made
	}

	/**
	 * Generates a string representation of the remaining bills in the counter.
	 * 
	 * @return A string containing the list of remaining bills.
	 */
	private String printBillsRemaining() {
		StringBuilder billsRemaining = new StringBuilder("[");
		boolean isFirst = true; // Flag to handle formatting

		// Iterate through the billCounter map and append counts to the string
		for (Map.Entry<Integer, Integer> entry : billCounter.entrySet()) {
			int bill = entry.getKey();
			int count = entry.getValue();

			for (int i = 0; i < count; i++) {
				if (!isFirst) {
					billsRemaining.append(", "); // Add a comma if this is not the first bill
				}
				billsRemaining.append(bill); // Append the bill value
				isFirst = false; // Set flag to false after first entry
			}
		}
		billsRemaining.append("]"); // Close the bills array
		return billsRemaining.toString(); // Return formatted string of remaining bills
	}

	/**
	 * Generates a report detailing total sales and the count of remaining bills.
	 * 
	 * @return A formatted string report with sales and bill information.
	 */
	public String printReport() {
		// Construct a report with sales information and remaining bills
		return String.format(
				"Total Lemonades sold - %d\nTotal Profit Made - %d\nTotal 5 Bills Remaining - %d\nTotal 10 Bills Remaining - %d\nTotal 20 Bills Remaining - %d",
				totalLemonadesSold, totalMoneyMade, billCounter.get(5), billCounter.get(10), billCounter.get(20));
	}
}
