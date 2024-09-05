package com.william.curso.springboot.app.springbootcrud.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.william.curso.springboot.app.springbootcrud.entities.User;
import com.william.curso.springboot.app.springbootcrud.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService{       //implementando de UserDetailsService esta clase se va a registrar en SpringSecurityCOntext siendo parte del contexto

    @Autowired
    private UserRepository repository;

    //v203
    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(username);

        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
        }
        User user = userOptional.orElseThrow();
        List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), //se escribe el paquete porque ya se está importando nuestra clase User y esta es una clase User diferente
        user.getPassword(), 
        user.isEnabled(),
        true,
        true,
        true,
        authorities);
    }

}
