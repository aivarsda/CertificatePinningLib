CertificatePinningLib
=====================
The library allows you to pin the server certificates by using the HttpsURLConnection.
You will be able to validate the certificate chain and ensure that one of the pins in 
the specified/trusted set of SubjectPublicKeyInfos appears in the valid certificate chain.

How To Use
--------------
[![Get it on Google Play](https://raw.github.com/repat/README-template/master/googleplay.png)](https://play.google.com/store/apps/details?id=com.aivarsda.certpinning.demo)

[![Get it on Google Play](https://lh3.ggpht.com/pVdyjPTdweDrZHmxPPjXKVD2YIVGUwOqoiY-ppznhQbD5x54ql8IlwgvxLfhPMn3XJ8=h310-rw)](https://play.google.com/store/apps/details?id=com.aivarsda.certpinning.demo)
[![Get it on Google Play](https://lh4.ggpht.com/mzmSWpNjCNlpU52aCIE7wGOcjWJJndRc8OjmAB7_rSNKn3MFvvy1iNx8QE50P9b4Yd5P=h310-rw)](https://play.google.com/store/apps/details?id=com.aivarsda.certpinning.demo)
[![Get it on Google Play](https://lh5.ggpht.com/onAIPFYkMT-O4kXEb1nP9N9T9YE7HseOOyRIfCHi-R0QizBiACuGo85r4pRF82_lUMwS=h310-rw)](https://play.google.com/store/apps/details?id=com.aivarsda.certpinning.demo)
- <b>1. In your class, that will be using the HttpsPinner, implement the IPinnerCallback:</b>
```java
 @Override
 public void onTaskPinningSuccess(PinnedConnectionResponse pinnedConnectionResponse) 
 {
 	 //Your logic on connection pinning success ...//
 }
 @Override
 public void onTaskPinningFailure(PinnedConnectionResponse pinnedConnectionResponse) 
 {
 	 //Your logic on connection pinning failure ...//
 }
```
 
- <b>2. Pinning the connection with HttpsPinner as following:</b>
```java
 private void execPinnedConection()
 {
 	String[] trustedPinsSet 	= new String[] {"a36012xcc17c231ac1ag6b788e610c8k75418t543"};
 	String serverUrl            = "https://YOUR_SERVER_URL";
 
 	HttpsPinner httpsPinner = new HttpsPinner(trustedPinsSet,false);
 	PinnedConnectionRequest  pinnedConnectionRequest = new PinnedConnectionRequest("GET",serverUrl);
 	httpsPinner.getPinnedHttpsConnectionTask(this).execute(pinnedConnectionRequest);
 }
```


## Contact Developer
Aivars Dalderis
* e-mail: <aivars.dalderis@gmail.com>
* LinkedIn: [Aivars LinkedIn](http://il.linkedin.com/in/aivarsd)
