package com.ace.security;

import com.ace.entity.Company;
import com.ace.entity.Position;
import com.ace.entity.Staff;
import com.ace.enums.Role;
import com.ace.service.CompanyService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

@Configuration
public class JwtUtil {

    private final CompanyService companyService;
    private final Environment env;

    public JwtUtil(CompanyService companyService, Environment env) {
        this.companyService = companyService;
        this.env = env;
    }

    public String generateToken(Staff user) {
        return Jwts.builder()
                .setSubject(user.getCompanyStaffId())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .claim("position",user.getPosition().getName())
                .claim("company",user.getCompany().getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getKeyFromJwtSecret(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Staff extractUserDataFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || token.isEmpty()) {
            return null;
        }
        Claims claims = extractClaims(token);
        return setStaffData(claims);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    public Cookie createCookie(boolean rememberMeFlg, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        if (rememberMeFlg) {
            cookie.setMaxAge(86400);
        }
        cookie.setSecure(true);
        return cookie;
    }

    public Claims extractClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getKeyFromJwtSecret())
                .build();

        return parser.parseClaimsJws(token).getBody();
    }

    public Staff setStaffData(Claims claims) {
        Staff staff = new Staff();
        staff.setCompanyStaffId(claims.getSubject());

        Company company = Company.builder()
                .id(companyService.findByName(claims.get("company", String.class)).getId())
                .name(claims.get("company", String.class))
                .build();
        staff.setCompany(company);

        Position position = Position.builder()
                .name(claims.get("position", String.class))
                .build();
        staff.setPosition(position);

        staff.setRole(Role.valueOf(claims.get("role", String.class).toUpperCase()));
        return staff;
    }

    public Key getKeyFromJwtSecret() {
        byte[] secretBytes = Objects.requireNonNull(
                env.getProperty("jwt.secret"), "jwt.secret must be set")
                .getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 64) {
            try {
                secretBytes = MessageDigest.getInstance("SHA-512").digest(secretBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-512 is required to derive the JWT signing key", e);
            }
        }
        return Keys.hmacShaKeyFor(secretBytes);
    }
}