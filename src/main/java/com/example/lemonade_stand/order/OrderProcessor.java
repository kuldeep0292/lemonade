package com.example.lemonade_stand.order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.lemonade_stand.database.LemonadeStandRecord;
import com.example.lemonade_stand.database.LemonadeStandRecordRepository;

/**
 * Service for processing lemonade orders over multiple days. Keeps track of
 * total sales, profits, and bill denominations using a database.
 */
@Service
@Scope("prototype") // New instance for each request
public class OrderProcessor {

	private static final int LEMONADE_COST = 5; // Cost of a single lemonade

	@Autowired
	private LemonadeStandRecordRepository lemonadeStandRecordRepository; // Repository to manage bills in the database

	private Map<Integer, Integer> billCounter = new HashMap<>(); // Using map to track bills for this order batch
	private int lemonadesSoldCurrentOrder;
	private boolean isSaleComplete = true;
	private Map<Integer, Integer> billsConsumedFromDB = new HashMap<>(); // map to keep track of bills consumed from db

	/**
	 * Initialize bill counters for 5, 10, and 20 denominations after the bean is
	 * created.
	 */
	@PostConstruct
	public void initializeBillCounter() {
		initializeBill(5);
		initializeBill(10);
		initializeBill(20);
		// Ensure sales record is present
		if (lemonadeStandRecordRepository.findFirstByOrderByIdDesc() == null) {
			lemonadeStandRecordRepository.save(new LemonadeStandRecord()); // Initialize sales record if it doesn't
																			// exist
		}
	}

	/**
	 * Processes a list of customer orders by checking for sufficient bills and
	 * calculating change.
	 *
	 * @param orders List of customer orders to process.
	 * @return A string detailing the bills collected during the current order minus
	 *         the bills consumed for change.
	 */
	public String processOrders(List<CustomerOrder> orders) {
		resetCurrentOrderCache();
		if (orders == null || orders.isEmpty()) {
			return "null"; // Return "null" if the order list is empty
		}

		// Sort the orders based on the customers' position in line.
		orders.sort(Comparator.comparingInt(CustomerOrder::getPositionInLine));

		// Process each order in sequence
		for (CustomerOrder order : orders) {
			if (processSingleOrder(order.getBillValue(), order.getRequestedLemonades(), billCounter) == -1) {
				isSaleComplete = false;
				return "null"; // Order processing failed
			}
		}

		// update lemonades sold count only if order sale is successful
		if (isSaleComplete) {
			updateSaleInDatabase(lemonadesSoldCurrentOrder);
		} else {
			restoreBillsConsumedWhenOrderFailed();
		}

		// Return the list of bills collected only during the current order
		return getBillsRemainingCurrentOrder();
	}

	/**
	 * Generates a sales report detailing total lemonades sold, total profit made,
	 * and the remaining bills in the system.
	 *
	 * @return A formatted string report with sales and bill information.
	 */
	public String getCompleteSalesReport() {
		LemonadeStandRecord lemonadeStandRecord = lemonadeStandRecordRepository.findFirstByOrderByIdDesc();
		StringBuilder billsRemaining = new StringBuilder();

		List<LemonadeStandRecord> bills = lemonadeStandRecordRepository.findAll();
		for (LemonadeStandRecord bill : bills) {
			billsRemaining.append(
					String.format("Total %d Bills Remaining - %d\n", bill.getBillDenomination(), bill.getCount()));
		}

		return String.format("Total Lemonades sold so far - %d\nTotal Profit Made - %d\n%s",
				lemonadeStandRecord.getTotalLemonadesSold(), lemonadeStandRecord.getTotalLemonadesSold() * 5,
				billsRemaining.toString());
	}

	/**
	 * Resets the current order cache by clearing the bill counter and resetting the
	 * count of lemonades sold during the current order. This ensures that each new
	 * order batch starts with a clean slate.
	 */
	private void resetCurrentOrderCache() {
		// Clear the map tracking bills collected in the current order
		billCounter.clear();

		// Reset the count of lemonades sold in the current order to 0
		lemonadesSoldCurrentOrder = 0;
		isSaleComplete = true;
	}

