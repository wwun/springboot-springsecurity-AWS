package com.william.curso.springboot.app.springbootcrud.entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Role(String name) {
        this.name = name;
    }

    //esta lista tampoco se haría en la práctica
    @JsonIgnoreProperties({"roles", "handler", "hibernateLazyInitializer"}) //"handler": Este atributo se refiere al manejador utilizado por Hibernate para manejar la carga perezosa (lazy loading) de la entidad. Es un detalle interno de Hibernate y no es relevante para la serialización de la entidad a JSON, por lo que se ignora para evitar problemas de serialización/deserialización    //"hibernateLazyInitializer": Este atributo es específico de Hibernate y se utiliza para realizar la inicialización perezosa de la entidad cuando se accede a ella por primera vez. Al igual que "handler", es un detalle interno de Hibernate y no debe ser serializado a JSON, por lo que se ignora con esta anotación
    @ManyToMany(mappedBy = "roles") //relación inversa  //la tabla intermedia que representa la relación muchos a muchos debe tener una columna llamada "roles" que actúa como clave externa para referenciar los roles asociados a esta entidad
    private List<User> users;

    public Role() {
        this.users = new ArrayList<>();
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
