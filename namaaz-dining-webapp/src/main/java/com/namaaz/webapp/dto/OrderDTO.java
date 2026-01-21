package com.namaaz.webapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO implements Serializable {
    private String id;
    private String clientId;
    private String reservationId;
    private String tableId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items = new ArrayList<>();
    
    public OrderDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    
    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