	/**
	 * Process a single order by handling bill and change transactions.
	 *
	 * @param billValue          The value of the bill provided by the customer.
	 * @param requestedLemonades The number of lemonades requested.
	 * @param billCounter        The map of bills collected and consumed during this
	 *                           order batch.
	 * @return 0 if successfully processed, -1 otherwise.
	 */
	private int processSingleOrder(int billValue, int requestedLemonades, Map<Integer, Integer> billCounter) {
		if (requestedLemonades == 0) {
			return -1; // Invalid order if no lemonades requested
		}

		int lemonadeCost = requestedLemonades * LEMONADE_COST;

		if (billValue < lemonadeCost) {
			return -1; // Invalid if the provided bill is less than the total cost
		}

		int changeRequired = billValue - lemonadeCost;

		// Track the bill collected from the customer
		billCounter.put(billValue, billCounter.getOrDefault(billValue, 0) + 1);

		// Process the required change if needed
		if (changeRequired > 0 && processChange(changeRequired, billCounter) == -1) {
			return -1; // Return error if change can't be provided
		}
		lemonadesSoldCurrentOrder += requestedLemonades;
		return 0;
	}

	/**
	 * Updates the sales record by adding the number of lemonades sold in the
	 * current order batch to the total lemonades sold in the system. Also adds the
	 * remaining bills from the current order batch to the database.
	 * 
	 * @param lemonadesSold The number of lemonades sold in the current order.
	 */
	private void updateSaleInDatabase(int lemonadesSold) {
		// Fetch the most recent sales record from the database
		LemonadeStandRecord lemonadeStandRecord = lemonadeStandRecordRepository.findFirstByOrderByIdDesc();

		if (lemonadeStandRecord != null) {
			// Increment the total lemonades sold by the current order's sales
			lemonadeStandRecord.setTotalLemonadesSold(lemonadeStandRecord.getTotalLemonadesSold() + lemonadesSold);
			lemonadeStandRecordRepository.save(lemonadeStandRecord); // Persist the updated sales record
		}

		// Add the remaining bills from the current order to the database
		for (Map.Entry<Integer, Integer> entry : billCounter.entrySet()) {
			int denomination = entry.getKey();
			int count = entry.getValue();

			// For each collected bill, add it to the database
			for (int i = 0; i < count; i++) {
				addBillToDatabase(denomination); // Persist bill into the database
			}
		}
	}

	/**
	 * Processes the required change by checking available bills in the system and
	 * consuming bills to match the change required.
	 * 
	 * @param changeRequired The amount of change that needs to be provided.
	 * @param billCounter    The map tracking bills collected and consumed in the
	 *                       current order.
	 * @return 0 if the change was successfully provided, -1 otherwise.
	 */
	private int processChange(int changeRequired, Map<Integer, Integer> billCounter) {
		// Special check for $15 change: ensure a $5 bill is available
		if (changeRequired == 15 && !(getBillCountinDatabase(5) > 0)) {
			return -1; // Cannot provide change, sale fails
		}

		// Provide $10 bills if possible
		while (changeRequired >= 10 && getBillCountinDatabase(10) > 0) {
			if (billCounter.getOrDefault(10, 0) > 0) {
				billCounter.put(10, billCounter.getOrDefault(10, 0) - 1); // Track $10 bill consumption
				changeRequired -= 10;
			} else {
				removeBillFromDatabase(10);
				changeRequired -= 10;
			}
		}

		// Provide $5 bills if possible
		while (changeRequired >= 5 && getBillCountinDatabase(5) > 0) {
			if (billCounter.getOrDefault(5, 0) > 0) {
				billCounter.put(5, billCounter.getOrDefault(5, 0) - 1); // Track $5 bill consumption
				changeRequired -= 5;
			} else {
				removeBillFromDatabase(5);
				changeRequired -= 5;
			}
		}

		// Return success if exact change was provided, otherwise fail
		return changeRequired == 0 ? 0 : -1;
	}

