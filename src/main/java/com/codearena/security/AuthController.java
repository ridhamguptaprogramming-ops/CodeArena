package com.codearena.security;
import com.codearena.common.api.ApiResponse; 
import com.codearena.common.domain.Role; 
import com.codearena.user.*; 
import jakarta.validation.constraints.*; 
import org.springframework.http.*; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.*; 
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/v1/auth") 
public class AuthController { 
    final UserRepository users; final PasswordEncoder encoder; 
    final JwtService jwt; AuthController(UserRepository u,PasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;} 
    record Register(@Email String email,@NotBlank @Size(min=12,max=128) String password,@NotBlank @Size(max=100) String displayName){} 
    record Login(@Email String email,@NotBlank String password){} record Tokens(String accessToken,String refreshToken){} @PostMapping("/register") 
    @ResponseStatus(HttpStatus.CREATED) ApiResponse<Tokens> register(@RequestBody @jakarta.validation.Valid Register r){
        if(users.existsByEmailIgnoreCase(r.email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
        var u=new User();u.email=r.email.toLowerCase();u.passwordHash=encoder.encode(r.password);
        u.displayName=r.displayName;u.roles.add(Role.STUDENT);users.save(u);
        return ApiResponse.of(new Tokens(jwt.access(u),jwt.refresh(u)));} 
        @PostMapping("/login") 
        ApiResponse<Tokens> login(@RequestBody @jakarta.validation.Valid Login r){var u=users.findByEmailIgnoreCase(r.email).filter(x->encoder.matches(r.password,x.passwordHash)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));return ApiResponse.of(new Tokens(jwt.access(u),jwt.refresh(u)));} }
