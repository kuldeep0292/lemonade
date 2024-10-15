package com.example.lemonade_stand.order;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lemonade_stand.database.BillCounterRecord;
import com.example.lemonade_stand.database.BillCounterRepository;
import com.example.lemonade_stand.database.SalesRecord;
import com.example.lemonade_stand.database.SalesRepository;

@Service
public class OrderRepositoryService {

	@Autowired
	private BillCounterRepository billCounterRepository;

	@Autowired
	private SalesRepository salesRepository;

	private Map<Integer, Integer> billsConsumedFromDB = new HashMap<>(); // map to keep track of bills consumed from db

	/**
	 * Initializes a bill record in the database if it doesn't already exist.
	 *
	 * @param denomination The bill denomination to initialize.
	 */
	public void initializeBill(int denomination) {
		if (billCounterRepository.findByBillDenomination(denomination) == null) {
			billCounterRepository.save(new BillCounterRecord(denomination, 0));
		}
	}

	/**
	 * Initializes a bill record in the database if it doesn't already exist.
	 *
	 * @param denomination The bill denomination to initialize.
	 */
	public void initializeSalesFigures() {
		if (salesRepository.findFirstByOrderByIdDesc() == null) {
			salesRepository.save(new SalesRecord());
		}
	}

	/**
	 * Retrieves the number of bills available in the database for a given
	 * denomination.
	 *
	 * @param denomination The denomination of the bill to check.
	 * @return The number of bills available for the specified denomination.
	 */
	public int getBillCountInDatabase(int denomination) {
		BillCounterRecord lemonadeStandRecord = billCounterRepository.findByBillDenomination(denomination);
		return lemonadeStandRecord != null ? lemonadeStandRecord.getCount() : 0;
	}

	/**
	 * Adds a bill of the given denomination to the database.
	 *
	 * @param billValue The denomination of the bill to be added.
	 */
	public void addBillToDatabase(int billValue) {
		BillCounterRecord record = billCounterRepository.findByBillDenomination(billValue);
		if (record != null) {
			record.setCount(record.getCount() + 1);
			billCounterRepository.save(record);
		}
	}

	/**
	 * Removes a bill of the given denomination from the database.
	 *
	 * @param billValue The denomination of the bill to be removed.
	 */
	public void removeBillFromDatabase(int billValue) {
		BillCounterRecord record = billCounterRepository.findByBillDenomination(billValue);
		if (record != null && record.getCount() > 0) {
			record.setCount(record.getCount() - 1);
			billCounterRepository.save(record);
			// Track bills consumed from the database during the current order
			billsConsumedFromDB.put(billValue, billsConsumedFromDB.getOrDefault(billValue, 0) + 1);
		}
	}

	public BillCounterRecord getLastRecord() {
		return billCounterRepository.findFirstByOrderByIdDesc();
	}

	/**
	 * Restores bills that were consumed from the database during a failed order.
	 * The consumed bills are added back to the database to undo the transaction.
	 */
	void restoreBillsConsumedWhenOrderFailed() {
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
	 * Clears all records in the LemonadeStandRecord table.
	 */
	public void clearAllRecords() {
		// Check if records are present before deleting
		if (billCounterRepository.count() > 0) {
			billCounterRepository.deleteAll();
		}
		// Check if records are present before deleting
		if (salesRepository.count() > 0) {
			salesRepository.deleteAll();
		}
	}
}
