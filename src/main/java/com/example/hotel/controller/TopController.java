package com.example.hotel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hotel.entity.Hotel;
import com.example.hotel.repository.HotelRepository;

@Controller
public class TopController {
	private final HotelRepository hotelRepository;
	
	public TopController(HotelRepository hotelRepository) {
		this.hotelRepository = hotelRepository;
	}
	
     @GetMapping("/")
     public String index(Model model) {
    	 List<Hotel> newHotels = hotelRepository.findTop10ByOrderByCreatedAtDesc();
    	 model.addAttribute("newHotels", newHotels);
    	 
    	 return "index";
     }
}
