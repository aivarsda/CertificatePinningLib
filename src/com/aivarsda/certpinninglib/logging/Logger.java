package com.aivarsda.certpinninglib.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import android.os.Environment;

public class Logger
{
	public static final String TAG = "Logger";
	
	public static void logCertificate(final Certificate cert, boolean isCertificateValid, String strPin, boolean hasTrustedPin) 
	{
		Log.w(TAG,"--------------------------------------------------------------------------------");
		Log.w(TAG,"Cert Type : " + cert.getType());
		Log.w(TAG,"Cert Public Key Algorithm : "+ cert.getPublicKey().getAlgorithm());
		Log.w(TAG,"Cert Public Key Format : "+ cert.getPublicKey().getFormat());
		
		if(cert instanceof X509Certificate) 
		{
			Log.w(TAG,"Signature Algorithm Name : " + ( (X509Certificate) cert).getSigAlgName());
			Log.w(TAG,"Valid From : " + ( (X509Certificate) cert).getNotBefore());
			Log.w(TAG,"Valid Until : " + ( (X509Certificate) cert).getNotAfter());
			Log.w(TAG,"Issuer DN : " + ( (X509Certificate) cert).getIssuerDN());
			Log.w(TAG,"Subject DN : " + ( (X509Certificate) cert).getSubjectDN());
			Log.w(TAG,"**[ Certificate is VALID ]**");
			Log.w(TAG,"**[ Certificate Pin = " + strPin + " ]**");
			Log.w(TAG,"**[ Certificate Pin is Trusted = "+hasTrustedPin+" ]**");
        }
		else
		{
			Log.w(TAG,"**[ NOT X509Certificate ]**");
		}
	}
	
	public static String getLogContent()
	{
		StringBuilder strBuilder = new StringBuilder();
		try
		{
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard,Log.LOG_PATH);

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
			{
				strBuilder.append(line);
				strBuilder.append('\n');
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return strBuilder.toString();
	}
	
	public static boolean delLog()
	{
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard,Log.LOG_PATH);
		boolean isDeleted = file.delete();
		return isDeleted;
	}
}
