package com.example.lemonade_stand.order;

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.lemonade_stand.database.BillCounter;
import com.example.lemonade_stand.database.BillCounterRepository;

/**
 * Service for processing lemonade orders over multiple days. Keeps track of
 * total sales, profits, and bill denominations using a database.
 */
@Component
@Service
@Scope("prototype") // New instance for each request
public class OrderProcessorMultiDay {

	private static final int LEMONADE_COST = 5; // Cost of a single lemonade

	@Autowired
	private BillCounterRepository billCounterRepository; // Repository to manage bills in the database

	private int totalMoneyMade = 0; // Total profit made across all days
	private int totalLemonadesSold = 0; // Total lemonades sold

	/**
	 * Initialize bill counters for 5, 10, and 20 denominations after the bean is
	 * created.
	 */
	@PostConstruct
	public void initializeBillCounter() {
		initializeBill(5);
		initializeBill(10);
		initializeBill(20);
	}

	/**
	 * Ensures that each bill denomination exists in the database. If not, creates
	 * an entry with a count of 0.
	 * 
	 * @param denomination The bill denomination to initialize.
	 */
	private void initializeBill(int denomination) {
		if (billCounterRepository.findByBillDenomination(denomination) == null) {
			billCounterRepository.save(new BillCounter(denomination, 0)); // Save new entry if not present
		}
	}

	/**
	 * Processes a list of customer orders by checking for sufficient bills and
	 * calculating change.
	 * 
	 * @param orders List of customer orders to process.
	 * @return A string detailing remaining bills after processing.
	 */
	public String processOrders(List<CustomerOrder> orders) {
		if (orders == null || orders.isEmpty()) {
			return "null";// Return "null" if the order list is empty
		}

		// Sort the orders based on the customers' position in line.
		orders.sort(Comparator.comparingInt(CustomerOrder::getPositionInLine));

		// Process each order in sequence
		for (CustomerOrder order : orders) {
			if (processSingleOrder(order.getBillValue(), order.getRequestedLemonades()) == -1) {
				return "null";// Order processing failed
			}
		}

		// Return the remaining bills in the system
		return printBillsRemaining();
	}

	/**
	 * Process a single order by handling bill and change transactions.
	 * 
	 * @param billValue          The value of the bill provided by the customer.
	 * @param requestedLemonades The number of lemonades requested.
	 * @return 0 if successfully processed, -1 otherwise.
	 */
	private int processSingleOrder(int billValue, int requestedLemonades) {
		if (requestedLemonades == 0) {
			return -1; // Invalid order if no lemonades requested
		}

		int lemonadeCost = requestedLemonades * LEMONADE_COST;

		if (billValue < lemonadeCost) {
			return -1; // Invalid if the provided bill is less than the total cost
		}

		int changeRequired = billValue - lemonadeCost;

		// Process the required change if needed
		if (changeRequired > 0 && processChange(changeRequired) == -1) {
			return -1; // Return error if change can't be provided
		}

		// Add the received bill to the system and update sales figures
		addBillToDatabase(billValue);
		updateMoneyMade(lemonadeCost);
		totalLemonadesSold += requestedLemonades;
		return 0;
	}

	/**
	 * Handles change processing by checking available bills in the system.
	 * 
	 * @param changeRequired The amount of change that needs to be provided.
	 * @return 0 if the change is successfully processed, -1 otherwise.
	 */
	private int processChange(int changeRequired) {
		if (changeRequired == 15 && getBillCount(5) <= 0) {
			return -1; // Ensure at least one $5 bill is available for $15 change
		}
		// Provide $10 bills if possible
		while (changeRequired >= 10 && getBillCount(10) > 0) {
			removeBillFromDatabase(10);
			changeRequired -= 10;
		}
		// Provide $5 bills if possible
		while (changeRequired >= 5 && getBillCount(5) > 0) {
			removeBillFromDatabase(5);
			changeRequired -= 5;
		}

		return changeRequired == 0 ? 0 : -1; // Return success if change was fully provided
	}

	/**
	 * Retrieves the count of bills available for a given denomination.
	 * 
	 * @param denomination The bill denomination to check.
	 * @return The number of bills available.
	 */
	private int getBillCount(int denomination) {
		BillCounter bill = billCounterRepository.findByBillDenomination(denomination);
		return bill != null ? bill.getCount() : 0;
	}

	/**
	 * Adds a bill of the given value to the database.
	 * 
	 * @param billValue The bill value to be added.
	 */
	private void addBillToDatabase(int billValue) {
		BillCounter billCounter = billCounterRepository.findByBillDenomination(billValue);
		if (billCounter != null) {
			billCounter.setCount(billCounter.getCount() + 1);
			billCounterRepository.save(billCounter); // Persist the updated count
		}
	}

	/**
	 * Removes a bill of the given value from the database.
	 * 
	 * @param billValue The bill value to be removed.
	 */
	private void removeBillFromDatabase(int billValue) {
		BillCounter billCounter = billCounterRepository.findByBillDenomination(billValue);
		if (billCounter != null && billCounter.getCount() > 0) {
			billCounter.setCount(billCounter.getCount() - 1);
			billCounterRepository.save(billCounter); // Persist the updated count
		}
	}

	/**
	 * Updates the total money made by adding the cost of the latest sale.
	 * 
	 * @param moneyMade The amount of money earned from the sale.
	 */
	private void updateMoneyMade(int moneyMade) {
		totalMoneyMade += moneyMade;
	}

	/**
	 * Prints the remaining bills in the system after processing orders.
	 * 
	 * @return A string representation of the remaining bills.
	 */
	private String printBillsRemaining() {
		List<BillCounter> bills = billCounterRepository.findAll();
		StringBuilder billsRemaining = new StringBuilder("[");

		for (BillCounter bill : bills) {
			for (int i = 0; i < bill.getCount(); i++) {
				billsRemaining.append(bill.getBillDenomination()).append(", ");
			}
		}

		// Trim the trailing comma and space
		if (billsRemaining.length() > 1) {
			billsRemaining.setLength(billsRemaining.length() - 2);
		}

		billsRemaining.append("]");
		return billsRemaining.toString();
	}

	/**
	 * Generates a sales report detailing total lemonades sold, total profit made,
	 * and the remaining bills in the system.
	 * 
	 * @return A formatted string report with sales and bill information.
	 */
	public String printReport() {
		List<BillCounter> bills = billCounterRepository.findAll();
		StringBuilder billsRemaining = new StringBuilder();

		for (BillCounter bill : bills) {
			billsRemaining.append(
					String.format("Total %d Bills Remaining - %d\n", bill.getBillDenomination(), bill.getCount()));
		}

		return String.format("Total Lemonades sold so far - %d\nTotal Profit Made - %d\n%s", totalLemonadesSold,
				totalMoneyMade, billsRemaining.toString());
	}
}
