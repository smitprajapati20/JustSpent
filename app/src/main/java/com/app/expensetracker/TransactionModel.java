package com.app.expensetracker;

public class TransactionModel {

    String title, type, date, category, payment;
    double amount;
    int icon;

    public TransactionModel(String title, double amount,
                            String type, String date,
                            String category, int icon,
                            String payment) {

        this.title = title;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.icon = icon;
        this.payment = payment;
    }
}