	/**
	 * Retrieves the number of bills available in the database for a given
	 * denomination.
	 * 
	 * @param denomination The denomination of the bill to check.
	 * @return The number of bills available for the specified denomination.
	 */
	private int getBillCountinDatabase(int denomination) {
		// Query the database for the specific bill denomination
		LemonadeStandRecord lemonadeStandRecord = lemonadeStandRecordRepository.findByBillDenomination(denomination);

		// Return the bill count or 0 if the denomination doesn't exist
		return lemonadeStandRecord != null ? lemonadeStandRecord.getCount() : 0;
	}

	/**
	 * Adds a bill of the given denomination to the database.
	 * 
	 * @param billValue The denomination of the bill to be added.
	 */
	private void addBillToDatabase(int billValue) {
		// Find the bill entry in the database
		LemonadeStandRecord lemonadeStandRecord = lemonadeStandRecordRepository.findByBillDenomination(billValue);

		// If the bill entry exists, increment the count and save
		if (lemonadeStandRecord != null) {
			lemonadeStandRecord.setCount(lemonadeStandRecord.getCount() + 1);
			lemonadeStandRecordRepository.save(lemonadeStandRecord); // Persist the updated count
		}
	}

	/**
	 * Removes a bill of the given denomination from the database.
	 * 
	 * @param billValue The denomination of the bill to be removed.
	 */
	private void removeBillFromDatabase(int billValue) {
		// Find the bill entry in the database
		LemonadeStandRecord lemonadeStandRecord = lemonadeStandRecordRepository.findByBillDenomination(billValue);

		// Ensure the bill exists and there are bills left to remove
		if (lemonadeStandRecord != null && lemonadeStandRecord.getCount() > 0) {
			lemonadeStandRecord.setCount(lemonadeStandRecord.getCount() - 1); // Decrement bill count
			lemonadeStandRecordRepository.save(lemonadeStandRecord); // Persist the updated count

			// Track bills consumed from the database during the current order
			billsConsumedFromDB.put(billValue, billsConsumedFromDB.getOrDefault(billValue, 0) + 1);
		}
	}

	/**
	 * Restores bills that were consumed from the database during a failed order.
	 * The consumed bills are added back to the database to undo the transaction.
	 */
	private void restoreBillsConsumedWhenOrderFailed() {
		// Loop through each consumed bill and restore it to the database
		for (Map.Entry<Integer, Integer> entry : billsConsumedFromDB.entrySet()) {
			int denomination = entry.getKey();
			int count = entry.getValue();

			// Add back each consumed bill to the database
			for (int i = 0; i < count; i++) {
				addBillToDatabase(denomination);
			}
		}
	}

	/**
	 * Generates a string representation of the remaining bills in the current
	 * order. This method converts the bill counter map to a list format for
	 * display.
	 * 
	 * @return A formatted string containing the remaining bills in the current
	 *         order.
	 */
	private String getBillsRemainingCurrentOrder() {
		// Convert the billCounter map to a list of remaining bills
		List<Integer> result = new ArrayList<>();

		// Loop through each denomination and count in the billCounter
		for (Map.Entry<Integer, Integer> entry : billCounter.entrySet()) {
			int denomination = entry.getKey();
			int count = entry.getValue();

			// Add each bill to the result list
			for (int i = 0; i < count; i++) {
				result.add(denomination);
			}
		}

		return result.toString(); // Return the formatted list as a string
	}

	/**
	 * Ensures that each bill denomination exists in the database. If not, creates
	 * an entry with a count of 0.
	 *
	 * @param denomination The bill denomination to initialize.
	 */
	private void initializeBill(int denomination) {
		if (lemonadeStandRecordRepository.findByBillDenomination(denomination) == null) {
			lemonadeStandRecordRepository.save(new LemonadeStandRecord(denomination, 0)); // Save new entry if not
																							// present
		}
	}
}
