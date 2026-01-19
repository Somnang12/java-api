package com.example.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Product;

public interface ProductRepo extends JpaRepository<Product, Integer>{

	
	
}
