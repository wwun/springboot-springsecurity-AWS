package com.william.curso.springboot.app.springbootcrud;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.william.curso.springboot.app.springbootcrud.entities.Product;

@Component
public class ProductValidation implements Validator{    //si se quiere usar una clase personalizada de los errores, esta clase se usa en el controller. Product tiene otra validación

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product)target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", null, "es requerido");    //no se está tomando desde message.properties
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotBlank.product.description");
        if(product.getDescription() == null || product.getDescription().isBlank()){
            errors.rejectValue("description", null, "es requerido, por favor");
        }
        if(product.getPrice() == null){
            errors.rejectValue("price", null, "no puede ser nulo, ok");
        }else if(product.getPrice()<500){
            errors.rejectValue("price", null, "debe ser un valor numérico igual o mayor a 500");
        }
    }

}
