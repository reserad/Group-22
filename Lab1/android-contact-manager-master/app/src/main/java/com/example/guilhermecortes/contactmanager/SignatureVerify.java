package com.example.guilhermecortes.contactmanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by Alec on 4/21/2016.
 */

public class SignatureVerify
{
    private static final int VALID = 0;
    private static final int INVALID = 1;
    private static final String SIGNATURE = "sA+HrPBYed/m5lt45CJFBTslxJw=";

    public static int checkSignature(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                if (SIGNATURE.trim().equals(currentSignature.trim()))
                    return VALID;
            }
        }
        catch (Exception e) { }
        return INVALID;
    }
}