package com.william.curso.springboot.app.springbootcrud.security.filter;

import java.io.IOException;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.william.curso.springboot.app.springbootcrud.security.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private AuthenticationManager authenticationManager;    

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)   //v204
            throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;

        System.out.println("============================================================================================================intento de autenticación============================================================================================================");
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);  //Esta línea de código crea un nuevo objeto ObjectMapper, que es una clase de la biblioteca Jackson utilizada para convertir entre objetos Java y JSON. Luego, utiliza el método readValue() para leer y convertir el flujo de entrada de la solicitud HTTP (request.getInputStream()) en un objeto de la clase User. Esto es útil para deserializar los datos JSON enviados en el cuerpo de la solicitud HTTP a un objeto Java User, lo que permite al servidor procesar y trabajar con esos datos en la aplicación
            System.out.println("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[tryyyyyyyyyyyyyyyyyyy]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e) {
            System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOR StreamReadException");
            e.printStackTrace();
        } catch (DatabindException e) {
            System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOR DatabindException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOR IOException");
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,    //v205  se ejecuta si todo sale bien
            Authentication authResult) throws IOException, ServletException {
        
            com.william.curso.springboot.app.springbootcrud.entities.User user = (com.william.curso.springboot.app.springbootcrud.entities.User)authResult.getPrincipal();    //obtiene el principal del resultado de la autenticación (authResult). En un contexto de autenticación en Spring Security, el "principal" representa al usuario autenticado. La expresión authResult.getPrincipal() devuelve el objeto principal asociado con la autenticación exitosa
            String username = user.getUsername();
            Collection<? extends GrantedAuthority> roles = authResult.getAuthorities(); //"authorities" (autoridades) representan los permisos o roles concedidos a un usuario dentro del sistema. Estos permisos determinan qué acciones o recursos puede acceder o modificar el usuario en la aplicación
            //authResult.getAuthorities(): Este método devuelve una colección de objetos GrantedAuthority, que representan los roles o permisos del usuario autenticado. Los roles son concedidos durante el proceso de autenticación y representan los niveles de acceso o las capacidades del usuario dentro del sistema
            //Collection<? extends GrantedAuthority> roles: Declara una variable llamada roles que almacenará las autoridades del usuario. La sintaxis <? extends GrantedAuthority> indica que la colección puede contener cualquier subtipo de GrantedAuthority

            Claims claims = Jwts.claims()
            .add("authorities", new ObjectMapper().writeValueAsString(roles))  //no poner datos sensibles
            .add("username", username)
            .build();  //Los "claims" son las declaraciones sobre una entidad y suelen contener información como el nombre de usuario, roles, permisos, etc
            
            String token = Jwts.builder().subject(username).claims(claims).expiration(new Date(System.currentTimeMillis() + 3600000)).issuedAt(new Date()).signWith(SECRET_KEY).compact();
            //Jwts.builder(): Este método estático crea un constructor de tokens JWT que permite configurar los distintos componentes del token
            //.subject(username): Aquí se establece el sujeto del token, que generalmente es el nombre de usuario del usuario autenticado. El sujeto es una de las partes estándar de un token JWT y se utiliza para identificar al usuario al que pertenece el token
            //issuedAt: indica el momento en que se emitió el token. Representa la fecha y hora en la que se creó el token. Esta propiedad se usa para determinar si el token es válido y si aún está dentro de su período de validez
            //.signWith(SECRET_KEY): Esta parte del código indica que el token debe ser firmado utilizando una clave secreta. La firma es importante para garantizar la integridad del token y para que el servidor pueda verificar la autenticidad del token cuando lo recibe de nuevo
            //.compact(): Finalmente, este método finaliza la construcción del token y devuelve una cadena compacta que representa el token JWT completo
            response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN+token);   //variables importadas de TokenJwtConfig

            Map<String, String> body = new HashMap<>();
            body.put("token", token);
            body.put("username", username);
            body.put("message", String.format("Hola %s has iniciado sesión con éxito", username));
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setContentType(CONTENT_TYPE);
            response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, //v207
            AuthenticationException failed) throws IOException, ServletException {
            Map<String, String> body = new HashMap<>();
            body.put("message", String.format("Error en la autenticación, username o password incorrecto"));
            body.put("error", failed.getMessage());

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType(CONTENT_TYPE);
    }    
    //todo se debe configurar en spring security config    v208
}
