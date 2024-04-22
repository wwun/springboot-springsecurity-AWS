package com.william.curso.springboot.app.springbootcrud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.william.curso.springboot.app.springbootcrud.ProductValidation;
import com.william.curso.springboot.app.springbootcrud.entities.Product;
import com.william.curso.springboot.app.springbootcrud.services.ProductService;

import jakarta.validation.Valid;

@CrossOrigin(origins="http://localhost:4200", originPatterns="*")   //v215  etiqueta para compartir información con una aplicación externa
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    // @Autowired
    // private ProductValidation validation;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")    //la diferencia con hacerlo en config es que acá no se puede hacer dinámico utilizando un for para diferentes opciones obteniendo las url de una tabla
    @GetMapping
    public List<Product> list(){
        return service.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable Long id){
        Optional<Product> productOptional = service.findById(id);
        if(productOptional.isPresent()){
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Product product, BindingResult result){ //se importa @Valid de validation, configurado en pom, Binding Result encapsula todas las validaciones que se hace en el request    
        //primero se realiza la validación del objeto Product utilizando la anotación @Valid y Si se encuentran errores de validación durante este proceso, Spring los captura y los almacena en el objeto BindingResult
        //validation.validate(product, result); //validando usando la clase personalizada de errores
        if(result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Product product, BindingResult result, @PathVariable Long id){    //BindingResult debe estar a la derecha del valor que se va a validar
        //validation.validate(product, result); //validando usando la clase personalizada de errores
        if(result.hasFieldErrors()){
            return validation(result);
        }
        //return ResponseEntity.status(HttpStatus.CREATED).body(service.update(id, product).get());
        Optional<Product> productOptional = service.update(id, product);
        if(productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<Product> productOptional = service.delete(id);
        if(productOptional.isPresent()){
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo "+ err.getField() + " "+ err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);    //badRequest es lo mismo que colocar status(HttpStatus.BAD_REQUEST)
    }
}