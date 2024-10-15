package com.example.lemonade_stand.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lemonade_stand.database.BillCounterRecord;
import com.example.lemonade_stand.database.BillCounterRepository;
import com.example.lemonade_stand.database.SalesRecord;
import com.example.lemonade_stand.database.SalesRepository;
import com.example.lemonade_stand.util.AppConstants;

/**
 * Service responsible for generating sales and bill reports for the lemonade
 * stand.
 */
@Service
public class SalesReportGenerator {

	@Autowired
	private SalesRepository salesRepository;
	@Autowired
	private BillCounterRepository billCounterRepository;

	/**
	 * Generates a sales report detailing total lemonades sold, total profit made,
	 * and the remaining bills in the system.
	 *
	 * @return A formatted string report with sales and bill information.
	 */
	public String getCompleteSalesReport() {
		SalesRecord salesRecord = salesRepository.findFirstByOrderByIdDesc();
		List<BillCounterRecord> bills = billCounterRepository.findAll();
		StringBuilder billsRemaining = new StringBuilder();

		for (BillCounterRecord bill : bills) {
			billsRemaining.append(
					String.format("Total %d Bills Remaining - %d\n", bill.getBillDenomination(), bill.getCount()));
		}

		return String.format("Total Lemonades sold so far - %d\nTotal Profit Made - %d\n%s",
				salesRecord.getTotalLemonadesSold(), salesRecord.getTotalLemonadesSold() * AppConstants.LEMONADE_COST,
				billsRemaining.toString());
	}
}
