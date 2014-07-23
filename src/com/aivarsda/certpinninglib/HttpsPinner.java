/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Aaivars Dalderis <aivars.dalderis@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.aivarsda.certpinninglib;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;

import com.aivarsda.certpinninglib.logging.Log;
import com.aivarsda.certpinninglib.logging.Logger;
import com.aivarsda.certpinninglib.network.PinnedConnectionRequest;
import com.aivarsda.certpinninglib.network.PinnedConnectionResponse;
import com.aivarsda.certpinninglib.utils.Hex;
import com.aivarsda.certpinninglib.utils.StringUtil;

import android.os.AsyncTask;

/**
 * @author Aivars Dalderis
 * 
 *         HttpsPinner will validate Certificate SubjectPublicKeyInfos (pins) by using the HttpsURLConnection.
 * 
 *         <p>
 *         HttpsPinner will validate all the certificate chain and ensure that
 *         one of the pins in the specified/trusted SubjectPublicKeyInfos
 *         appears in the valid certificate chain.
 *         </p>
 *         <p>
 *         <b>Usage example:</b>
 *         <p>
 *         NOTE : If you won't be using the log file, then just remove the
 *         com.aivarsda.certpinninglib.logging.Log instead import the basic
 *         android.util.Log
 *         </p>
 *<pre>
 *<u>In you class implement the IPinnerCallback:</u>
 *aOverride
 *public void onTaskPinningCompleted(PinnedConnectionResponse pinnedConnectionResponse)
 *{
 *	//Your logic after pinnedConnectionResponse is returned ...//
 *}
 *
 *<u>Pinning the connection as following:</u>
 *private void execPinnedConection()
 *{
 *	String[] trustedPinsSet 	= new String[] {"a36012xcc17c231ac1ag6b788e610c8k75418t543"};
 *	String serverUrl		= "https://YOUR_SERVER_URL";
 *
 *	HttpsPinner httpsPinner = new HttpsPinner(trustedPinsSet,false);
 *	PinnedConnectionRequest  pinnedConnectionRequest = new PinnedConnectionRequest("GET",serverUrl);
 *	httpsPinner.getPinnedHttpsConnectionTask(this).execute(pinnedConnectionRequest);
 *}
 * </pre>
 * 
 */
public class HttpsPinner
{
	public static final String	TAG								= "HttpsPinner";
	public static final String	CALLBACK_KEY_IS_SERVER_TRUSTED	= "IS_SERVER_TRUSTED";
	private boolean 			_stopPinningWhenTrusdedFound 	= false;

	/**
	 * Is an array of encoded pins, it will be matched against the certificate
	 * chain, to validate the specific certificate by checking if it has one of
	 * the trusted pins. A pin is a hex-encoded hash of a X.509 certificate's
	 * SubjectPublicKeyInfo.
	 */
	private final List<byte[]>	_trustedPins					= new LinkedList<byte[]>();

	/**
	 * Constructs a HttpsPinner with a set of valid pins.
	 * 
	 * @param trustedPins
	 *  A set of valid pins, which will be matched against the
	 *  certificate chain. A pin is a hex-encoded hash of the X.509
	 *  certificate's SubjectPublicKeyInfo.
	 * @param stopPinningWhenTrusdedFound 
	 * 	Flag that indicates if we should stop pinning after the trusted pin is found.
	 */
	public HttpsPinner(String[] trustedPins, boolean stopPinningWhenTrusdedFound)
	{
		for (String pin : trustedPins)
		{
			this._trustedPins.add(Hex.hexStringToBytes(pin));
		}
	}

	/**
	 * Will connect to the given serverURL and validate the supported
	 * certificates.
	 * @param pinnerTaskCallbackListener 
	 * 	The call back listener which will receive the pinnedConnectionResponse.
	 * @return PinnedConnectionResponse 
	 * 	The pinned connection response, which will hold a flag indicating whether the connection was secured
	 * 	and the String representing the InsputStream of the pinned connection.
	 */
	public PinnedHttpsConnectionTask getPinnedHttpsConnectionTask(IPinnerCallback pinnerTaskCallbackListener)
	{
		return new PinnedHttpsConnectionTask(pinnerTaskCallbackListener);
	}
	
	public class PinnedHttpsConnectionTask extends AsyncTask<PinnedConnectionRequest, Void, PinnedConnectionResponse>
	{
		private IPinnerCallback	pinnerTaskCallbackListener;

		public PinnedHttpsConnectionTask(IPinnerCallback pinnerTaskCallbackListener)
		{
			this.pinnerTaskCallbackListener = pinnerTaskCallbackListener;
		}

		@Override
		protected PinnedConnectionResponse doInBackground(PinnedConnectionRequest... pinnedConnectionRequests)
		{
			PinnedConnectionResponse pinnedConnectionResponse = null;
			for (PinnedConnectionRequest pinnedConnectionRequest : pinnedConnectionRequests)
			{
				pinnedConnectionResponse = getPinnedHttpsURLConnection(pinnedConnectionRequest);
			}
			return pinnedConnectionResponse;
		}

