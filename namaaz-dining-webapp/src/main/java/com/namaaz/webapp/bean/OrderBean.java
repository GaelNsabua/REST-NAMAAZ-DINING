package com.namaaz.webapp.bean;

import com.namaaz.webapp.client.MenuClient;
import com.namaaz.webapp.client.OrderClient;
import com.namaaz.webapp.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class OrderBean implements Serializable {
    
    @Inject
    private OrderClient orderClient;
    
    @Inject
    private MenuClient menuClient;
    
    private List<OrderDTO> orders;
    private List<ClientDTO> clients;
    private List<MenuItemDTO> menuItems;
    private OrderDTO newOrder;
    private OrderItemDTO newOrderItem;
    
    @PostConstruct
    public void init() {
        loadOrders();
        loadClients();
        loadMenuItems();
        initNewOrder();
    }
    
    private void initNewOrder() {
        newOrder = new OrderDTO();
        newOrder.setStatus("NEW");
        newOrder.setItems(new ArrayList<>());
        newOrderItem = new OrderItemDTO();
    }
    
    public void loadOrders() {
        try {
            orders = orderClient.getAllOrders();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les commandes");
        }
    }
    
    public void loadClients() {
        try {
            clients = orderClient.getAllClients();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les clients");
        }
    }
    
    public void loadMenuItems() {
        try {
            menuItems = menuClient.getAllMenuItems();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger le menu");
        }
    }
    
    public void addItemToOrder() {
        if (newOrderItem.getMenuItemId() != null && newOrderItem.getQuantity() != null && newOrderItem.getQuantity() > 0) {
            // Find menu item to get price
            MenuItemDTO menuItem = menuItems.stream()
                .filter(m -> m.getId().equals(newOrderItem.getMenuItemId()))
                .findFirst()
                .orElse(null);
                
            if (menuItem != null) {
                newOrderItem.setUnitPrice(menuItem.getPrice());
                newOrderItem.setTotalPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(newOrderItem.getQuantity())));
                newOrder.getItems().add(newOrderItem);
                calculateTotal();
                newOrderItem = new OrderItemDTO();
                addMessage(FacesMessage.SEVERITY_INFO, "OK", "Article ajouté");
            }
        }
    }
    
    public void removeItemFromOrder(OrderItemDTO item) {
        newOrder.getItems().remove(item);
        calculateTotal();
    }
    
    private void calculateTotal() {
        BigDecimal total = newOrder.getItems().stream()
            .map(OrderItemDTO::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotalAmount(total);
    }
    
    public void createOrder() {
        try {
            if (newOrder.getItems().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Attention", "Ajoutez au moins un article");
                return;
            }
            orderClient.createOrder(newOrder);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Commande créée avec succès");
            initNewOrder();
            loadOrders();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer la commande");
        }
    }
    
    public String getMenuItemName(String menuItemId) {
        return menuItems.stream()
            .filter(m -> m.getId().equals(menuItemId))
            .map(MenuItemDTO::getName)
            .findFirst()
            .orElse("N/A");
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    public String getStatusBadgeClass(String status) {
        switch (status) {
            case "NEW": return "bg-blue-500";
            case "IN_PROGRESS": return "bg-yellow-500";
            case "COMPLETED": return "bg-green-500";
            case "CANCELLED": return "bg-red-600";
            default: return "bg-gray-400";
        }
    }
    
    // Getters and Setters
    public List<OrderDTO> getOrders() { return orders; }
    public void setOrders(List<OrderDTO> orders) { this.orders = orders; }
    
    public List<ClientDTO> getClients() { return clients; }
    public void setClients(List<ClientDTO> clients) { this.clients = clients; }
    
    public List<MenuItemDTO> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItemDTO> menuItems) { this.menuItems = menuItems; }
    
    public OrderDTO getNewOrder() { return newOrder; }
    public void setNewOrder(OrderDTO newOrder) { this.newOrder = newOrder; }
    
    public OrderItemDTO getNewOrderItem() { return newOrderItem; }
    public void setNewOrderItem(OrderItemDTO newOrderItem) { this.newOrderItem = newOrderItem; }
}
