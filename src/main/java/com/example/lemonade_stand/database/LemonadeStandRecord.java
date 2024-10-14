package com.example.lemonade_stand.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LemonadeStandRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Unique identifier for the record
	private int billDenomination; // The denomination of the bill
	private int count; // Count of bills for this denomination
	private int totalLemonadesSold; // Total number of lemonades sold for this denomination

	// Default constructor
	public LemonadeStandRecord() {
	}

	// Constructor
	public LemonadeStandRecord(int billDenomination, int count) {
		this.billDenomination = billDenomination;
		this.count = count;
		this.totalLemonadesSold = 0; // Initialize with zero sold
	}

	// Getters and Setters
	public int getBillDenomination() {
		return billDenomination;
	}

	public void setBillDenomination(int billDenomination) {
		this.billDenomination = billDenomination;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotalLemonadesSold() {
		return totalLemonadesSold;
	}

	public void setTotalLemonadesSold(int totalLemonadesSold) {
		this.totalLemonadesSold = totalLemonadesSold;
	}

	// Method to increment count of bills
	public void incrementCount() {
		this.count++;
	}

	// Method to decrement count of bills
	public void decrementCount() {
		if (this.count > 0) {
			this.count--;
		}
	}

	// Method to add to the total lemonades sold
	public void incrementLemonadesSold(int amount) {
		this.totalLemonadesSold += amount;
	}

	// Method to add bills
	public void addBills(int amount) {
		this.count += amount;
	}
}
