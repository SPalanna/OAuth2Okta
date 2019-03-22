package com.sp.auth.OktaOAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.io.DataOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.sp.auth.OktaOAuth2.GetPublicKey.getKeyFromServer;

/**
 * https://github.com/dogeared/JWKTokenVerifier/blob/master/pom.xml
 */

public class JWKTokenVerifier {

    public static String verify(String token) {

        String accessTokenString = token;
        //final String modulusString = "sUlUr5E-T05aAnrN7ZVHbEosHyphRq8WceKlj5LdcGfG8vgyzmjOfE-4Jhj8qoBNIjNKUqTrmCUfVuTAshjX5yZeKqK07xy6RAo4z03Z5XBsuKuRbtNr9kjGe0KdQp339qDCrsU6-RLSVFtCAkdhvC-Dz2yGP74P75WoTDhV0gVX9KC55MGP-cFnXBA6GkAjwUEUeVnPBNHfZvZHkQxrZIGSmCdrBeFVTmxdrKVKEYspes99AkjizI7Fh-eQRJlIXJRd0H89yp9RBX0Os2lgx_hcr1vb0dCI3mJ3eNBc3__2eT7KTIHRABizeeC--WRJfTgFEsTFJXLFhEPTgekB4Q";
        //final String exponentString = "AQAB";

        //final String keyURL = args[1];
        //final String clientID = args[2];

        final String keyURL = "https://dev-307684.oktapreview.com/oauth2/v1/keys";
        final String clientID = "0oajl6fatuzs6g7eQ0h7";

        try {

            JSONArray jwk =  GetPublicKey.getKeyFromServer(keyURL,clientID);
            final String modulusString = jwk.getJSONObject(0).getString("n");
            final String exponentString = jwk.getJSONObject(0).getString("e");
            System.out.println("Modulus : " + modulusString);
            System.out.println("Exponent : " + exponentString);


        SigningKeyResolver resolver = new SigningKeyResolverAdapter() {
            public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
                try {
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusString));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentString));
                    System.out.println("Modulus : " + modulus);
                    System.out.println("Exponent : " + exponent);

                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKeyResolver(resolver)
                    .parseClaimsJws(accessTokenString);

            System.out.println("Verified Access Token");
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwsClaims));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessTokenString;
    }
}