package com.abc;

import java.util.Calendar;
import java.util.Date;

public class Transaction {

	public final double amount;
	private Date transactionDate;

	public Transaction(double amount) {
		this.amount = amount;
		this.transactionDate = DateProvider.getInstance().now();
	}

	// Add getAmount and getDate for each transaction, in order to calculate
	// interest
	public double getAmount() {
		return amount;
	}

	public Date getDate() {
		return transactionDate;
	}

	public void lastYear() {
		// Change transaction date to last year
		// used for testing
		long year = 24L * 3600L * 1000L * 365L;
		transactionDate = new Date(transactionDate.getTime() - year);
	}

	public void tenDaysAgo() {
		// Change transaction date to ten days ago
		// used for testing
		long tenDays = 24L * 3600L * 1000L * 10L;
		transactionDate = new Date(transactionDate.getTime() - tenDays);
	}
}
