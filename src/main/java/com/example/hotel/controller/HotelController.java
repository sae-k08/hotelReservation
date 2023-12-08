package com.example.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hotel.entity.Hotel;
import com.example.hotel.form.ReservationInputForm;
import com.example.hotel.repository.HotelRepository;

@Controller
@RequestMapping("/hotels")
public class HotelController {
     private final HotelRepository hotelRepository;
     
     public HotelController(HotelRepository hotelRepository) {
    	 this.hotelRepository = hotelRepository;
     }
     
     @GetMapping
     public String index(@RequestParam(name = "keyword", required = false) String keyword,
    		             @RequestParam(name = "area", required = false) String area,
    		             @RequestParam(name = "price", required = false) Integer price,
    		             @RequestParam(name = "order", required = false) String order,
    		             @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    		             Model model)
     {
    	 Page<Hotel> hotelPage;
    	 
    	 if (keyword != null && !keyword.isEmpty()) {
    		 if (order != null && order.equals("priceAsc")) {
                 hotelPage = hotelRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
             } else {
                 hotelPage = hotelRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
             }
    	 } else if (area != null && !area.isEmpty()) {
    		 if (order != null && order.equals("priceAsc")) {
                 hotelPage = hotelRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
             } else {
                 hotelPage = hotelRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
             }
         } else if (price != null) {
        	 if (order != null && order.equals("priceAsc")) {
                 hotelPage = hotelRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
             } else {
                 hotelPage = hotelRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
             }
         } else {
        	 if (order != null && order.equals("priceAsc")) {
                 hotelPage = hotelRepository.findAllByOrderByPriceAsc(pageable);
             } else {
                 hotelPage = hotelRepository.findAllByOrderByCreatedAtDesc(pageable);   
             }
         }
    	 
    	 model.addAttribute("hotelPage", hotelPage);
         model.addAttribute("keyword", keyword);
         model.addAttribute("area", area);
         model.addAttribute("price", price); 
         model.addAttribute("order", order);
         
         return "hotels/index";
     }
     
     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         Hotel hotel = hotelRepository.getReferenceById(id);
         
         model.addAttribute("hotel", hotel); 
         model.addAttribute("reservationInputForm", new ReservationInputForm());
         
         return "hotels/show";
     }
}
