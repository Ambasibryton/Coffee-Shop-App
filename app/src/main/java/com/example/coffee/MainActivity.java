package com.example.coffee;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int coffeeCount = 1;
    private PresfHelper prefs;
    private TextView textCounter, tvWelcome, tvPrice;
    private Spinner spinnerCoffeeType;
    private CheckBox chkChapati, chkGitheri, chkMandazi, chkBread;

    private static final Map<String, Integer> COFFEE_PRICES = new HashMap<>();
    private static final Map<String, Integer> BITE_PRICES = new HashMap<>();

    static {
        COFFEE_PRICES.put("House Blend", 150);
        COFFEE_PRICES.put("Espresso", 150);
        COFFEE_PRICES.put("Cappuccino", 220);
        COFFEE_PRICES.put("Latte", 250);
        COFFEE_PRICES.put("Black Coffee", 120);

        BITE_PRICES.put("Chapati", 50);
        BITE_PRICES.put("Githeri", 100);
        BITE_PRICES.put("Mandazi", 20);
        BITE_PRICES.put("Bread", 40);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PresfHelper(this);
        if (!prefs.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        tvPrice = findViewById(R.id.tvPrice);
        spinnerCoffeeType = findViewById(R.id.spinnerCoffeeType);
        textCounter = findViewById(R.id.textQuantityCounter);
        ImageButton btnMinus = findViewById(R.id.btnMinus);
        ImageButton btnPlus = findViewById(R.id.btnPlus);
        chkChapati = findViewById(R.id.chkChapati);
        chkGitheri = findViewById(R.id.chkGitheri);
        chkMandazi = findViewById(R.id.chkMandazi);
        chkBread = findViewById(R.id.chkBread);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        tvWelcome.setText("👋 Hello, " + prefs.getSessionName());

        String[] coffeeOptions = {"House Blend", "Espresso", "Cappuccino", "Latte", "Black Coffee"};
        spinnerCoffeeType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, coffeeOptions));

        btnMinus.setOnClickListener(v -> {
            if (coffeeCount > 1) {
                coffeeCount--;
                textCounter.setText(String.valueOf(coffeeCount));
                updatePrice();
            }
        });
        btnPlus.setOnClickListener(v -> {
            coffeeCount++;
            textCounter.setText(String.valueOf(coffeeCount));
            updatePrice();
        });

        CheckBox[] boxes = {chkChapati, chkGitheri, chkMandazi, chkBread};
        for (CheckBox cb : boxes) {
            cb.setOnCheckedChangeListener((b, c) -> updatePrice());
        }
        ImageView imgCoffee = findViewById(R.id.imgCoffeePreview);

        spinnerCoffeeType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                updatePrice();

                String type = parent.getItemAtPosition(position).toString();
                int resId = R.drawable.coffee; // default

                switch (type) {
                    case "Espresso":
                        resId = R.drawable.espresso;
                        break;
                    case "Cappuccino":
                        resId = R.drawable.capuccino;
                        break;
                    case "Latte":
                        resId = R.drawable.latte;
                        break;
                    case "Black Coffee":
                        resId = R.drawable.black_coffee;
                        break;
                    case "House Blend":
                        resId = R.drawable.home_blende;
                        break;
                }

                try {
                    imgCoffee.setImageResource(resId);
                } catch (Exception e) {
                    imgCoffee.setImageResource(R.drawable.coffee);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });


        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Menu buttons
        findViewById(R.id.btnMyOrders).setOnClickListener(v ->
                startActivity(new Intent(this, OrdersActivity.class)));
        findViewById(R.id.btnAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        updatePrice();
    }

    private List<String> getSelectedBites() {
        List<String> list = new ArrayList<>();
        if (chkChapati.isChecked()) list.add("Chapati");
        if (chkGitheri.isChecked()) list.add("Githeri");
        if (chkMandazi.isChecked()) list.add("Mandazi");
        if (chkBread.isChecked()) list.add("Bread");
        return list;
    }

    private int calculateTotal() {
        String type = spinnerCoffeeType.getSelectedItem().toString();
        int total = (COFFEE_PRICES.containsKey(type) ? COFFEE_PRICES.get(type) : 0) * coffeeCount;
        for (String b : getSelectedBites()) {
            total += BITE_PRICES.containsKey(b) ? BITE_PRICES.get(b) : 0;
        }
        return total;
    }

    private void updatePrice() {
        tvPrice.setText("Estimated Total: KSh " + calculateTotal());
    }

    private void placeOrder() {
        String coffee = spinnerCoffeeType.getSelectedItem().toString();
        List<String> bites = getSelectedBites();
        int total = calculateTotal();
        String name = prefs.getSessionName();
        String phone = prefs.getSessionPhone();
        showMpesaDialog(coffee, coffeeCount, bites, total, name, phone);
    }

    private void showMpesaDialog(String coffee, int qty, List<String> bites,
                                 int total, String name, String phone) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mpesa);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvAmount = dialog.findViewById(R.id.tvAmount);
        TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        TextView tvPhone = dialog.findViewById(R.id.tvPhone);
        TextView tvStatus = dialog.findViewById(R.id.tvStatus);
        Button btnSimulate = dialog.findViewById(R.id.btnSimulate);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        tvAmount.setText("KSh " + total);
        String bitesText = bites.isEmpty() ? "" : "\nSides: " + TextUtils.join(", ", bites);
        tvDesc.setText(qty + "x " + coffee + bitesText);
        tvPhone.setText("Paying with: " + formatPhone(phone));
        btnSimulate.setText("Pay Now");

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSimulate.setOnClickListener(v -> {
            tvStatus.setText("Processing...\nSending STK Push");
            btnSimulate.setEnabled(false);

            // After short delay, ask for PIN
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialog.dismiss();
                showPinDialog(coffee, qty, bites, total, name, phone);
            }, 1500);
        });

        dialog.show();
    }

    private void showPinDialog(String coffee, int qty, List<String> bites,
                               int total, String name, String phone) {
        Dialog pinDialog = new Dialog(this);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.dialog_mpesa_pin);
        if (pinDialog.getWindow() != null) {
            pinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pinDialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.90),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvPinAmount = pinDialog.findViewById(R.id.tvPinAmount);
        EditText etPin = pinDialog.findViewById(R.id.etPin);
        Button btnConfirm = pinDialog.findViewById(R.id.btnConfirmPin);
        Button btnCancel = pinDialog.findViewById(R.id.btnCancelPin);

        tvPinAmount.setText("KSh " + total);

        btnCancel.setOnClickListener(v -> pinDialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String pin = etPin.getText().toString().trim();
            if (pin.length() != 4) {
                Toast.makeText(this, "Enter a valid 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save order with person's name
            String orderId = "ORD" + String.valueOf(System.currentTimeMillis()).substring(5);
            PresfHelper.Order order = new PresfHelper.Order(
                    orderId, name, phone, coffee, qty, bites, total, "paid", System.currentTimeMillis());
            prefs.addOrder(order);

            // Reset form
            coffeeCount = 1;
            textCounter.setText("1");
            chkChapati.setChecked(false);
            chkGitheri.setChecked(false);
            chkMandazi.setChecked(false);
            chkBread.setChecked(false);
            spinnerCoffeeType.setSelection(0);
            updatePrice();

            pinDialog.dismiss();
            Toast.makeText(this, "Payment successful! Order by " + name, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, OrdersActivity.class));
        });

        pinDialog.show();
    }

    private String formatPhone(String phone) {
        if (phone != null && phone.startsWith("0")) return "+254" + phone.substring(1);
        return phone;
    }
}