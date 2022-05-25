package spring.shat.demo.service;

// import io.lettuce.core.dynamic.annotation.Value;
// import lombok.Value;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    // 1시간만 토큰이 유효하도록 설정
    private long tokenValidMilisecond = 1000L * 60 * 60;

    /**
     * 이름으로 Jwt Token을 생성
     */
    public String generateToken(String name){
        Date now = new Date();
        return Jwts.builder()
                .setId(name)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 유효시간 설정
                .signWith(SignatureAlgorithm.ES256, secretKey) // 암호화 알고리즘, secret 값 세팅
                .compact();
    }


    /**
     * Jwt Token을 복호화하여 이름을 얻는다
     */
    public String getUserNameFromJwt(String jwt){
        return getClaims(jwt).getBody().getId();
    }


    /**
     * Jwt Token의 유효성을 체크한다
     */
    public boolean validateToken(String jwt){
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt) {
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException ex){
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex){
            log.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex){
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex){
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex){
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }
}