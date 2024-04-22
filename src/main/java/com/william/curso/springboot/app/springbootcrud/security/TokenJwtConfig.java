package com.william.curso.springboot.app.springbootcrud.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build(); //v205  copiado desde el repositorio github //eso nunca se mueve ni se cambia (mientras la aplicación está levantada), es la llave que usa la aplicación paracodificar lo que se requiera
    public static final String PREFIX_TOKEN = "Bearer";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

    //los token se generan cada vez que se inicia la aplicación
    //en este caso, cuando se loguea, se genera un token
    //para entrar a los recursos protegidos, siempre se debe usar el token que se haya generado para esa sesión
}
