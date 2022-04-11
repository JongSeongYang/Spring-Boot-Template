package com.example.template.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class EncUtils {

    public static byte[] computeHash(byte[] s) {
        String messageDigest = "SHA-256";
        try {
            MessageDigest md = MessageDigest.getInstance(messageDigest);
            md.update(s);
            return md.digest();
        } catch (Exception e) {
            return null;
        }
    }

    public static String toHash(String value) {

        if (StringUtils.isEmpty(value)) return null;
        String ret = "";
        try {
            byte[] bytes = (value).getBytes("utf-8");
            byte[] encHashBytes = computeHash(bytes);
//            ret = Base64Utils.encodeToString(encHashBytes);
            ret = Hex.encodeHexString(encHashBytes);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.toString());
        }

        return ret;
    }

    public static String toPasswordHash(String password) {

        return toHash(password);
    }
}
