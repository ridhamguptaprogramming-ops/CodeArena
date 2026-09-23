package com.codearena.security;


import io.jsonwebtoken.JwtException; 
import jakarta.servlet.*; 
import jakarta.servlet.http.*; 
import java.io.IOException; 
import java.util.*; 
import org.springframework.context.annotation.*; 
import org.springframework.http.HttpMethod; 
import org.springframework.lang.NonNull; 
import org.springframework.security.authentication.*; 
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.config.http.SessionCreationPolicy; 
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.security.web.*; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
import org.springframework.web.cors.*; 
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import org.springframework.http.HttpStatus;


@Configuration @EnableMethodSecurity 
public class SecurityConfig { 
    @Bean 
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    } 
    @Bean 
    SecurityFilterChain filterChain(HttpSecurity h, JwtService jwt, StringRedisTemplate redis, @org.springframework.beans.factory.annotation.Value("${app.cors-origins}") String origins) throws Exception { 
    h.csrf(c->c.disable()).cors(c->c.configurationSource
            (req->{var x=new CorsConfiguration();x.setAllowedOriginPatterns(Arrays.stream(origins.split(",")).map(String::trim).toList());x.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));x.setAllowedHeaders(List.of("Authorization","Content-Type"));x.setAllowCredentials(true);return x;})).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/refresh","/api/v1/auth/logout","/actuator/health").permitAll().requestMatchers(HttpMethod.GET,"/api/v1/problems/**").permitAll().requestMatchers("/ws/**").permitAll().anyRequest().authenticated()).addFilterBefore(new JwtFilter(jwt),UsernamePasswordAuthenticationFilter.class).addFilterBefore(new RateLimitFilter(redis),UsernamePasswordAuthenticationFilter.class); return h.build(); }
static class RateLimitFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redis; RateLimitFilter(StringRedisTemplate redis){this.redis=redis;}
    @org.springframework.lang.NonNull protected void doFilterInternal(@NonNull HttpServletRequest req,@NonNull HttpServletResponse res,@NonNull FilterChain chain)throws ServletException,IOException {
        if(req.getRequestURI().startsWith("/api/v1/auth/")&&req.getMethod().equals("POST")) {
            String key="rate:auth:"+req.getRemoteAddr()+":"+req.getRequestURI();
            try { Long n=redis.opsForValue().increment(key); if(n!=null&&n==1)redis.expire(key,Duration.ofMinutes(1)); if(n!=null&&n>20){res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());return;} }
            catch(RuntimeException unavailable){/* Redis outages do not make authentication unavailable. */}
        }
        chain.doFilter(req,res);
    }
}
static class JwtFilter extends OncePerRequestFilter { 
    final JwtService jwt; JwtFilter(JwtService j){jwt=j;
    } 
    @org.springframework.lang.NonNull protected void doFilterInternal(
        @NonNull HttpServletRequest r,
        @NonNull HttpServletResponse s,
        @NonNull FilterChain c)throws ServletException,
        IOException {var h=r.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer "))try{var claims=jwt.parse(h.substring(7));if(!"access".equals(claims.get("typ")))throw new JwtException("wrong token type");var roles=((List<?>)claims.get("roles")).stream().map(v->new SimpleGrantedAuthority("ROLE_"+v)).toList();SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(),null,roles));}catch(JwtException ignored){}c.doFilter(r,s);} } }
