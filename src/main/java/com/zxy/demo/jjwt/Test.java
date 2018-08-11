package com.zxy.demo.jjwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * 生成token，验证token。
 *
 * https://github.com/jwtk/jjwt
 *
 * https://stormpath.com/blog/jwt-java-create-verify
 */
public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	private static String secret = "Jasper@123456";
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setId(6);
		userInfo.setName("测试");
		logger.info("UserInfo:" + objectMapper.writeValueAsString(userInfo));

		String token = generateToken(userInfo, 60 * 1000);
		logger.info("token:" + token);

		Object result = check(token);
		logger.info("check:" + objectMapper.writeValueAsString(result));
	}

	// 生成token
	public static String generateToken(UserInfo userInfo, long ttlSecs) {
		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builder = null;
		try {
			builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setIssuer(objectMapper.writeValueAsString(userInfo))
                    .signWith(signatureAlgorithm, signingKey);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}

		//if it has been specified, let's add the expiration
		if (ttlSecs >= 0) {
			long expMillis = nowMillis + ttlSecs * 1000;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	// 从token中反向解析出UserInfo
	public static UserInfo check(String token) {
		try {
			//This line will throw an exception if it is not a signed JWS (as expected)
			Claims claims = Jwts.parser()
					.setSigningKey(DatatypeConverter.parseBase64Binary(secret))
					.parseClaimsJws(token).getBody();
			String userInfoStr = claims.getIssuer();
			return objectMapper.readValue(userInfoStr, UserInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
