package com.codearena.security;
import com.codearena.common.api.ApiResponse; 
import com.codearena.common.domain.Role; 
import com.codearena.user.*; 
import jakarta.validation.constraints.*; 
import org.springframework.http.*; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.*; 
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets; import java.security.MessageDigest; import java.time.Instant; import java.util.HexFormat;
@RestController @RequestMapping("/api/v1/auth") 
public class AuthController { 
    final UserRepository users; final PasswordEncoder encoder; final JwtService jwt; final RefreshTokenRepository refreshTokens;
    AuthController(UserRepository u,PasswordEncoder e,JwtService j,RefreshTokenRepository rt){users=u;encoder=e;jwt=j;refreshTokens=rt;}
    record Register(@Email String email,@NotBlank @Size(min=12,max=128) String password,@NotBlank @Size(max=100) String displayName){} 
    record Login(@Email String email,@NotBlank String password){} record Refresh(@NotBlank String refreshToken){} record Tokens(String accessToken,String refreshToken){} @PostMapping("/register") 
    @ResponseStatus(HttpStatus.CREATED) ApiResponse<Tokens> register(@RequestBody @jakarta.validation.Valid Register r){
        if(users.existsByEmailIgnoreCase(r.email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
        var u=new User();u.email=r.email.toLowerCase();u.passwordHash=encoder.encode(r.password);
        u.displayName=r.displayName;u.roles.add(Role.STUDENT);users.save(u);
        return ApiResponse.of(issue(u));} 
        @PostMapping("/login") 
        ApiResponse<Tokens> login(@RequestBody @jakarta.validation.Valid Login r){var u=users.findByEmailIgnoreCase(r.email).filter(x->x.accountStatus.equals("ACTIVE")&&encoder.matches(r.password,x.passwordHash)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));return ApiResponse.of(issue(u));}
    @PostMapping("/refresh") @Transactional ApiResponse<Tokens> refresh(@RequestBody @jakarta.validation.Valid Refresh request){String hash=hash(request.refreshToken);var stored=refreshTokens.findByTokenHashAndRevokedAtIsNull(hash).filter(t->t.expiresAt.isAfter(Instant.now())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid refresh token"));var u=users.findById(java.util.Objects.requireNonNull(stored.userId)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));stored.revokedAt=Instant.now();refreshTokens.save(stored);return ApiResponse.of(issue(u));}
    @PostMapping("/logout") ApiResponse<Void> logout(@RequestBody @jakarta.validation.Valid Refresh request){refreshTokens.findByTokenHashAndRevokedAtIsNull(hash(request.refreshToken)).ifPresent(t->{t.revokedAt=Instant.now();refreshTokens.save(t);});return ApiResponse.of(null);}
    private Tokens issue(User u){String access=jwt.access(u), refresh=jwt.refresh(u);var row=new RefreshToken();row.userId=u.id;row.tokenHash=hash(refresh);row.expiresAt=Instant.now().plusSeconds(jwt.refreshTtlSeconds());refreshTokens.save(row);return new Tokens(access,refresh);}
    private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
}
