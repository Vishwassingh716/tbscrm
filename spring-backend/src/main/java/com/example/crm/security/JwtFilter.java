package com.example.crm.security;
import jakarta.servlet.FilterChain; import jakarta.servlet.ServletException; import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil; public JwtFilter(JwtUtil jwtUtil){ this.jwtUtil=jwtUtil; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header=request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header!=null && header.startsWith("Bearer ")){ String token=header.substring(7); String subject=jwtUtil.validateTokenAndGetSubject(token); if(subject!=null && SecurityContextHolder.getContext().getAuthentication()==null){ UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(subject,null, List.of(new SimpleGrantedAuthority("ROLE_USER"))); auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); SecurityContextHolder.getContext().setAuthentication(auth); } }
        filterChain.doFilter(request,response);
    }
}
