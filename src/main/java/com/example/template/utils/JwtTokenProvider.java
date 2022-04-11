package com.example.template.utils;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    public final static String secretKeyMaster = "F12GViFbqBay6Cnx/fWIBAYFv/vGZWUmz7aiBCuP8IVKEO9J4oX5V1yfbgKCbVDw4lKdIRz7llprMlEVJ/0IY8nYKUpCORXeBPF3Vaodn/4A73yk44hMUw0PU8tbdZgVG7kVp4sFowkESxW1n0+yThawORZRmuPkxDXPttyvCMxo3guoMH1MsWElIxdC7tKvz1Nx6dHpVBiboZ0bm0rVlrwU8oP5GacXvrSqb58eYdaP20c5WNOqVfSTNIiITHhBJ8JnhG5LpmmU4o7R4uyNDyEzfoGQe5jN/c9pvW+BnjtoFY7/IpWlsIYXkE+MEh421GWzBnIl2qYoOJiY5kFy";

    public String createRegisterToken(Long memberId, String name, String email) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String key = secretKeyMaster;
        //String role = "";

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> headerMap = new HashMap<>();

        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(memberId));
        map.put("name", name);
        map.put("email", email);

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(DateUtils.addDays(new Date(), 7))
                .signWith(signingKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }

    public String createExpireToken(Long memberId, String name, String email) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String key = secretKeyMaster;
        //String role = "";

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> headerMap = new HashMap<>();

        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(memberId));
        map.put("name", name);
        map.put("email", email);

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(new Date())
                .signWith(signingKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }

    public String createAuthToken() {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String key = secretKeyMaster;
        //String role = "";

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> headerMap = new HashMap<>();

        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setExpiration(DateUtils.addMinutes(new Date(), 3))
                .signWith(signingKey, SignatureAlgorithm.HS256);

        return builder.compact();

    }

    public String getSubject(String token) {
        String key = secretKeyMaster;
        return Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Map<String, String> getClaims(String token) {
        String key = secretKeyMaster;
        Claims claims = Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Map<String, String> map = new HashMap<>();
        map.put("id", claims.get("id", String.class));
        map.put("name", claims.get("name", String.class));
        map.put("email", claims.get("email", String.class));
        return map;
    }

    public boolean validateToken(String token) {
        try {
            String key = secretKeyMaster;
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


}
