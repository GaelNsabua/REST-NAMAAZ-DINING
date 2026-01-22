package com.namaaz.webapp.menu.bean;

import com.namaaz.webapp.menu.client.MenuServiceClient;
import com.namaaz.webapp.menu.dto.CategoryDTO;
import com.namaaz.webapp.menu.dto.MenuItemDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean for Dashboard
 */
@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private MenuServiceClient menuServiceClient;

    private long totalCategories;
    private long totalMenuItems;
    private long availableItems;
    private long unavailableItems;

    @PostConstruct
    public void init() {
        loadStatistics();
    }

    public void loadStatistics() {
        List<CategoryDTO> categories = menuServiceClient.getAllCategories();
        totalCategories = categories.size();

        List<MenuItemDTO> allItems = menuServiceClient.getAllMenuItems();
        totalMenuItems = allItems.size();

        availableItems = allItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .count();

        unavailableItems = totalMenuItems - availableItems;
    }

    // Getters
    public long getTotalCategories() {
        return totalCategories;
    }

    public long getTotalMenuItems() {
        return totalMenuItems;
    }

    public long getAvailableItems() {
        return availableItems;
    }

    public long getUnavailableItems() {
        return unavailableItems;
    }
}
