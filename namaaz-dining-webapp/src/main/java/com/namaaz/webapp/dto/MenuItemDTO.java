package com.namaaz.webapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class MenuItemDTO implements Serializable {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryId;
    private Boolean available;
    
    public MenuItemDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
