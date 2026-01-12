package com.example.expensetracker.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoExpenses;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_expense_list);
        }

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoExpenses = findViewById(R.id.tvNoExpenses);
        expenseList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadExpenses();
    }

    private void loadExpenses() {
        expenseList.clear();
        expenseList.addAll(dbHelper.getAllExpenses());

        if (expenseList.isEmpty()) {
            tvNoExpenses.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoExpenses.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            if (adapter == null) {
                adapter = new ExpenseAdapter(this, expenseList, this::showExpenseDetailDialog);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showExpenseDetailDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_expense_detail, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(expense);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDeleteConfirmationDialog(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteExpense(expense);
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    loadExpenses();
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
