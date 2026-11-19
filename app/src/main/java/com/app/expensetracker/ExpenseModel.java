package com.app.expensetracker;

public class ExpenseModel {

    String title, date, category, payment;
    double amount;
    int icon;

    public ExpenseModel(String title, double amount,
                        String date, String category, int icon,
                        String payment) {

        this.title = title;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.icon = icon;
        this.payment = payment;
    }
}
