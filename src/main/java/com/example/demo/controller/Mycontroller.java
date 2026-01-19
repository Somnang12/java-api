package com.example.demo.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.entities.PostProductDAO;
import com.example.demo.entities.Product;
import com.example.demo.entities.PutProductDAO;
import com.example.demo.repos.ProductRepo;

@RestController
@RequestMapping("/api/product")
public class Mycontroller {

  
	@Autowired
	private ProductRepo productRepo;


	@GetMapping
	private Object GetAll() {
		var products = productRepo.findAll();
		
		if(products.isEmpty())
			return ResponseEntity.status(404).body(Map.of("message", "Product is empty"));
		return productRepo.findAll();
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO postProduct) throws Exception{
		String uploadDir = new File("myApp/static").getAbsolutePath();
		File dir = new File(uploadDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		var file = postProduct.getFile();
		String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
		String filepath = Paths.get(uploadDir, uniqueName).toString();
		
		file.transferTo(new File(filepath));
		
		var product = new Product();
		product.setName(postProduct.getName());
		product.setPrice(postProduct.getPrice());
		product.setQty(postProduct.getQty());
		product.setImageUrl("/static/"+uniqueName);
		
		productRepo.save(product);
		
		return ResponseEntity.status(201).body(product);
	}
	
	
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> upDateProduct(@ModelAttribute PutProductDAO putProduct) 
			throws Exception{
		var product = productRepo.findById(putProduct.getId());
		if(product.isPresent()) {
			var update = product.get();
			
			update.setName(putProduct.getName());
			update.setPrice(putProduct.getPrice());
			update.setQty(putProduct.getQty());
			if(putProduct.getFile()!=null) {
				var file = putProduct.getFile();
				String uploadDir = new File("myApp/static").getAbsolutePath();
				File dir = new File(uploadDir);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
				String filepath = Paths.get(uploadDir, uniqueName).toString();
				
				new File("myApp/"+update.getImageUrl()).delete();
				
				file.transferTo(new File(filepath));
				update.setImageUrl("/static/"+uniqueName);
				
			}
			
			productRepo.save(update);
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "product Update successful", "product", update));
		}
		
		return ResponseEntity.status(404)
				.body(Map.of("message", "Product id"+putProduct.getId()+"not found"));
	}
	
	@GetMapping({"/{id}","/id/{id}"})
	public ResponseEntity<?> getById(@PathVariable("id") Integer id){
		var product = productRepo.findById(id);
		if(product.isPresent()) {


			return ResponseEntity.status(HttpStatus.OK)
					.body(product.get());
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(Map.of("message", "product id = "+id+" not found"));
		
	}
	
	
	@DeleteMapping({"/{id}","/id/{id}"})
	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id){
		var product = productRepo.findById(id);
		if(product.isPresent()) {

			new File("myApp/"+product.get().getImageUrl()).delete();
			
			productRepo.delete(product.get());
			
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Product id = "+id+" has been deleted!"));
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(Map.of("message", "product id = "+id+" not found"));
		
	}
}
