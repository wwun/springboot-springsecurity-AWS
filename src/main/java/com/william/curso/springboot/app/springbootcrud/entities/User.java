package com.william.curso.springboot.app.springbootcrud.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.william.curso.springboot.app.springbootcrud.validation.ExistsByUsername;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ExistsByUsername
    @Column(unique = true)
    @NotBlank
    @Size(min=4, max=12)
    private String username;

    @NotBlank
    //@JsonIgnore //ignora el campo y no lo muestra en el json como @JsonProperty pero tanto para escribir como para leer
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  //para no mostrarlo en el json, solo se da acceso cuando se escribe el json(deserializa) y no cuando se lea. Se puede hacer utilizando un Dto definiendo qué datos se van a enviar
    private String password;

    //esta parte de es un ejemplo de cómo manejar relaciones bidireccionales, pero en la práctica no se listaría los roles
    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})   //ignora ciertos atributos del objeto
    @ManyToMany // no es cascade porque el rol no se crea junto al usuario
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id"),
        uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<Role> roles;
    //fin relaciones bidireccionales

    public User() {
        roles = new ArrayList<>();
    }

    private boolean enabled;    //cuando es primitivo por defecto es 0

    @Transient  //no es un campo de la tabla, sino de la clase
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @PrePersist
    public void prePersist(){   //agregando valor por defecto
        enabled=true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*public User(Long id, @NotBlank @Size(min = 4, max = 12) String username, @NotBlank String password,
            List<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        admin=false;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
    
}
