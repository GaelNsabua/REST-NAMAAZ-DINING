package com.namaaz.webapp.clients.orders.bean;

import com.namaaz.webapp.clients.orders.client.ClientOrderServiceClient;
import com.namaaz.webapp.clients.orders.client.MenuServiceClient;
import com.namaaz.webapp.clients.orders.dto.ClientDTO;
import com.namaaz.webapp.clients.orders.dto.MenuItemDTO;
import com.namaaz.webapp.clients.orders.dto.OrderDTO;
import com.namaaz.webapp.clients.orders.dto.OrderItemDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class OrderBean implements Serializable {

    @Inject
    private ClientOrderServiceClient clientOrderServiceClient;

    @Inject
    private MenuServiceClient menuServiceClient;

    private List<OrderDTO> orders;
    private List<ClientDTO> clients;
    private List<MenuItemDTO> menuItems;
    private OrderDTO newOrder;
    private boolean showDialog;
    private String filterStatus;

    // For order items
    private String selectedMenuItemId;
    private Integer itemQuantity = 1;

    @PostConstruct
    public void init() {
        loadClients();
        loadMenuItems();
        loadOrders();
        initializeNewOrder();
    }

    private void initializeNewOrder() {
        newOrder = new OrderDTO();
        newOrder.setStatus("NEW");
        newOrder.setOrderDateTime(OffsetDateTime.now());
        newOrder.setTotalAmount(BigDecimal.ZERO);
        newOrder.setItems(new ArrayList<>());
    }

    public void loadClients() {
        clients = clientOrderServiceClient.getAllClients();
    }

    public void loadMenuItems() {
        menuItems = menuServiceClient.getAvailableMenuItems();
    }

    public void loadOrders() {
        if (filterStatus != null && !filterStatus.isEmpty() && !"ALL".equals(filterStatus)) {
            orders = clientOrderServiceClient.getOrdersByStatus(filterStatus);
        } else {
            orders = clientOrderServiceClient.getAllOrders();
        }
        
        // Enrich with client names
        for (OrderDTO order : orders) {
            if (order.getClientId() != null) {
                ClientDTO client = clients.stream()
                        .filter(c -> c.getId().equals(order.getClientId()))
                        .findFirst()
                        .orElse(null);
                if (client != null) {
                    order.setClientName(client.getFullName());
                }
            }
        }
    }

    public void applyFilter() {
        loadOrders();
    }

    public void clearFilter() {
        filterStatus = null;
        loadOrders();
    }

    public void prepareNewOrder() {
        initializeNewOrder();
        showDialog = true;
    }

    public void addItemToOrder() {
        if (selectedMenuItemId == null || itemQuantity == null || itemQuantity <= 0) {
            addMessage("Erreur", "Sélectionnez un plat et une quantité valide", FacesMessage.SEVERITY_ERROR);
            return;
        }

        MenuItemDTO menuItem = menuItems.stream()
                .filter(m -> m.getId().equals(selectedMenuItemId))
                .findFirst()
                .orElse(null);

        if (menuItem != null) {
            OrderItemDTO orderItem = new OrderItemDTO();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setQuantity(itemQuantity);
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice().multiply(BigDecimal.valueOf(itemQuantity)));

            newOrder.getItems().add(orderItem);
            calculateTotal();

            // Reset
            selectedMenuItemId = null;
            itemQuantity = 1;
        }
    }

    public void removeItemFromOrder(OrderItemDTO item) {
        newOrder.getItems().remove(item);
        calculateTotal();
    }

    private void calculateTotal() {
        BigDecimal total = newOrder.getItems().stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotalAmount(total);
    }

    public void saveOrder() {
        try {
            if (newOrder.getItems().isEmpty()) {
                addMessage("Erreur", "Ajoutez au moins un article à la commande", FacesMessage.SEVERITY_ERROR);
                return;
            }

            OrderDTO created = clientOrderServiceClient.createOrder(newOrder);
            if (created != null) {
                addMessage("Succès", "Commande créée avec succès", FacesMessage.SEVERITY_INFO);
                loadOrders();
                showDialog = false;
            } else {
                addMessage("Erreur", "Impossible de créer la commande", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void completeOrder(String id) {
        try {
            boolean completed = clientOrderServiceClient.completeOrder(id);
            if (completed) {
                addMessage("Succès", "Commande terminée", FacesMessage.SEVERITY_INFO);
                loadOrders();
            } else {
                addMessage("Erreur", "Impossible de terminer la commande", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteOrder(String id) {
        try {
            boolean deleted = clientOrderServiceClient.deleteOrder(id);
            if (deleted) {
                addMessage("Succès", "Commande supprimée avec succès", FacesMessage.SEVERITY_INFO);
                loadOrders();
            } else {
                addMessage("Erreur", "Impossible de supprimer la commande", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        initializeNewOrder();
    }

    public List<SelectItem> getClientSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", "-- Sélectionner un client --"));
        for (ClientDTO client : clients) {
            items.add(new SelectItem(client.getId(), client.getFullName()));
        }
        return items;
    }

    public List<SelectItem> getMenuItemSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", "-- Sélectionner un plat --"));
        for (MenuItemDTO item : menuItems) {
            items.add(new SelectItem(item.getId(), item.getName() + " - " + item.getPrice() + "€"));
        }
        return items;
    }

    public List<SelectItem> getStatusFilterItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ALL", "Tous les statuts"));
        items.add(new SelectItem("NEW", "Nouvelles"));
        items.add(new SelectItem("IN_PROGRESS", "En cours"));
        items.add(new SelectItem("COMPLETED", "Terminées"));
        items.add(new SelectItem("CANCELLED", "Annulées"));
        return items;
    }

    public String getStatusLabel(String status) {
        switch (status) {
            case "NEW": return "Nouvelle";
            case "IN_PROGRESS": return "En cours";
            case "COMPLETED": return "Terminée";
            case "CANCELLED": return "Annulée";
            default: return status;
        }
    }

    public String getStatusBadgeClass(String status) {
        switch (status) {
            case "NEW": return "badge-info";
            case "IN_PROGRESS": return "badge-warning";
            case "COMPLETED": return "badge-success";
            case "CANCELLED": return "badge-danger";
            default: return "badge-secondary";
        }
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<OrderDTO> getOrders() {
        return orders;
    }

    public OrderDTO getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(OrderDTO newOrder) {
        this.newOrder = newOrder;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }

    public String getSelectedMenuItemId() {
        return selectedMenuItemId;
    }

    public void setSelectedMenuItemId(String selectedMenuItemId) {
        this.selectedMenuItemId = selectedMenuItemId;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
