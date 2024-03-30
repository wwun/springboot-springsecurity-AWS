package com.william.curso.springboot.app.springbootcrud.entities;

import com.william.curso.springboot.app.springbootcrud.validation.IsExistsDb;
import com.william.curso.springboot.app.springbootcrud.validation.IsRequired;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //@NotBlank(message="{NotBlank.product.name}")   //etiqueta de Validation, para String   //puede ser NotEmpty
    @IsRequired(message="{IsRequired.product.name}")   //anotación de clase personalizada
    @Size(min=3, max=20)    //etiqueta de Validación, número de caracteres
    private String name;
    
    @Min(value=500, message="{Min.product.price}")
    @NotNull(message="{NotNull.product.price}")    //de cualquier otro tipo de dato
    private Integer price;

    @NotBlank(message="{NotBlank.product.description}")
    private String description;

    @IsRequired
    @IsExistsDb
    private String sku;

    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
