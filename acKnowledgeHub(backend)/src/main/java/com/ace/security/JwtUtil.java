package com.ace.security;

import com.ace.entity.Company;
import com.ace.entity.Position;
import com.ace.entity.Staff;
import com.ace.service.CompanyService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Configuration
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final CompanyService companyService;

    public JwtUtil(CompanyService companyService) {
        this.companyService = companyService;
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
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Staff extractUserDataFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
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
        // Convert String secret to Key
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
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

        return staff;
    }
}