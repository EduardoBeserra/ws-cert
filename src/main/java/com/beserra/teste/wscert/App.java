package com.beserra.teste.wscert;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class App {
	
	private String senhaPrivada = "datacoper";
	private String senhaPublica = "12345678";
	private String nomeCertPrivado = "";
	private String nomeCertPublico = "";
	private String urlStr = "";
	
	
	
	public static void main(String[] args) throws Exception {
		new App().execute();
	}

	public App() {
		nomeCertPrivado = "82653726000198_129182693021671589602074674827715921253_datacoper.pfx";
		nomeCertPublico = "keystore_ICP_Brasil.pfx";
		urlStr = "https://hom.api.sgi.ms.gov.br/d0100/esaniagrointegracaoagrotoxico/v1/swagger/index.html";
		/*
		nomeCertPublico = "cert_google.cer";
		urlStr = "https://google.com";
		*/
	}
	private void execute() {
		try {
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); //SunX509
			
			KeyStore ksPrivado = KeyStore.getInstance("PKCS12");
			InputStream is = new FileInputStream(
					new File("C:\\Users\\Beserra\\Desktop\\cert\\" + nomeCertPrivado)
			);
			ksPrivado.load(is, senhaPrivada.toCharArray());
			is.close();
			kmf.init(ksPrivado, senhaPrivada.toCharArray());
			
			
			KeyStore ksPublico = KeyStore.getInstance("PKCS12");
			is = new FileInputStream(
					new File("C:\\Users\\Beserra\\Desktop\\cert\\" + nomeCertPublico)
			);
			ksPublico.load(is, senhaPublica.toCharArray());
			is.close();
			
			
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
				      TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(ksPublico);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(kmf.getKeyManagers(), trustManagers, null);
			
			SSLSocketFactory ssf = context.getSocketFactory();
			
			System.out.println("Conectando");
			URL url = new URL(urlStr);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.datacoper.com.br", 3128));
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection(proxy);
			setarProxy();
			con.setSSLSocketFactory(ssf);
			con.setRequestMethod("GET");
			
			Certificate certificate = ksPrivado.getCertificate("datacoper software ltda:82653726000198");
			byte[] encodedCert = certificate.getEncoded();
			String encodedStringCert = Base64.getEncoder().encodeToString(
					new String(encodedCert).getBytes(StandardCharsets.UTF_8)); 
			con.addRequestProperty("X-Client-Cert", encodedStringCert);
			con.setRequestProperty("Authorization", encodedStringCert);
			
			System.out.println("Aqui1");
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			System.out.println("Aqui2");
			
			String input;
			
			   while ((input = br.readLine()) != null){
			      System.out.println(input);
			   }
			   br.close();
			
			System.out.println("Fim");
			
		} catch (KeyStoreException e) {
			System.out.println("keyStore nao encontrada.");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algoritmo de criptografia nao encontrado.");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de certificado nao encontrado.");
			e.printStackTrace();
		} catch (CertificateException e) {
			System.out.println("Nao foi possivel validar o certificado");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro ao carregar o certificado.");
			e.printStackTrace();
		} /*catch (UnrecoverableKeyException e) {
			System.out.println("Sei la que erro eh esse.");
			e.printStackTrace();
		}*/ catch (KeyManagementException e) {
			System.out.println("Ah mas dai neh");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Erro ao conectar na url");
			e.printStackTrace();
		}
	}
	
	private static SSLSocketFactory createTrustAllSslSocketFactory() throws Exception {
        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, byPassTrustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }
	
	private void setarProxy() {
		String urlProxy = "proxy.datacoper.com.br";
		int portProxy = 3128;
		
		System.setProperty("https.proxyHost", urlProxy);
        System.setProperty("https.proxyPort", String.valueOf(portProxy));
        System.setProperty("http.proxyHost", urlProxy);
        System.setProperty("http.proxyPort", String.valueOf(portProxy));
		Authenticator.setDefault(new ProxyAuth("eduardo.silva", "7394Ed1%5"));
	}
	
	private void execute2() throws Exception {
		
		System.out.println("Conectando");
		URL url = new URL(urlStr);
		setarProxy();
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(urlProxy, portProxy));
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection(); //(proxy)
		
		con.connect();
		
		System.out.println("Aqui1");
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		System.out.println("Aqui2");
		
		String input;
		
		   while ((input = br.readLine()) != null){
		      System.out.println(input);
		   }
		   br.close();
		
		System.out.println("Fim");
	}
	
	private Certificate importCert() throws Exception {
		KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null);
		InputStream fis = new FileInputStream(new File("C:\\Users\\Beserra\\Desktop\\cert\\" + nomeCertPublico));
		BufferedInputStream bis = new BufferedInputStream(fis);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		Certificate cert = null;
		while (bis.available() > 0) {
		    cert = cf.generateCertificate(bis);
		    trustStore.setCertificateEntry("fiddler"+bis.available(), cert);
		}
		
		return cert;
	}
	
}