		/** Connects the HttpsURLConnection and returns the PinnedConnectionResponse
		 * @param pinnedConnectionRequest
		 * The pinnedConnectionRequest that will hold the connection URL which needs to be pinned and the HTTP method.
		 * @return PinnedConnectionResponse
		 * The pinned connection response, which will hold a flag indicating whether the connection was secured
		 * 	and the String representing the InsputStream of the pinned connection.
		 */
		private PinnedConnectionResponse getPinnedHttpsURLConnection(PinnedConnectionRequest pinnedConnectionRequest)
		{
			InputStream isConOutputRes = null;
			PinnedConnectionResponse pinnedConnectionResponse = new PinnedConnectionResponse();
			
			try
			{
				HttpsURLConnection httpsURLConnection = null;
				BrowserCompatHostnameVerifier hostNameVerifier = new BrowserCompatHostnameVerifier();
				HttpsURLConnection.setDefaultHostnameVerifier(hostNameVerifier);

				URL url = null;
				try 
				{
					url = new URL(pinnedConnectionRequest.getUrl());
				} 
				catch (MalformedURLException e)
				{
					e.printStackTrace();
					pinnedConnectionResponse.setConnResponse("Err: MalformedURL -> "+e.toString());
				}

				if(url!=null)
				{
					if (!url.getProtocol().equals("https"))
					{
						pinnedConnectionResponse.setConnResponse("Err: Attempt to construct pinned non-https connection!");
					}
					else
					{
						httpsURLConnection = (HttpsURLConnection) url.openConnection();
						httpsURLConnection.setRequestMethod(pinnedConnectionRequest.getMethod());
						httpsURLConnection.connect();
						
						Log.w(TAG, "Https Connection Cipher Suite : " + httpsURLConnection.getCipherSuite());
						if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK)
						{
							boolean isConnectionTrusted = validateTrustedPins(httpsURLConnection);
							pinnedConnectionResponse.setConTrusted(isConnectionTrusted);
							if(isConnectionTrusted)
							{
								isConOutputRes = httpsURLConnection.getInputStream();
								pinnedConnectionResponse.setConnResponse(StringUtil.inputStreamToString(isConOutputRes));
							}
							else
							{
								pinnedConnectionResponse.setConnResponse("Err: Connection Not Trusted");
							}
						}
					}
				}
				
				return pinnedConnectionResponse;
			}
			catch (MalformedURLException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (IOException e)
			{
				Log.e(TAG, e.toString());
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(PinnedConnectionResponse pinnedConnectionResponse)
		{
			pinnerTaskCallbackListener.onTaskPinningCompleted(pinnedConnectionResponse);
		}
	}

	/**
	 * Will go over all certificate chains of the given HttpsURLConnection and
	 * validate each one.
	 * 
	 * @param con HttpsURLConnection that needs to be pinned.
	 */
	private boolean validateTrustedPins(HttpsURLConnection con)
	{
		boolean isSrvTrusted = false;
		if (con != null)
		{
			try
			{
				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs)
				{
					// More info on X509Certificate -> http://www.ietf.org/rfc/rfc2459.txt
					if (cert instanceof X509Certificate)
					{
						// Checking the certificate validity, if not valid - exception will be thrown.
						((X509Certificate) cert).checkValidity();

						// Pinning the certificate against the trusted pins list.
						boolean hasTrustedPin = false;
						try
						{
							hasTrustedPin = hasTrustedPin((X509Certificate) cert);
							if (hasTrustedPin) isSrvTrusted = true;
						}
						catch (CertificateException e)
						{
							Log.e(TAG, e.toString());
						}

						// Stop when the trusted pin is found
						if (hasTrustedPin && _stopPinningWhenTrusdedFound) break;
					}
				}
			}
			catch (SSLPeerUnverifiedException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (CertificateExpiredException e1)
			{
				Log.e(TAG, e1.toString());
			}
			catch (CertificateNotYetValidException e1)
			{
				Log.e(TAG, e1.toString());
			}
		}

		return isSrvTrusted;
	}

	/**
	 * Will match the _trustedPins against the certificate chain, to validate
	 * the specific certificate by checking if it has one of the trusted pins.
	 * 
	 * @param certificate Certificate to validate with the pin.
	 * @return True - certificate has one of the trusted pins. False - otherwise.
	 * @throws CertificateException
	 */
	private boolean hasTrustedPin(X509Certificate certificate) throws CertificateException
	{
		try
		{
			boolean hasTrustedPin = false;
			final MessageDigest digest = MessageDigest.getInstance("SHA1");
			final byte[] encodedPublicKey = certificate.getPublicKey().getEncoded();
			final byte[] pin = digest.digest(encodedPublicKey);
			String strPin = Hex.bytesToHexString(pin);

			for (byte[] validPin : this._trustedPins)
			{
				if (Arrays.equals(validPin, pin))
				{
					hasTrustedPin = true;
					break;
				}
			}

			// Remove logging if not needed
			Logger.logCertificate(certificate, true, strPin, hasTrustedPin);

			return hasTrustedPin;
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new CertificateException(nsae);
		}
	}
}
