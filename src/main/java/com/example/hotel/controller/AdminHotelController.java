package com.example.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotel.entity.Hotel;
import com.example.hotel.form.HotelEditForm;
import com.example.hotel.form.HotelRegisterForm;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.service.HotelService;

@Controller
@RequestMapping("/admin/hotels")
public class AdminHotelController {
     private final HotelRepository hotelRepository;
     private final HotelService hotelService;
     
     public AdminHotelController(HotelRepository hotelRepository, HotelService hotelService) {
    	 this.hotelRepository = hotelRepository;
    	 this.hotelService = hotelService;
     }
     
     @GetMapping
     public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword) {
    	 Page<Hotel> hotelPage;
    	 
    	 if (keyword != null && !keyword.isEmpty()) {
             hotelPage = hotelRepository.findByNameLike("%" + keyword + "%", pageable);                
         } else {
             hotelPage = hotelRepository.findAll(pageable);
         }
    	 
    	 model.addAttribute("hotelPage", hotelPage);
    	 model.addAttribute("keyword", keyword);
    	 
    	 return "admin/hotels/index";
     }
     
     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
    	 Hotel  hotel = hotelRepository.getReferenceById(id);
    	 
    	 model.addAttribute("hotel", hotel);
    	 
    	 return "admin/hotels/show";
     }
     
     @GetMapping("/register")
     public String register(Model model) {
    	 model.addAttribute("hotelRegisterForm", new HotelRegisterForm());
    	 return "admin/hotels/register";
     }
     
     @PostMapping("/create")
     public String create(@ModelAttribute @Validated HotelRegisterForm hotelRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    	 if (bindingResult.hasErrors()) {
    		 return "admin/hotels/register";
    	 }
    	 
    	 hotelService.create(hotelRegisterForm);
    	 redirectAttributes.addFlashAttribute("successMessage", "ホテルを登録しました。");
    	 
    	 return "redirect:/admin/hotels";
     }
     
     @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
         Hotel hotel = hotelRepository.getReferenceById(id);
         String imageName = hotel.getImageName();
         HotelEditForm hotelEditForm = new HotelEditForm(hotel.getId(), hotel.getName(), null, hotel.getDescription(), hotel.getPrice(), hotel.getCapacity(), hotel.getPostalCode(), hotel.getAddress(), hotel.getPhoneNumber());
         
         model.addAttribute("imageName", imageName);
         model.addAttribute("hotelEditForm", hotelEditForm);
         
         return "admin/hotels/edit";
     }
     
     @PostMapping("/{id}/update")
     public String update(@ModelAttribute @Validated HotelEditForm hotelEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/hotels/edit";
         }
         
         hotelService.update(hotelEditForm);
         redirectAttributes.addFlashAttribute("successMessage", "ホテル情報を編集しました。");
         
         return "redirect:/admin/hotels";
     }
     
     @PostMapping("/{id}/delete")
     public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
         hotelRepository.deleteById(id);
                 
         redirectAttributes.addFlashAttribute("successMessage", "ホテルを削除しました。");
         
         return "redirect:/admin/hotels";
     }
}
