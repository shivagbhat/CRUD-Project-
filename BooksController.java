package com.firrstproject.Book_Bazaar.controllers;

import java.nio.file.Files;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.firrstproject.Book_Bazaar.models.BookDto;
import com.firrstproject.Book_Bazaar.models.Books;
import com.firrstproject.Book_Bazaar.services.BooksRepository;

import jakarta.validation.Valid; 
@Controller
@RequestMapping("/books")
public class BooksController {

	  @Autowired
	  private BooksRepository repo;
	  
	  @GetMapping({"", "/"})
	  public String showBookList(Model model)
	  {
		  List<Books> books = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		  model.addAttribute("books", books);
		  return "books/index";
	  }
	  
	  @GetMapping("/create")
	  public String showCreatePage(Model model)
	  {
		  BookDto bookDto = new BookDto();
		  model.addAttribute("bookDto", bookDto);
		  return "books/createBook";
	  } 
	  
	  @PostMapping("/create")
	  public String createBook(@Valid @ModelAttribute BookDto bookDto, BindingResult result)
	  {
		  if(bookDto.getImageFile().isEmpty()) {
			  result.addError(new FieldError("bookDto", "imageFile", "The image file is required"));
		  }
		  
		  if(result.hasErrors()) {
			  return "books/createBook";
		  }
		  
		  MultipartFile image=bookDto.getImageFile();
		  Date createdAt =new Date();
		  String storageFileName=createdAt.getTime() + "_" + image.getOriginalFilename();
		  
		  try {
			   String uploadDir="public/images/";
			   Path uploadPath = Paths.get(uploadDir);
			   
			   if  (!Files.exists(uploadPath)) {
				   Files.createDirectories(uploadPath);
			   }
			   try (InputStream inputStream = image.getInputStream()){
				   Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				   
			   }
		  } catch(Exception ex)
		  {
			  System.out.println("Exception: "+ ex.getMessage());
		  } 
		  
		  Books book = new Books();
		  book.setName(bookDto.getName());
		  book.setAuthor(bookDto.getAuthor());
		  book.setCategory(bookDto.getCategory());
		  book.setPrice(bookDto.getPrice());
		  book.setLanguages(bookDto.getLanguages());
		  book.setCreatedAt(createdAt);
		  book.setImageFileName(storageFileName);
		  
		  repo.save(book);
		  
		  return "redirect:/books";  
	  } 
	  
	  @GetMapping("/edit")
	  public String showEditPage(Model model, @RequestParam int id)
	  {
		  
		  try {
			  Books book=repo.findById(id).get();
			  model.addAttribute("book", book);
			  
			  BookDto bookDto = new BookDto();
			  bookDto.setName(book.getName());
			  bookDto.setAuthor(book.getAuthor());
			  bookDto.setCategory(book.getCategory());
			  bookDto.setPrice(book.getPrice());
			  bookDto.setLanguages(book.getLanguages());
			 
			  model.addAttribute("bookDto", bookDto);
			  
			  
			  
		  }
		  catch(Exception ex) {
			  System.out.println("Exception:"+ex.getMessage());
			  return "redirct/books";
		  }
		  return "books/editBook";
	  }
	  
	  @PostMapping("/edit")
	  public String updateBook(Model model, @RequestParam int id, @Valid @ModelAttribute BookDto bookDto, BindingResult result){
		 
		  try {
			  Books book=repo.findById(id).get();
			  model.addAttribute("book", book);
			  
			  if (result.hasErrors()) {
				  return "books/editBook";
			  }
			  
			  if(!bookDto.getImageFile().isEmpty()) {
				  
				  String uploadDir ="public/images";
				  Path oldImagePath = Paths.get(uploadDir + book.getImageFileName());
				  try {
					  Files.delete(oldImagePath);
				  }
				  catch(Exception ex) {
					  System.out.println("Exception:" + ex.getMessage());
				  }
		  
		  MultipartFile image=bookDto.getImageFile();
		  Date createdAt =new Date();
		  String storageFileName=createdAt.getTime() + "_" + image.getOriginalFilename();
		  
		     try (InputStream inputStream = image.getInputStream()){
		
				   Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				   
			   }
		  book.setImageFileName(storageFileName);
		  }
			  
			  book.setName(bookDto.getName());
			  book.setAuthor(bookDto.getAuthor());
			  book.setCategory(bookDto.getCategory());
			  book.setPrice(bookDto.getPrice());
			  book.setLanguages(bookDto.getLanguages());
		  
		  repo.save(book);
		  }
		  
		  catch(Exception ex) {
			  System.out.println("Exception:" +ex.getMessage());
		  }
		  return "redirect:/books";
		  }
	  
	  
	  @GetMapping("/delete")
	  public String deleteBook(
			  @RequestParam int id
			    
			  
			  )
	  {
	          try
	         {
		
	  
		  			Books book =repo.findById(id).get();
		  			
		  			Path imagePath = Paths.get("public/images/" + book.getImageFileName());
		  			try {
		  				Files.delete(imagePath);
		  			}
		  			catch(Exception ex) {
		  				System.out.println("Exception:" +ex.getMessage());
		  
		  			}
		  			
		  			repo.delete(book);
	  }
		  
		  catch (Exception ex) {
			  System.out.println("Exception:" + ex.getMessage());
		  }
		  return "redirect:/books";
	  }
	 
	  
}
