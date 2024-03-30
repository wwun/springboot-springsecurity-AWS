package com.william.curso.springboot.app.springbootcrud.repositories;

import org.springframework.data.repository.CrudRepository;

import com.william.curso.springboot.app.springbootcrud.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{
    boolean existsBySku(String sku);
}
