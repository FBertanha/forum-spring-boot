package br.com.alura.forum.config.security;

import br.com.alura.forum.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${forum-jwt-expiration}")
    private Long expiration;
    @Value("${forum-jwt-secret}")
    private String secret;

    public String gerarToken(Authentication authenticate) {
        Usuario logado = (Usuario) authenticate.getPrincipal();
        Date hoje = new Date();
        Date exp = new Date(hoje.getTime() + expiration);
        return Jwts.builder()
                .setIssuer("APi do Fórum da Alura")
                .setSubject(logado.getId().toString())
                .setIssuedAt(hoje)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isTokenValido(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Long getIdUsuario(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }
}
