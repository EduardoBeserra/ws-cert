package com.beserra.teste.wscert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class App {
	public static void main(String[] args) {
		new App().execute();
	}

	private void execute() {
		try {
			String senha = "12345678";
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("PKCS12");
			
			InputStream is = new FileInputStream(new File("C:\\Users\\Beserra\\Desktop\\keystore_ICP_Brasil.pfx"));
			ks.load(is, senha.toCharArray());
			is.close();
			
			kmf.init(ks, senha.toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
				      TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(ks);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(kmf.getKeyManagers(), trustManagers, new SecureRandom());
			
			SSLSocketFactory ssf = context.getSocketFactory();
			
			System.out.println("Conectando");
			URL url = new URL("https://hom.api.sgi.ms.gov.br/d0100/esaniagrointegracaoagrotoxico/v1/swagger/index.html");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setSSLSocketFactory(ssf);
			con.setRequestMethod("GET");
			
			Certificate certificate = ks.getCertificate("cacerts");
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
		} catch (UnrecoverableKeyException e) {
			System.out.println("Sei la que erro eh esse.");
			e.printStackTrace();
		} catch (KeyManagementException e) {
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
}
