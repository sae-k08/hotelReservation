package com.example.hotel.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotel.entity.Hotel;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.User;
import com.example.hotel.form.ReservationInputForm;
import com.example.hotel.form.ReservationRegisterForm;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.security.UserDetailsImpl;
import com.example.hotel.service.ReservationService;
import com.example.hotel.service.StripeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;  
	private final HotelRepository hotelRepository;
	private final ReservationService reservationService;
	private final StripeService stripeService;

    
    public ReservationController(ReservationRepository reservationRepository, HotelRepository hotelRepository, ReservationService reservationService, StripeService stripeService) {        
        this.reservationRepository = reservationRepository; 
        this.hotelRepository = hotelRepository;
        this.reservationService = reservationService;
        this.stripeService = stripeService;
    }    

    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        model.addAttribute("reservationPage", reservationPage);         
        
        return "reservations/index";
    }
    
    @GetMapping("/hotels/{id}/reservations/input")
    public String input(@PathVariable(name = "id") Integer id,
                        @ModelAttribute @Validated ReservationInputForm reservationInputForm,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {   
        Hotel hotel = hotelRepository.getReferenceById(id);
        Integer numberOfPeople = reservationInputForm.getNumberOfPeople();   
        Integer capacity = hotel.getCapacity();
        
        if (numberOfPeople != null) {
            if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "宿泊人数が定員を超えています。");
                bindingResult.addError(fieldError);                
            }            
        }         
        
        if (bindingResult.hasErrors()) {            
            model.addAttribute("hotel", hotel);            
            model.addAttribute("errorMessage", "予約内容に不備があります。"); 
            return "hotels/show";
        }
        
        redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);           
        
        return "redirect:/hotels/{id}/reservations/confirm";
    }
    
    @GetMapping("/hotels/{id}/reservations/confirm")
    public String confirm(@PathVariable(name = "id") Integer id,
                          @ModelAttribute ReservationInputForm reservationInputForm,
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                          HttpServletRequest httpServletRequest,
                          Model model) 
    {        
        Hotel hotel = hotelRepository.getReferenceById(id);
        User user = userDetailsImpl.getUser(); 
                
        //チェックイン日とチェックアウト日を取得する
        LocalDate checkinDate = reservationInputForm.getCheckinDate();
        LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
 
        // 宿泊料金を計算する
        Integer price = hotel.getPrice();        
        Integer amount = reservationService.calculateAmount(checkinDate, checkoutDate, price);
        
        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(hotel.getId(), user.getId(), checkinDate.toString(), checkoutDate.toString(), reservationInputForm.getNumberOfPeople(), amount);
        
        String sessionId = stripeService.createStripeSession(hotel.getName(), reservationRegisterForm, httpServletRequest);
        
        model.addAttribute("hotel", hotel);  
        model.addAttribute("reservationRegisterForm", reservationRegisterForm); 
        model.addAttribute("sessionId", sessionId);
        
        return "reservations/confirm";
    } 
    
    /*
    @PostMapping("/hotels/{id}/reservations/create")
    public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {                
        reservationService.create(reservationRegisterForm);        
        
        return "redirect:/reservations?reserved";
    }
   */
}
