package com.example.coffee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PresfHelper prefs = new PresfHelper(this);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        RecyclerView rv = findViewById(R.id.rvOrders);
        TextView empty = findViewById(R.id.tvEmpty);

        String phone = prefs.getSessionPhone();
        String name = prefs.getSessionName();

        if (phone == null) {
            tvSubtitle.setText("Please login first");
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            return;
        }

        List<PresfHelper.Order> orders = prefs.getOrdersForUser(phone);

        if (orders.isEmpty()) {
            tvSubtitle.setText("No orders yet for " + name);
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvSubtitle.setText("Orders for " + name);
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new OrdersAdapter(orders));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {
        private final List<PresfHelper.Order> orders;
        private final SimpleDateFormat fmt =
                new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault());

        OrdersAdapter(List<PresfHelper.Order> orders) {
            this.orders = orders;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvId, tvName, tvDetails, tvTotal, tvDate, tvStatus;
            VH(View v) {
                super(v);
                tvId = v.findViewById(R.id.tvOrderId);
                tvName = v.findViewById(R.id.tvCustomerName);
                tvDetails = v.findViewById(R.id.tvOrderDetails);
                tvTotal = v.findViewById(R.id.tvTotal);
                tvDate = v.findViewById(R.id.tvDate);
                tvStatus = v.findViewById(R.id.tvStatus);
            }
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            PresfHelper.Order o = orders.get(position);
            h.tvId.setText("#" + o.id);
            h.tvStatus.setText(o.status.toUpperCase());
            h.tvName.setText(o.userName);   // person's name
            String bites = o.bites.isEmpty() ? "" :
                    "\nBites: " + android.text.TextUtils.join(", ", o.bites);
            h.tvDetails.setText(o.quantity + "x " + o.coffeeType + bites);
            h.tvTotal.setText("KSh " + o.total);
            h.tvDate.setText(fmt.format(new Date(o.createdAt)));
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }
}