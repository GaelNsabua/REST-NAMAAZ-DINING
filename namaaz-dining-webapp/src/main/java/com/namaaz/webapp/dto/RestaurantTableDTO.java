package com.namaaz.webapp.dto;

import java.io.Serializable;

public class RestaurantTableDTO implements Serializable {
    private String id;
    private Integer tableNumber;
    private Integer seats;
    private String location;
    private String status;
    
    public RestaurantTableDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }
    
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
