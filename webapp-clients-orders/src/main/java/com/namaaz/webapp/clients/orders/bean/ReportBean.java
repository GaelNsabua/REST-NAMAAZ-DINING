package com.namaaz.webapp.clients.orders.bean;

import com.namaaz.webapp.clients.orders.client.ClientOrderServiceClient;
import com.namaaz.webapp.clients.orders.dto.OrderDTO;
import com.namaaz.webapp.clients.orders.dto.OrderItemDTO;
import com.namaaz.webapp.clients.orders.dto.PaymentDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class ReportBean implements Serializable {

    @Inject
    private ClientOrderServiceClient clientOrderServiceClient;

    private List<OrderDTO> orders;
    private List<PaymentDTO> payments;
    
    // Daily sales
    private LocalDate selectedDate;
    private BigDecimal dailySales;
    private long dailyOrdersCount;
    
    // Dish profitability
    private Map<String, DishStats> dishStatsMap;

    @PostConstruct
    public void init() {
        selectedDate = LocalDate.now();
        loadData();
        calculateDailySales();
        calculateDishProfitability();
    }

    private void loadData() {
        orders = clientOrderServiceClient.getAllOrders();
        payments = clientOrderServiceClient.getAllPayments();
    }

    public void calculateDailySales() {
        List<PaymentDTO> dailyPayments = payments.stream()
                .filter(p -> "OK".equals(p.getStatus()))
                .filter(p -> p.getPaymentDateTime() != null && 
                            p.getPaymentDateTime().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

        dailySales = dailyPayments.stream()
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dailyOrdersCount = orders.stream()
                .filter(o -> o.getOrderDateTime() != null && 
                            o.getOrderDateTime().toLocalDate().equals(selectedDate))
                .count();
    }

    public void calculateDishProfitability() {
        dishStatsMap = new HashMap<>();

        for (OrderDTO order : orders) {
            if (order.getItems() != null) {
                for (OrderItemDTO item : order.getItems()) {
                    String menuItemName = item.getMenuItemName();
                    DishStats stats = dishStatsMap.getOrDefault(menuItemName, new DishStats(menuItemName));
                    
                    stats.totalQuantity += item.getQuantity();
                    stats.totalRevenue = stats.totalRevenue.add(item.getSubtotal());
                    
                    dishStatsMap.put(menuItemName, stats);
                }
            }
        }
    }

    public List<DishStats> getTopDishes() {
        return dishStatsMap.values().stream()
                .sorted((a, b) -> b.totalRevenue.compareTo(a.totalRevenue))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        calculateDailySales();
    }

    public BigDecimal getDailySales() {
        return dailySales;
    }

    public long getDailyOrdersCount() {
        return dailyOrdersCount;
    }

    // Inner class for dish statistics
    public static class DishStats implements Serializable {
        private String dishName;
        private int totalQuantity;
        private BigDecimal totalRevenue;

        public DishStats(String dishName) {
            this.dishName = dishName;
            this.totalQuantity = 0;
            this.totalRevenue = BigDecimal.ZERO;
        }

        public String getDishName() {
            return dishName;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public BigDecimal getAveragePrice() {
            if (totalQuantity == 0) return BigDecimal.ZERO;
            return totalRevenue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
