package com.example.template.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class AesUtils {

    private static final byte[] keyData = Base64Utils.decodeFromString("B4obYmCraUAnt1ben8tiCQ==");

    /**
     * AES256 으로 암호화 한다.
     *
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */

    @SneakyThrows
    public String encrypt(String str) {
        if(ObjectUtils.isEmpty(str)) {
            return "";
        }
        byte[] IV = new byte[16];
        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV));
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeBase64(encrypted));
        return enStr;
    }

    @SneakyThrows
    public String decrypt(String str) {
        if(ObjectUtils.isEmpty(str)) {
            return null;
        }

        byte[] IV = new byte[16];
        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV));

        byte[] decrypted = Base64Utils.decodeFromString(str);
        return new String(c.doFinal(decrypted), "UTF-8");
    }



}
