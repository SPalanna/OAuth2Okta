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

import org.json.JSONObject;

/**
 * https://github.com/dogeared/JWKTokenVerifier/blob/master/pom.xml
 */

public class JWKTokenVerifier {

    public static String verify(String token) {

        String accessTokenString = token;
        final String modulusString = "sUlUr5E-T05aAnrN7ZVHbEosHyphRq8WceKlj5LdcGfG8vgyzmjOfE-4Jhj8qoBNIjNKUqTrmCUfVuTAshjX5yZeKqK07xy6RAo4z03Z5XBsuKuRbtNr9kjGe0KdQp339qDCrsU6-RLSVFtCAkdhvC-Dz2yGP74P75WoTDhV0gVX9KC55MGP-cFnXBA6GkAjwUEUeVnPBNHfZvZHkQxrZIGSmCdrBeFVTmxdrKVKEYspes99AkjizI7Fh-eQRJlIXJRd0H89yp9RBX0Os2lgx_hcr1vb0dCI3mJ3eNBc3__2eT7KTIHRABizeeC--WRJfTgFEsTFJXLFhEPTgekB4Q";
        final String exponentString = "AQAB";

        //final String keyURL = args[1];
        //final String clientID = args[2];

        final String keyURL = "https://dev-307684.oktapreview.com/oauth2/v1/keys";
        final String clientID = "0oajl6fatuzs6g7eQ0h7";

        try {
            getKeys(keyURL,clientID) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        SigningKeyResolver resolver = new SigningKeyResolverAdapter() {
            public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
                try {
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusString));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentString));

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
            e.printStackTrace();
        }
        return accessTokenString;
    }

    public static String getKeys(String keyUrl, String clientID) throws Exception{

        URL obj = new URL(keyUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //add request header
        System.out.println(" Before get ");
        con.setRequestProperty("User-Agent", "Apache-HttpClient/4.1.1");
        con.setRequestProperty("Content-Type", "application/json");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientID);
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + keyUrl);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print in String
        System.out.println(response.toString());

        //Read JSON response and print
        JSONObject myResponse = new JSONObject(response.toString());
        System.out.println("result after Reading JSON Response");
        System.out.println("origin- "+myResponse.getString("origin"));

        return inputLine;
    }
}