package com.example.hotel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.hotel.entity.Hotel;
import com.example.hotel.form.HotelEditForm;
import com.example.hotel.form.HotelRegisterForm;
import com.example.hotel.repository.HotelRepository;

@Service
public class HotelService {
     private final HotelRepository hotelRepository;
     
     public HotelService(HotelRepository hotelRepository) {
    	 this.hotelRepository = hotelRepository;
     }
     
     @Transactional
     public void create(HotelRegisterForm hotelRegisterForm) {
    	 Hotel hotel = new Hotel();
    	 MultipartFile imageFile = hotelRegisterForm.getImageFile();
    	 
    	 if (!imageFile.isEmpty()) {
    		 String imageName = imageFile.getOriginalFilename();
    		 String hashedImageName = generateNewFileName(imageName);
    		 Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
    		 copyImageFile(imageFile, filePath);
    		 hotel.setImageName(hashedImageName);
    	 }
    	 
    	 hotel.setName(hotelRegisterForm.getName());
    	 hotel.setDescription(hotelRegisterForm.getDescription());
    	 hotel.setPrice(hotelRegisterForm.getPrice());
    	 hotel.setCapacity(hotelRegisterForm.getCapacity());
    	 hotel.setPostalCode(hotelRegisterForm.getPostalCode());
    	 hotel.setAddress(hotelRegisterForm.getAddress());
    	 hotel.setPhoneNumber(hotelRegisterForm.getPhoneNumber());
    	 
    	 hotelRepository.save(hotel);
     }
     
     @Transactional
     public void update(HotelEditForm hotelEditForm) {
         Hotel hotel = hotelRepository.getReferenceById(hotelEditForm.getId());
         MultipartFile imageFile = hotelEditForm.getImageFile();
         
         if (!imageFile.isEmpty()) {
             String imageName = imageFile.getOriginalFilename(); 
             String hashedImageName = generateNewFileName(imageName);
             Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
             copyImageFile(imageFile, filePath);
             hotel.setImageName(hashedImageName);
         }
         
         hotel.setName(hotelEditForm.getName());                
         hotel.setDescription(hotelEditForm.getDescription());
         hotel.setPrice(hotelEditForm.getPrice());
         hotel.setCapacity(hotelEditForm.getCapacity());
         hotel.setPostalCode(hotelEditForm.getPostalCode());
         hotel.setAddress(hotelEditForm.getAddress());
         hotel.setPhoneNumber(hotelEditForm.getPhoneNumber());
                     
         hotelRepository.save(hotel);
     }
     
     //UUIDを使って生成したファイル名を返す
     public String generateNewFileName(String fileName) {
         String[] fileNames = fileName.split("\\.");                
         for (int i = 0; i < fileNames.length - 1; i++) {
             fileNames[i] = UUID.randomUUID().toString();            
         }
         String hashedFileName = String.join(".", fileNames);
         return hashedFileName;
     }     
     
     // 画像ファイルを指定したファイルにコピーする
     public void copyImageFile(MultipartFile imageFile, Path filePath) {           
         try {
             Files.copy(imageFile.getInputStream(), filePath);
         } catch (IOException e) {
             e.printStackTrace();
         }          
     } 
}
