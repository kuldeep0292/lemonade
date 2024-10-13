package com.example.lemonade_stand.order;

import com.example.lemonade_stand.exception_handler.InvalidOrderException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerOrder {

	// Instance variables to hold order details
	private final int billValue; // The value of the bill used to pay for the order
	private final int positionInLine; // The customer's position in line
	private final int requestedLemonades; // The number of lemonades requested

	// Allowed bill values
	private static final int[] VALID_BILLS = { 5, 10, 20 };

	// Constructor to initialize a CustomerOrder object
	public CustomerOrder(@JsonProperty("bill_value") int billValue,
			@JsonProperty("position_in_line") int positionInLine,
			@JsonProperty("requested_lemonades") int requestedLemonades) {

		// Validate the bill value; throw an exception if invalid
		if (!isValidBill(billValue)) {
			throw new InvalidOrderException("Invalid bill value: " + billValue + ". Accepted values are 5, 10, or 20.");
		}

		this.billValue = billValue;
		this.positionInLine = positionInLine;
		this.requestedLemonades = requestedLemonades;
	}

	// Method to check if the provided bill value is valid
	private boolean isValidBill(int billValue) {
		// Iterate through the valid bills to check if the provided bill is valid
		for (int validBill : VALID_BILLS) {
			if (validBill == billValue) {
				return true; // Return true if a valid bill is found
			}
		}
		return false; // Return false if no valid bill is found
	}

	// Getters to access private variables
	public int getBillValue() {
		return billValue;
	}

	public int getPositionInLine() {
		return positionInLine;
	}

	public int getRequestedLemonades() {
		return requestedLemonades;
	}

	// Override the toString method for easy logging and debugging
	@Override
	public String toString() {
		return String.format("CustomerOrder [billValue=%d, positionInLine=%d, requestedLemonades=%d]", billValue,
				positionInLine, requestedLemonades);
	}
}
