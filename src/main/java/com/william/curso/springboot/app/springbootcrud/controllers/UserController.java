package com.william.curso.springboot.app.springbootcrud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.william.curso.springboot.app.springbootcrud.entities.User;
import com.william.curso.springboot.app.springbootcrud.services.UserService;

import jakarta.validation.Valid;

//@CrossOrigin(origins="http://localhost:4200", originPatterns="*")   //v215  etiqueta para compartir información con una aplicación externa
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> list(){
        return service.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")   //reemplaza hasRole de SpringSecurityConfig
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    @PostMapping("/register")   //v200
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        user.setAdmin(false);
        return create(user, result);
    }

    public ResponseEntity<?> validation(BindingResult result){

        Map<String, String> errors = new HashMap<>();
        
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

    }
}
