package com.namaaz.service.reservations.business;

import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.entities.ReservationStatus;
import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.entities.TableStatus;
import com.namaaz.service.reservations.repository.ReservationRepository;
import com.namaaz.service.reservations.repository.RestaurantTableRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class ReservationService {
    
    @Inject
    private ReservationRepository reservationRepository;
    
    @Inject
    private RestaurantTableRepository tableRepository;
    
    // === TABLE OPERATIONS ===
    
    @Transactional
    public RestaurantTable createTable(RestaurantTable table) {
        return tableRepository.save(table);
    }
    
    public Optional<RestaurantTable> getTableById(UUID id) {
        return tableRepository.findById(id);
    }
    
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }
    
    public List<RestaurantTable> getTablesByStatus(TableStatus status) {
        return tableRepository.findByStatus(status);
    }
    
    public List<RestaurantTable> getAvailableTables() {
        return tableRepository.findByStatus(TableStatus.FREE);
    }
    
    @Transactional
    public Optional<RestaurantTable> updateTable(UUID id, RestaurantTable updatedTable) {
        Optional<RestaurantTable> existing = tableRepository.findById(id);
        if (existing.isPresent()) {
            RestaurantTable table = existing.get();
            table.setTableNumber(updatedTable.getTableNumber());
            table.setSeats(updatedTable.getSeats());
            table.setLocation(updatedTable.getLocation());
            table.setStatus(updatedTable.getStatus());
            return Optional.of(tableRepository.save(table));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteTable(UUID id) {
        Optional<RestaurantTable> table = tableRepository.findById(id);
        if (table.isPresent()) {
            tableRepository.delete(table.get());
            return true;
        }
        return false;
    }
    
    // === RESERVATION OPERATIONS ===
    
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
    
    public Optional<Reservation> getReservationById(UUID id) {
        return reservationRepository.findById(id);
    }
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    public List<Reservation> getReservationsByClientId(UUID clientId) {
        return reservationRepository.findByClientId(clientId);
    }
    
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
    
    public List<Reservation> getUpcomingReservations() {
        return reservationRepository.findUpcoming();
    }
    
    public List<Reservation> getReservationsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return reservationRepository.findByDateRange(startDate, endDate);
    }
    
    @Transactional
    public Optional<Reservation> updateReservation(UUID id, Reservation updatedReservation) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setClientId(updatedReservation.getClientId());
            reservation.setNumPeople(updatedReservation.getNumPeople());
            reservation.setStartTime(updatedReservation.getStartTime());
            reservation.setEndTime(updatedReservation.getEndTime());
            reservation.setStatus(updatedReservation.getStatus());
            reservation.setNotes(updatedReservation.getNotes());
            reservation.setTables(updatedReservation.getTables());
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public Optional<Reservation> confirmReservation(UUID id) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setStatus(ReservationStatus.CONFIRMED);
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public Optional<Reservation> cancelReservation(UUID id) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setStatus(ReservationStatus.CANCELLED);
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteReservation(UUID id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            reservationRepository.delete(reservation.get());
            return true;
        }
        return false;
    }
}
