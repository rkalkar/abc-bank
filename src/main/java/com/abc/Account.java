package com.abc;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Account {

	public static final int CHECKING = 0;
	public static final int SAVINGS = 1;
	public static final int MAXI_SAVINGS = 2;

	private final int accountType;
	//keep track of Account Balance
	private double accountBalance;
	public List<Transaction> transactions;
	

	public Account(int accountType) {
		this.accountType = accountType;
		this.transactions = new ArrayList<Transaction>();
		this.accountBalance = 0.0;
	}

	public void deposit(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Transaction Amount must be greater than zero");
		}  else {
			transactions.add(new Transaction(amount));
			this.accountBalance += amount;
		}
	}

	public void withdraw(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("amount must be greater than zero");
		} else if(amount > this.accountBalance){
			throw new IllegalArgumentException("Insufficient Account Balance for withdrawal");
		} else {
			 transactions.add(new Transaction(-amount));
			this.accountBalance -= amount;
		}
	}

	public double interestEarned() {
		//calculate interest
		switch(accountType){
		case SAVINGS:
			return calcSavingsInterest();
		case MAXI_SAVINGS:
			return calcMaxiSavingsInterest();
		default:
			return calcCheckingInterest();
		}
	}

	private double calcSavingsInterest(){
		double savingsRateTierOne = 0.001;  //Interest is 0.1% for first $1,000
		double savingsRateTierTwo = 0.002;  //Interest is 0.2% for anything above $1,000

		double interest = 0.00;
		double balance = 0.00;
		double accumulate = 0.00;
		Date prev = this.transactions.get(0).getDate();
		Date curr;
		long interval = 0;

		for(int i = 0; i < transactions.size(); i++){
    		Transaction t = this.transactions.get(i);
			if(i > 0){
				curr = t.getDate();
				interval = getDateDiff(prev, curr, TimeUnit.DAYS);
				if(balance <= 1000){
					accumulate = balance * interval * savingsRateTierOne / 365;
				} else {
					accumulate = ((1 * interval/365) + ((balance - 1000) * interval * savingsRateTierTwo / 365));
				}
				interest += accumulate;
				prev = curr;
			}

			balance += t.getAmount(); //update balance
			if(i == transactions.size() - 1) {
				//We have reached the final transaction
				//We need to calculate the interest as of today
				curr = new Date();
				interval = getDateDiff(prev, curr, TimeUnit.DAYS);
				if(balance < 1000){
					accumulate = balance * interval * savingsRateTierOne / 365;
				} else {
					accumulate = ((1 * interval/365) + ((balance - 1000) * interval * savingsRateTierTwo / 365));
				}
				interest += accumulate;
			}
		}
		return interest;
	}

    private double calcMaxiSavingsInterest(){
    	double maxiSavingsRateTierOne = 0.05;  //Interest is 5% if there are no withdrawals in 10 days
    	double maxiSavingsRateTierWithdrawal = 0.001;  //Interest is 0.1% if there are withdrawals in 10 days
    	double interest = 0.00;
    	double balance = 0.00;
    	double accumulate = 0.00;
    	
    	Date prev = this.transactions.get(0).getDate();
    	Date curr;
    	long interval = 0;
    	
    	for(int i = 0; i < transactions.size(); i++){
    		Transaction t = this.transactions.get(i);
    		if(i > 0){
    			curr = t.getDate();
    			interval = getDateDiff(prev, curr, TimeUnit.DAYS);
    			if(transactions.get(i - 1).getAmount()<0) {   //Previous Transaction is a withdrawal
    				if(interval <= 10){ //If the interval is less than 10 days
    					accumulate = balance * interval * maxiSavingsRateTierWithdrawal / 365;
    				} else {
    					accumulate = (balance * 10 * maxiSavingsRateTierWithdrawal/365) + ( balance * (interval - 10) * maxiSavingsRateTierOne/365);
    				}
    			} else {
    				accumulate = balance * interval * maxiSavingsRateTierOne / 365;
    			}
    			interest += accumulate;
    			prev = curr;
    		}
    		balance += t.getAmount(); //update balance
    		
    		if(i == transactions.size() - 1){
    			//We have reached the final transaction
				//We need to calculate the interest as of today
    			curr = new Date();
    			interval = getDateDiff(prev, curr, TimeUnit.DAYS);
    			if(transactions.get(i).getAmount()<0) {   //Previous Transaction is a withdrawal
    				if(interval <= 10){ //If the interval is less than 10 days
    					accumulate = balance * interval * maxiSavingsRateTierWithdrawal / 365;
    				} else {
    					accumulate = (balance * 10 * maxiSavingsRateTierWithdrawal/365) + ( balance * (interval - 10) * maxiSavingsRateTierOne/365);
    				}
    			} else {
    				accumulate = balance * interval * maxiSavingsRateTierOne / 365;
    			}
    			interest += accumulate;
    		}
    	}
    	return interest;
    }
	
	private double calcCheckingInterest(){
		// Flat Interest Rate of 0.1% per annum, which translates to 0.001 * (interval/365)
		double CheckingRateTierOne = 0.001;    
		double interest = 0.00;
		double balance = 0.00;
		double accumulate = 0.00;
		Date prev = this.transactions.get(0).getDate();
		Date curr;
		long interval = 0;

		for(int i = 0; i < transactions.size(); i++){
    		Transaction t = this.transactions.get(i);
			if(i > 0){
				curr = t.getDate();
				interval = getDateDiff(prev, curr, TimeUnit.DAYS);
				accumulate = balance * interval * CheckingRateTierOne / 365;
				interest += accumulate;
				prev = curr;
			}
			balance += t.getAmount(); //update balance 

			if(i == transactions.size() - 1){
				//We have reached the final transaction
				//We need to calculate the interest as of today
				curr = new Date();
				interval = getDateDiff(prev, curr, TimeUnit.DAYS);
				accumulate = balance * interval * CheckingRateTierOne / 365;
				interest += accumulate;
			}
		}
		return interest;
	}

	public double sumTransactions() {
		return checkIfTransactionsExist(true);
	}

	private double checkIfTransactionsExist(boolean checkAll) {
		double amount = 0.0;
		for (Transaction t: transactions)
			amount += t.amount;
		return amount;
	}

	public int getAccountType() {
		return accountType;
	}

	public double getBalance(){
		return accountBalance;
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMilli = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMilli,TimeUnit.MILLISECONDS);
	} 

	public void transfer(Account targetAccount, double amount){
		if(this.equals(targetAccount)){
			throw new IllegalArgumentException("Source and Target Accounts must be different");
		} else if (amount <= 0) {
			throw new IllegalArgumentException("Transfer Amount must be greater than zero");
		} else {
			if(accountBalance < amount){
				throw new IllegalArgumentException("Insufficient Funds in Source Account to process Transfer");
			} else {
				this.withdraw(amount);
				transactions.add(new Transaction(-amount));
			
				targetAccount.deposit(amount);
				targetAccount.transactions.add(new Transaction(amount));
				
			}
		}

	}


}
