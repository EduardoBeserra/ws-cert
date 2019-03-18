package com.datacoper.integradorprogress.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class GerenciadorCertificados {

	private SSLSocketFactory sslFactory;
	private SSLContext context;
	private KeyStore ksPrivado;
	private KeyStore ksPublico;
	
	public GerenciadorCertificados() throws Exception {
		carregar();
	}
	
	public SSLSocketFactory getSSLFactory() {
		return sslFactory;
	}
	
	public SSLContext getContext() {
		return context;
	}
	
	public String getCertificadoEnconded() throws Exception {
		Certificate certificate = getCertificate(ksPrivado);
		byte[] encodedCert = certificate.getEncoded();
		String encodedStringCert = Base64.getEncoder().encodeToString(
				new String(encodedCert).getBytes(StandardCharsets.UTF_8)); 
		return encodedStringCert;
	}
	
	private void carregar() throws Exception {
		String senhaPrivada = "datacoper";
		String senhaPublica = "12345678";
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		ksPrivado = getKeyStore(ConfigCertificado.getInstance().getCertificadoPrivado(), senhaPrivada);
		ksPublico = getKeyStore(ConfigCertificado.getInstance().getCertificadoPublico(), senhaPublica);
		if(ksPrivado == null || ksPublico == null)
			throw new RuntimeException("Não foi possivel carregar os certificados.");
		kmf.init(ksPrivado, senhaPrivada.toCharArray());
		
		TrustManager[] tm = getTrustManagers(ksPublico);
		if(tm == null)
			throw new RuntimeException("Não foi possivel criar TrustManager");
		
		context = SSLContext.getInstance("TLSv1.2");
		context.init(kmf.getKeyManagers(), tm, null);
		
		sslFactory = context.getSocketFactory();
	}
	
	private KeyStore getKeyStore(File certificado, String senha) {
		KeyStore ks;
		try {
			ks = KeyStore.getInstance("PKCS12");
			
			InputStream is = new FileInputStream(certificado);
			ks.load(is, senha.toCharArray());
			is.close();
			return ks;
		} catch (KeyStoreException e) {
			System.out.println("Erro ao criar KeyStore. Arquivo: " + certificado.getAbsolutePath());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Erro ao ler certificado.");
			System.out.println("Arquivo nao encontrado. Arquivo: " + certificado.getAbsolutePath());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Erro ao ler certificado. Arquivo: " + certificado.getAbsolutePath());
			System.out.println("Algoritmo inexistente.");
			e.printStackTrace();
		} catch (CertificateException e) {
			System.out.println("Erro ao carregar o certificado. Arquivo: " + certificado.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Arquivo: " + certificado.getAbsolutePath());
			e.printStackTrace();
		}
		return null;
	}
	
	private TrustManager[] getTrustManagers(KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory trustManagerFactory;
		trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(ks);
		return trustManagerFactory.getTrustManagers();
	}
	
	private Certificate getCertificate(KeyStore ks) throws KeyStoreException {
		String alias = "";
		Enumeration<String> aliases = ks.aliases();
		while(aliases.hasMoreElements())
			alias = aliases.nextElement();
		
		return ks.getCertificate(alias);
	}
}
