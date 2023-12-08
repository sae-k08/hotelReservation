package com.example.hotel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
     public Page<Hotel> findByNameLike(String keyword, Pageable pageable);
     
     public Page<Hotel> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
     public Page<Hotel> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
     public Page<Hotel> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
     public Page<Hotel> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
     public Page<Hotel> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
     public Page<Hotel> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable); 
     public Page<Hotel> findAllByOrderByCreatedAtDesc(Pageable pageable);
     public Page<Hotel> findAllByOrderByPriceAsc(Pageable pageable);
     
     public List<Hotel> findTop10ByOrderByCreatedAtDesc();
}  
