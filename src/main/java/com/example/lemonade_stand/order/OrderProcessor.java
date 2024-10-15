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

import com.example.lemonade_stand.database.SalesRecord;
import com.example.lemonade_stand.database.SalesRepository;
import com.example.lemonade_stand.exception_handler.InvalidOrderException;
import com.example.lemonade_stand.util.AppConstants;

/**
 * Service for processing lemonade orders over multiple days. Keeps track of
 * total sales, profits, and bill denominations using a database.
 */
@Service
@Scope("prototype") // New instance for each request
public class OrderProcessor {

	@Autowired
	private OrderRepositoryService orderRepositoryService;
	@Autowired
	private SalesRepository salesRepository; // Repository to manage bills in the database
	private Map<Integer, Integer> billsFromThisOrder = new HashMap<>(); // Using map to track bills for this order batch
	private int lemonadesSold;
	private boolean isSaleComplete = true;

	/**
	 * Initialize bill counters for 5, 10, and 20 denominations after the bean is
	 * created.
	 */
	@PostConstruct
	public void initializeDB() {
		for (int denomination : AppConstants.BILL_DENOMINATIONS) {
			orderRepositoryService.initializeBill(denomination);
		}
		orderRepositoryService.initializeSalesFigures();
	}

	/**
	 * /** Resets the current order cache by clearing the bill counter and resetting
	 * the count of lemonades sold during the current order. This ensures that each
	 * new order batch starts with a clean slate.
	 */
	private void resetCurrentOrder() {
		// Clear the map tracking bills collected in the current order
		billsFromThisOrder.clear();

		// Reset the count of lemonades sold in the current order to 0
		lemonadesSold = 0;
		isSaleComplete = true;
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
		if (orders == null || orders.isEmpty() || orders.contains(null)) {
			return "null"; // Return "null" if the order list is empty
		}
		resetCurrentOrder();

		// Sort the orders based on the customers' position in line.
		orders.sort(Comparator.comparingInt(CustomerOrder::getPositionInLine));

		// Process each order in sequence
		for (CustomerOrder order : orders) {
			if (processSingleOrder(order.getBillValue(), order.getRequestedLemonades(), billsFromThisOrder) == -1) {
				isSaleComplete = false;
				return "null"; // Order processing failed
			}
		}

		// update lemonades sold count only if order sale is successful
		if (isSaleComplete) {
			updateSaleInDatabase(lemonadesSold);
		} else {
			orderRepositoryService.restoreBillsConsumedWhenOrderFailed();
		}

		// Return the list of bills collected only during the current order
		return getBillsRemainingCurrentOrder();
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

		int lemonadeCost = requestedLemonades * AppConstants.LEMONADE_COST;

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
		lemonadesSold += requestedLemonades;
		return 0;
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
		Map<Integer, Integer> availableBillsFromDB = fetchAvailableBillsFromDB();
		// Special check for $15 change: ensure a $5 bill is available
		if (changeRequired == 15 && !(availableBillsFromDB.getOrDefault(5, 0) > 0)) {
			return -1; // Cannot provide change, sale fails
		}

		// Provide $10 bills if possible
		while (changeRequired >= 10 && availableBillsFromDB.getOrDefault(10, 0) > 0) {
			if (billCounter.getOrDefault(10, 0) > 0) {
				billCounter.put(10, billCounter.getOrDefault(10, 0) - 1); // Track $10 bill consumption
				changeRequired -= 10;
			} else {
				orderRepositoryService.removeBillFromDatabase(10);
				changeRequired -= 10;
			}
		}

		// Provide $5 bills if possible
		while (changeRequired >= 5 && availableBillsFromDB.getOrDefault(5, 0) > 0) {
			if (billCounter.getOrDefault(5, 0) > 0) {
				billCounter.put(5, billCounter.getOrDefault(5, 0) - 1); // Track $5 bill consumption
				changeRequired -= 5;
			} else {
				orderRepositoryService.removeBillFromDatabase(5);
				changeRequired -= 5;
			}
		}

		// Return success if exact change was provided, otherwise fail
		return changeRequired == 0 ? 0 : -1;
	}

	private Map<Integer, Integer> fetchAvailableBillsFromDB() {
		// Fetch all available bills and return as a map for quick lookup
		Map<Integer, Integer> availableBills = new HashMap<>();
		availableBills.put(5, orderRepositoryService.getBillCountInDatabase(5));
		availableBills.put(10, orderRepositoryService.getBillCountInDatabase(10));
		return availableBills;
	}

	/**
	 * Updates the sales record by adding the number of lemonades sold in the
	 * current order batch to the total lemonades sold in the system. Also adds the
	 * remaining bills from the current order batch to the database.
	 * 
	 * @param lemonadesSold The number of lemonades sold in the current order.
	 */
	private void updateSaleInDatabase(int lemonadesSold) {
		// Increment the total lemonades sold by the current order's sales
		SalesRecord salesRecord = salesRepository.findFirstByOrderByIdDesc();
		if (salesRecord != null) {
			salesRecord.setTotalLemonadesSold(salesRecord.getTotalLemonadesSold() + lemonadesSold);
			salesRepository.save(salesRecord); // Persist the updated sales record
		}

		// Add the remaining bills from the current order to the database
		for (Map.Entry<Integer, Integer> entry : billsFromThisOrder.entrySet()) {
			int denomination = entry.getKey();
			int count = entry.getValue();

			// For each collected bill, add it to the database
			for (int i = 0; i < count; i++) {
				orderRepositoryService.addBillToDatabase(denomination); // Persist bill into the database
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
		for (Map.Entry<Integer, Integer> entry : billsFromThisOrder.entrySet()) {
			int denomination = entry.getKey();
			int count = entry.getValue();

			// Add each bill to the result list
			for (int i = 0; i < count; i++) {
				result.add(denomination);
			}
		}

		return result.toString(); // Return the formatted list as a string
	}

}
