package com.example.coffee;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PresfHelper {

    private final SharedPreferences prefs;

    public PresfHelper(Context context) {
        prefs = context.getSharedPreferences("coffee_prefs", Context.MODE_PRIVATE);
    }

    // ===== Session =====
    public void saveSession(String name, String phone) {
        prefs.edit()
                .putString("session_name", name)
                .putString("session_phone", phone)
                .apply();
    }

    public String getSessionName() {
        return prefs.getString("session_name", null);
    }

    public String getSessionPhone() {
        return prefs.getString("session_phone", null);
    }

    public boolean isLoggedIn() {
        return getSessionPhone() != null;
    }

    public void clearSession() {
        prefs.edit()
                .remove("session_name")
                .remove("session_phone")
                .apply();
    }

    // ===== Users =====
    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString("users", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new User(
                        obj.getString("name"),
                        obj.getString("phone"),
                        obj.getString("password")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveUsers(List<User> users) {
        try {
            JSONArray arr = new JSONArray();
            for (User u : users) {
                JSONObject obj = new JSONObject();
                obj.put("name", u.name);
                obj.put("phone", u.phone);
                obj.put("password", u.password);
                arr.put(obj);
            }
            prefs.edit().putString("users", arr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        List<User> users = getUsers();
        for (User u : users) {
            if (u.phone.equals(user.phone)) return false;
        }
        users.add(user);
        saveUsers(users);
        return true;
    }

    public User login(String phone, String password) {
        for (User u : getUsers()) {
            if (u.phone.equals(phone) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    // ===== Orders =====
    public List<Order> getOrders() {
        List<Order> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(prefs.getString("orders", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONArray bitesArr = obj.getJSONArray("bites");
                List<String> bites = new ArrayList<>();
                for (int j = 0; j < bitesArr.length(); j++) {
                    bites.add(bitesArr.getString(j));
                }
                list.add(new Order(
                        obj.getString("id"),
                        obj.getString("userName"),
                        obj.getString("userPhone"),
                        obj.getString("coffeeType"),
                        obj.getInt("quantity"),
                        bites,
                        obj.getInt("total"),
                        obj.getString("status"),
                        obj.getLong("createdAt")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addOrder(Order order) {
        List<Order> orders = getOrders();
        orders.add(0, order);
        try {
            JSONArray arr = new JSONArray();
            for (Order o : orders) {
                JSONObject obj = new JSONObject();
                obj.put("id", o.id);
                obj.put("userName", o.userName);
                obj.put("userPhone", o.userPhone);
                obj.put("coffeeType", o.coffeeType);
                obj.put("quantity", o.quantity);
                JSONArray bitesArr = new JSONArray();
                for (String b : o.bites) bitesArr.put(b);
                obj.put("bites", bitesArr);
                obj.put("total", o.total);
                obj.put("status", o.status);
                obj.put("createdAt", o.createdAt);
                arr.put(obj);
            }
            prefs.edit().putString("orders", arr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrdersForUser(String phone) {
        List<Order> result = new ArrayList<>();
        for (Order o : getOrders()) {
            if (o.userPhone.equals(phone)) result.add(o);
        }
        return result;
    }

    // ===== Models =====
    public static class User {
        public final String name, phone, password;
        public User(String name, String phone, String password) {
            this.name = name;
            this.phone = phone;
            this.password = password;
        }
    }

    public static class Order {
        public final String id, userName, userPhone, coffeeType, status;
        public final int quantity, total;
        public final List<String> bites;
        public final long createdAt;

        public Order(String id, String userName, String userPhone, String coffeeType,
                     int quantity, List<String> bites, int total, String status, long createdAt) {
            this.id = id;
            this.userName = userName;
            this.userPhone = userPhone;
            this.coffeeType = coffeeType;
            this.quantity = quantity;
            this.bites = bites;
            this.total = total;
            this.status = status;
            this.createdAt = createdAt;
        }
    }
}