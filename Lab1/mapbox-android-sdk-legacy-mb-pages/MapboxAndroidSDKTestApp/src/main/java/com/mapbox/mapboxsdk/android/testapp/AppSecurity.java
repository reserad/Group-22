package com.mapbox.mapboxsdk.android.testapp; /**
 * Created by Alec on 4/14/2016.
 */

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AppSecurity
{
    public static String Encrypt(String string) throws UnsupportedEncodingException {
        return Base64.encodeToString(string.getBytes("UTF-8"), Base64.DEFAULT );
    }
    public static String Decrypt(String string) throws UnsupportedEncodingException {
        byte[] byteArray = Base64.decode(string.getBytes(), Base64.DEFAULT  );
        return new String(byteArray, "UTF-8");
    }

    public static String generateMac(String key, String data) {
        String hash="";
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secret_key);
            hash = Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.DEFAULT);
        }
        catch (Exception e) { }
        return hash.trim();
    }
}