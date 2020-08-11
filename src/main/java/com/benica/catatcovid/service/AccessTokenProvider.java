package com.benica.catatcovid.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.benica.catatcovid.CatatcovidApplication;
import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.exception.TokenExpiredException;
import com.fasterxml.jackson.databind.util.ClassUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenProvider
{
    @Autowired private Environment env;
    
    private final Logger logger = LoggerFactory.getLogger(AccessTokenProvider.class);

    RSAKeyProvider keyProvider = new RSAKeyProvider() {

        @Override
        public RSAPublicKey getPublicKeyById(String keyId) {
            String publicKeyPath = env.getProperty("app.jwt.rsa.public_key");
			try (BufferedReader bufferedReader = 
				new BufferedReader(new FileReader(publicKeyPath)))
			{
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while((line = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(line);
				}
				
                String keyContent = stringBuilder.toString()
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");

				X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyContent));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(spec);

				return publicKey;
			} catch (Exception ex) {
				logger.error(ex.getClass().getName() + ":" + ex.getMessage());
			}

			return null;
        }

        @Override
        public RSAPrivateKey getPrivateKey() {
            String privateKeyPath = env.getProperty("app.jwt.rsa.private_key");
			try (BufferedReader bufferedReader = 
				new BufferedReader(new FileReader(privateKeyPath)))
			{
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while((line = bufferedReader.readLine()) != null)
				{
					stringBuilder.append(line);
				}
				
                String keyContent = stringBuilder.toString()
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");
                    
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyContent));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(spec);

				return privateKey;
			} catch (Exception ex) {
				logger.error(ex.getClass().getName() + ":" + ex.getMessage());
			}

			return null;
        }

        @Override
        public String getPrivateKeyId() {
            return "private_key";
        }
        
    };

    public String encode(User user) throws Exception
    {
        Algorithm algorithm = Algorithm.RSA256(keyProvider);

        Integer tokenExpirationMinute = Integer.parseInt(env.getProperty("app.jwt.token_expiration", "30"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, tokenExpirationMinute);
        
        String token  = JWT.create()
            .withIssuer(ClassUtil.getPackageName(CatatcovidApplication.class))
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(calendar.getTime())
            .withClaim("user_id", user.getId())
            .withClaim("user_name", user.getUsername())
            .sign(algorithm);

        return token;
    }

    public UserDTO verifyAndDecode(String token) throws Exception
    {
        try
        {
            String jwtToken = token.replace("Bearer ", "");
            Algorithm algorithm = Algorithm.RSA256(keyProvider); 

            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ClassUtil.getPackageName(CatatcovidApplication.class))
                .build();

            DecodedJWT jwt = verifier.verify(jwtToken);
            UserDTO payload = new UserDTO();
            payload.setId(jwt.getClaim("user_id").asLong());
            payload.setUsername(jwt.getClaim("user_name").asString());

            return payload;
        } catch (Exception ex) {
            logger.error(ex.getClass().getSimpleName() + " " + ex.getMessage());
            
            if (ex instanceof com.auth0.jwt.exceptions.TokenExpiredException) throw new TokenExpiredException();
            else throw new InvalidAuthorizationTokenException();
        }
    }
}