package com.datacoper.integradorprogress.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.datacoper.integradorprogress.cert.GerenciadorCertificados;

public class TesteServico {

	public static void main(String[] args) throws Exception {
		TesteServico ts = new TesteServico();
		ts.testeGetIndex(
				"https://hom.api.sgi.ms.gov.br/d0100/esaniagrointegracaoagrotoxico/v1/swagger/index.html"
		);
	}
	
	public String testeGetIndex(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.datacoper.com.br", 3128));
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection(proxy);
		
		GerenciadorCertificados gc = new GerenciadorCertificados();
		con.setSSLSocketFactory(gc.getSSLFactory());
		
		con.setRequestMethod("GET");
		con.addRequestProperty("X-Client-Cert", gc.getCertificadoEnconded());
		
		con.connect();
		
		String dados = lerRetorno(con);
		System.out.println(dados);
		return dados;
	}
	
	
	
	
	private String lerRetorno(HttpsURLConnection con) {
		BufferedReader br = null;
		StringBuffer dados = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String input;
			while ((input = br.readLine()) != null)
				dados.append(input);
			Utils.close(br);
			return dados.toString();
		} catch (IOException e) {
			System.out.println("Erro ao ler os dados.");
			e.printStackTrace();
		}
		return "";
	}
	
	
}
