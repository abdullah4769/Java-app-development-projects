package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalExpense;
    private Button btnAddExpense, btnViewExpenses;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Setup database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);

        // Setup Buttons
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        btnViewExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpenseListActivity.class);
            startActivity(intent);
        });

        // Schedule Daily Reminder
        NotificationHelper.scheduleDailyReminder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void updateDashboard() {
        double total = dbHelper.getTotalExpense();
        tvTotalExpense.setText("₹ " + String.format("%.2f", total));
    }
}
