package com.namaaz.webapp.clients.orders.bean;

import com.namaaz.webapp.clients.orders.client.ClientOrderServiceClient;
import com.namaaz.webapp.clients.orders.dto.ClientDTO;
import com.namaaz.webapp.clients.orders.dto.OrderDTO;
import com.namaaz.webapp.clients.orders.dto.PaymentDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private ClientOrderServiceClient clientOrderServiceClient;

    private long totalClients;
    private long totalOrders;
    private long completedOrders;
    private BigDecimal totalRevenue;

    @PostConstruct
    public void init() {
        loadStatistics();
    }

    public void loadStatistics() {
        List<ClientDTO> clients = clientOrderServiceClient.getAllClients();
        totalClients = clients.size();

        List<OrderDTO> orders = clientOrderServiceClient.getAllOrders();
        totalOrders = orders.size();

        completedOrders = orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .count();

        List<PaymentDTO> payments = clientOrderServiceClient.getAllPayments();
        totalRevenue = payments.stream()
                .filter(p -> "OK".equals(p.getStatus()))
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public long getTotalClients() {
        return totalClients;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}
