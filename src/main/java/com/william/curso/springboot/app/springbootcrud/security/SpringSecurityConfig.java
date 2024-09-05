package com.william.curso.springboot.app.springbootcrud.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.william.curso.springboot.app.springbootcrud.security.filter.JwtAuthenticationFilter;
import com.william.curso.springboot.app.springbootcrud.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)   //activa las etiquetas hasRoles que se utilizan como anotaciones, no se necesita si se habilitaran acá
public class SpringSecurityConfig {

    //Configuración JwtAuthenticationFilter v208
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Fin Configuration

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{    //contiene los permisos y filtros que se están agregando
        return http.authorizeHttpRequests((authz) -> authz
        .requestMatchers(HttpMethod.GET,"/api/users").permitAll()  //se deja público la ruta users (todos los métodos que se mapean en esa ruta)
        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll()  //v200
        // .requestMatchers(HttpMethod.POST,"/api/users").hasRole("ADMIN") //acá no se incluye el prefijo, o sea no va ROLE_ADMIN
        // .requestMatchers(HttpMethod.GET,"/api/products","/api/products/{id}").hasAnyRole("ADMIN","USER")
        // .requestMatchers(HttpMethod.POST,"/api/products").hasRole("ADMIN")  //estas configuraciones (hasRole) se pueden hacer con anotaciones (como el de UserController)
        // .requestMatchers(HttpMethod.PUT,"/api/products/{id}").hasRole("ADMIN")
        // .requestMatchers(HttpMethod.DELETE,"/api/products/{id}").hasRole("ADMIN")   //todo esto se puede hacer en una tabla de bd, agregando todas estas rutas y consultando para asignar permisos
        .anyRequest().authenticated())  //cualquier otra ruta necesita autenticación
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))    //v208  new JwtAuthenticationFilter(authenticationManager()) crea una instancia del filtro JwtAuthenticationFilter, que es responsable de manejar la autenticación basada en JWT. El constructor JwtAuthenticationFilter recibe un parámetro authenticationManager(), que proporciona el AuthenticationManager de Spring. Este AuthenticationManager se utiliza para autenticar las solicitudes entrantes mediante JWT .addFilter() agrega este filtro al flujo de filtros de seguridad de Spring. Esto asegura que las solicitudes entrantes pasen por el filtro JwtAuthenticationFilter, donde se realiza la autenticación basada en JWT antes de pasar a los filtros de seguridad posteriores
        .addFilter(new JwtValidationFilter(authenticationManager()))    //v210
        .csrf(config -> config.disable())   //Aquí se está deshabilitando la protección CSRF (Cross-Site Request Forgery) para la aplicación. CSRF es un tipo de ataque que implica enviar solicitudes no autorizadas desde un sitio web malicioso en nombre del usuario autenticado en otro sitio web
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))  //v215
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //la aplicación no mantendrá información de sesión para los usuarios y cada solicitud se manejará de forma independiente, sin depender de sesiones anteriores
        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){  //v215
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corseFilter(){   //v215
        FilterRegistrationBean<CorsFilter>  corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return corsBean;
    }
}
