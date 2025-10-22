package com.example.crm.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key; import java.util.Date;
@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.access-expiration-minutes}") private long accessMinutes;
    @Value("${jwt.refresh-expiration-days}") private long refreshDays;
    private Key getKey(){ return Keys.hmacShaKeyFor(secret.getBytes()); }
    public String generateAccessToken(String subject){ Date now=new Date(); Date exp=new Date(now.getTime()+accessMinutes*60*1000); return Jwts.builder().setSubject(subject).setIssuedAt(now).setExpiration(exp).signWith(getKey(), SignatureAlgorithm.HS256).compact(); }
    public String generateRefreshToken(String subject){ Date now=new Date(); Date exp=new Date(now.getTime()+refreshDays*24*60*60*1000); return Jwts.builder().setSubject(subject).setIssuedAt(now).setExpiration(exp).signWith(getKey(), SignatureAlgorithm.HS256).compact(); }
    public String validateTokenAndGetSubject(String token){ try{ Jws<Claims> claims=Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token); return claims.getBody().getSubject(); }catch(Exception e){ return null; } }
}
