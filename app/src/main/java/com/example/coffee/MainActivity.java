package com.example.coffee;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int coffeeCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize XML Views
        Spinner spinnerCoffeeType = findViewById(R.id.spinnerCoffeeType);
        TextView textCounter = findViewById(R.id.textQuantityCounter);
        ImageButton btnMinus = findViewById(R.id.btnMinus);
        ImageButton btnPlus = findViewById(R.id.btnPlus);

        CheckBox chkChapati = findViewById(R.id.chkChapati);
        CheckBox chkGitheri = findViewById(R.id.chkGitheri);
        CheckBox chkMandazi = findViewById(R.id.chkMandazi);
        CheckBox chkBread = findViewById(R.id.chkBread);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // 2. Populate the Coffee Type Dropdown (Spinner)
        String[] coffeeOptions = {"House Blend", "Espresso", "Cappuccino", "Latte", "Black Coffee"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, coffeeOptions);
        spinnerCoffeeType.setAdapter(adapter);

        // 3. Quantity Counter Logic
        btnMinus.setOnClickListener(v -> {
            if (coffeeCount > 1) {
                coffeeCount--;
                textCounter.setText(String.valueOf(coffeeCount));
            }
        });

        btnPlus.setOnClickListener(v -> {
            coffeeCount++;
            textCounter.setText(String.valueOf(coffeeCount));
        });

        // 4. Place Order Logic
        btnPlaceOrder.setOnClickListener(v -> {
            String selectedCoffee = spinnerCoffeeType.getSelectedItem().toString();

            // Gather selected accompaniments
            ArrayList<String> snacks = new ArrayList<>();
            if (chkChapati.isChecked()) snacks.add("Chapati");
            if (chkGitheri.isChecked()) snacks.add("Githeri");
            if (chkMandazi.isChecked()) snacks.add("Mandazi");
            if (chkBread.isChecked()) snacks.add("Bread");

            // Build confirmation message
            String receipt = "Ordered: " + coffeeCount + "x " + selectedCoffee;
            if (!snacks.isEmpty()) {
                receipt += "\nSides: " + String.join(", ", snacks);
            }

            // Display pop-up alert message
            Toast.makeText(MainActivity.this, receipt, Toast.LENGTH_LONG).show();
        });
    }
}