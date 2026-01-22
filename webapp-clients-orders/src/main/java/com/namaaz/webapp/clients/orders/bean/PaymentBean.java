package com.namaaz.webapp.clients.orders.bean;

import com.namaaz.webapp.clients.orders.client.ClientOrderServiceClient;
import com.namaaz.webapp.clients.orders.dto.OrderDTO;
import com.namaaz.webapp.clients.orders.dto.PaymentDTO;
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
public class PaymentBean implements Serializable {

    @Inject
    private ClientOrderServiceClient clientOrderServiceClient;

    private List<PaymentDTO> payments;
    private List<OrderDTO> orders;
    private PaymentDTO newPayment;
    private boolean showDialog;

    @PostConstruct
    public void init() {
        loadOrders();
        loadPayments();
        newPayment = new PaymentDTO();
        newPayment.setStatus("PENDING");
        newPayment.setPaymentDateTime(OffsetDateTime.now());
    }

    public void loadOrders() {
        orders = clientOrderServiceClient.getAllOrders();
    }

    public void loadPayments() {
        payments = clientOrderServiceClient.getAllPayments();
    }

    public void prepareNewPayment() {
        newPayment = new PaymentDTO();
        newPayment.setStatus("PENDING");
        newPayment.setPaymentDateTime(OffsetDateTime.now());
        showDialog = true;
    }

    public void preparePaymentForOrder(OrderDTO order) {
        newPayment = new PaymentDTO();
        newPayment.setOrderId(order.getId());
        newPayment.setAmount(order.getTotalAmount());
        newPayment.setStatus("PENDING");
        newPayment.setPaymentDateTime(OffsetDateTime.now());
        showDialog = true;
    }

    public void savePayment() {
        try {
            PaymentDTO created = clientOrderServiceClient.createPayment(newPayment);
            if (created != null) {
                addMessage("Succès", "Paiement enregistré avec succès", FacesMessage.SEVERITY_INFO);
                loadPayments();
                showDialog = false;
            } else {
                addMessage("Erreur", "Impossible d'enregistrer le paiement", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newPayment = new PaymentDTO();
        newPayment.setStatus("PENDING");
        newPayment.setPaymentDateTime(OffsetDateTime.now());
    }

    public List<SelectItem> getOrderSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", "-- Sélectionner une commande --"));
        for (OrderDTO order : orders) {
            items.add(new SelectItem(order.getId(), 
                "Commande #" + order.getId().substring(0, 8) + " - " + order.getTotalAmount() + "€"));
        }
        return items;
    }

    public List<SelectItem> getPaymentMethodSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("CASH", "Espèces"));
        items.add(new SelectItem("CARD", "Carte bancaire"));
        items.add(new SelectItem("ONLINE", "Paiement en ligne"));
        return items;
    }

    public List<SelectItem> getPaymentStatusSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("PENDING", "En attente"));
        items.add(new SelectItem("OK", "Validé"));
        items.add(new SelectItem("FAILED", "Échoué"));
        return items;
    }

    public String getPaymentMethodLabel(String method) {
        switch (method) {
            case "CASH": return "Espèces";
            case "CARD": return "Carte bancaire";
            case "ONLINE": return "Paiement en ligne";
            default: return method;
        }
    }

    public String getPaymentStatusLabel(String status) {
        switch (status) {
            case "PENDING": return "En attente";
            case "OK": return "Validé";
            case "FAILED": return "Échoué";
            default: return status;
        }
    }

    public String getPaymentStatusBadgeClass(String status) {
        switch (status) {
            case "PENDING": return "badge-warning";
            case "OK": return "badge-success";
            case "FAILED": return "badge-danger";
            default: return "badge-secondary";
        }
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public PaymentDTO getNewPayment() {
        return newPayment;
    }

    public void setNewPayment(PaymentDTO newPayment) {
        this.newPayment = newPayment;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }
}
