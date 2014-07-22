CertificatePinningLib
=====================
The library allows you to pin the server certificates by using the HttpsURLConnection.
You will be able to validate the certificate chain and ensure that one of the pins in 
the specified/trusted SubjectPublicKeyInfos appears in the valid certificate chain.

How To Use
--------------
[![Get it on Google Play](https://raw.github.com/repat/README-template/master/googleplay.png)](https://play.google.com/store/apps/details?id=com.aivarsda.certpinning.demo)
- 1. In you class implement the IPinnerCallback:
'''java
 @Override
 public void onTaskPinningCompleted(PinnedConnectionResponse pinnedConnectionResponse)
 {
 	//Your logic after pinnedConnectionResponse is returned ...//
 }
 '''
 
- 2. Pinning the connection as following:
'''java
 private void execPinnedConection()
 {
 	String[] trustedPinsSet 	= new String[] {"a36012xcc17c231ac1ag6b788e610c8k75418t543"};
 	URL serverUrl		= new URL("https://YOUR_SERVER_URL");
 
 	HttpsPinner httpsPinner = new HttpsPinner(trustedPinsSet,false);
 	PinnedConnectionRequest  pinnedConnectionRequest = new PinnedConnectionRequest("GET",serverUrl);
 	httpsPinner.getPinnedHttpsConnectionTask(this).execute(pinnedConnectionRequest);
 }
 '''


## Contact
Aivars Dalderis
* e-mail: <aivars.dalderis@gmail.com>
* LinkedIn: [Aivars LinkedIn](http://il.linkedin.com/in/aivarsd)
