package com.example.hotel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.entity.Hotel;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.User;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;  
    private final HotelRepository hotelRepository;  
    private final UserRepository userRepository;  
    
    public ReservationService(ReservationRepository reservationRepository, HotelRepository hotelRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;  
        this.hotelRepository = hotelRepository;  
        this.userRepository = userRepository;  
    }    
    
    @Transactional
    public void create(Map<String, String> paymentIntentObject) {
        Reservation reservation = new Reservation();
        
        Integer hotelId = Integer.valueOf(paymentIntentObject.get("hotelId"));
        Integer userId = Integer.valueOf(paymentIntentObject.get("userId"));
        
        
        Hotel hotel = hotelRepository.getReferenceById(hotelId);
        User user = userRepository.getReferenceById(userId);
        LocalDate checkinDate = LocalDate.parse(paymentIntentObject.get("checkinDate"));
        LocalDate checkoutDate = LocalDate.parse(paymentIntentObject.get("checkoutDate"));
        Integer numberOfPeople = Integer.valueOf(paymentIntentObject.get("numberOfPeople"));        
        Integer amount = Integer.valueOf(paymentIntentObject.get("amount"));
                
        reservation.setHotel(hotel);
        reservation.setUser(user);
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setAmount(amount);
        
        reservationRepository.save(reservation);
    }
    
    // 宿泊人数が定員以下かどうかをチェックする
    public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
        return numberOfPeople <= capacity;
    }
    
    // 宿泊料金を計算する
    public Integer calculateAmount(LocalDate checkinDate, LocalDate checkoutDate, Integer price) {
        long numberOfNights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        int amount = price * (int)numberOfNights;
        return amount;
    }    
}
