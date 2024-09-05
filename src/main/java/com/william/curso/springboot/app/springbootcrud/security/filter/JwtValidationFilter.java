package com.william.curso.springboot.app.springbootcrud.security.filter;

import static com.william.curso.springboot.app.springbootcrud.security.TokenJwtConfig.CONTENT_TYPE;
import static com.william.curso.springboot.app.springbootcrud.security.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.william.curso.springboot.app.springbootcrud.security.TokenJwtConfig.PREFIX_TOKEN;
import static com.william.curso.springboot.app.springbootcrud.security.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.william.curso.springboot.app.springbootcrud.security.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
            String header = request.getHeader(HEADER_AUTHORIZATION);    //Authorization

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {       //se busca BEARER
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace(PREFIX_TOKEN, "").trim();                //se quita el bearer para poder trabajar solo con el token
        System.out.println("tokeeeeen: "+token);
        try{
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            //v210  validando token
            String username = claims.getSubject();  //de otra manera: String username = (String)claims.get("username");
            Object authoritiesClaims = claims.get("authorities");   //lo que se pasa como argumento es tal como se definió en successfulAuthentication de JwtAuthenticationFilter

            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)    //se agrega la abstract class creada SimpleGrantedAuthorityJsonCreator  La función addMixIn en Jackson permite asociar una clase mix-in con otra clase durante la serialización y deserialización de objetos JSON   al serializar o deserializar objetos de la clase SimpleGrantedAuthority, Jackson utilizará las anotaciones y configuraciones definidas en la clase SimpleGrantedAuthorityJsonCreator
                .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);    //el segundo argumento es null porque solo se está validando el token, sino podría ir password
            SecurityContextHolder .getContext().setAuthentication(authenticationToken);  //SecurityContextHolder: Es una clase proporcionada por Spring Security que se utiliza para acceder al contexto de seguridad de la aplicación.	getContext(): Este método devuelve el contexto de seguridad actual, que contiene información sobre la autenticación y autorización del usuario.	setAuthentication(authenticationToken): Este método establece el objeto de autenticación proporcionado (authenticationToken) en el contexto de seguridad actua
            chain.doFilter(request, response);  //esto es para continuar con los otros filtros que se están aplicando
            //v210
        }catch(JwtException e){
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es inválido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }
    }

}
