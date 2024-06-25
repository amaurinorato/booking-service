package com.hostfully.bookingservice.repository;

import com.hostfully.bookingservice.domain.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query(
      "SELECT b FROM Booking b WHERE b.property.id = :propertyId AND "
          + "(b.start <= :endDate AND b.end >= :startDate)"
          + " AND b.canceled = false")
  List<Booking> findActiveOverlappingBookings(
      @Param("propertyId") Long propertyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  Optional<Booking> findActiveBookingByIdAndCanceledIsFalse(Long bookingId);
}
