package com.namaaz.webapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CategoryDTO implements Serializable {
    private String id;
    private String name;
    private String description;
    
    public CategoryDTO() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
