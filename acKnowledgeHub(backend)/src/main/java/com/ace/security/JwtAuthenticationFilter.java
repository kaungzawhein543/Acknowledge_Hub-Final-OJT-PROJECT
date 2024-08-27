package com.ace.security;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//
//    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        try {
//            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
//            return authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(user.getStaff().getStaffId(), user.getPassword())
//            );
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (StreamReadException e) {
//            throw new RuntimeException(e);
//        } catch (DatabindException e) {
//            throw new RuntimeException(e);
//        } catch (java.io.IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
//        User user = (User) authResult.getPrincipal();
//
//        String token = Jwts.builder()
//                .setSubject(user.getStaff().getStaffId())
//                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS512, JwtUtil.SECRET)
//                .compact();
//
//        response.addHeader(JwtUtil.HEADER_STRING, JwtUtil.TOKEN_PREFIX + token);
//    }
}