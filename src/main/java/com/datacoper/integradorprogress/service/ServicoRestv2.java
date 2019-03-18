package com.datacoper.integradorprogress.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.json.JSONObject;
import org.json.XML;

import com.datacoper.integradorprogress.cert.GerenciadorCertificados;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@WebService(endpointInterface = "com.datacoper.integradorprogress.service.IServicoRestV2")
public class ServicoRestv2 implements IServicoRestv2 {
	
	public static void main(String[] args) throws Exception {
		ServicoRestv2 sr = new ServicoRestv2();
		/*
		File f = new File("C:\\Users\\Beserra\\Desktop\\testeGetToken.txt");
		String dados = Files.toString(f, Charsets.UTF_8);
		sr.executePost(dados);
		*/
		sr.execute3();
	}
	
	private void execute3() throws Exception {
		String url = "https://hom.api.sgi.ms.gov.br/d0100/esaniagrointegracaoagrotoxico/v1/swagger/index.html";
		GerenciadorCertificados gc = new GerenciadorCertificados();
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.PROXY_URI, "proxy.datacoper.com.br:3128");
		config.property(ClientProperties.PROXY_USERNAME, "eduardo.silva");
		config.property(ClientProperties.PROXY_PASSWORD, "7394Ed1$4");
		Client c = ClientBuilder.newBuilder().withConfig(config).sslContext(gc.getContext()).build();
		WebTarget wt = c.target(url);

		Builder builder = wt.request()
				.header("cache-control", "n-cache")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				;
		Response r = builder.get();
		String resposta = (String) r.getEntity();
		
		System.out.println(resposta);
	}

	private void execute2() throws Exception {
		String url = "http://hom.id.ms.gov.br/auth/realms/ms/protocol/openid-connect/token";
		GerenciadorCertificados gc = new GerenciadorCertificados();
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.PROXY_URI, "proxy.datacoper.com.br:3128");
		config.property(ClientProperties.PROXY_USERNAME, "eduardo.silva");
		config.property(ClientProperties.PROXY_PASSWORD, "7394Ed1$4");
		Client c = ClientBuilder.newBuilder().withConfig(config).sslContext(gc.getContext()).build();
		WebTarget wt = c.target(url);

		Builder builder = wt.request()
				.header("cache-control", "n-cache")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				;
		Response r = builder.post(Entity.text(
				"grant_type=client_credentials&client_id=integracaorevenda"
				+ "&client_secret=e4edf2ef-b9ec-45f1-84d5-edd2e9d0b291")
		);
		String resposta = (String) r.getEntity();
		
		System.out.println(resposta);
	}

	private void execute() throws Exception {
		String urlStr = "http://hom.id.ms.gov.br/auth/realms/ms/protocol/openid-connect/token";
		URL url = new URL(urlStr);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.datacoper.com.br", 3128));
		HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
		con.setRequestMethod("POST");
		con.addRequestProperty("cache-control", "no-cache");
		con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setDoOutput(true);
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		dos.writeUTF("grant_type=client_credentials&client_id=integracaorevenda&client_secret=e4edf2ef-b9ec-45f1-84d5-edd2e9d0b291");
		dos.close();
		con.setReadTimeout(15*1000);
		
		con.connect();
		System.out.println(con.getResponseCode());
		System.out.println(con.getResponseMessage());
		
		String retorno = lerRetorno(con);
		con.disconnect();
		System.out.println(retorno);
	}
	
	
	@Override
	public String executeGet(String dados) {
		JsonObject obj = JsonUtil.strToObject(dados);
		try {
			JsonUtil.validarParametrosObrigatorios(obj, "url");
			HttpURLConnection con = getConnection(obj);
			con.setRequestMethod("GET");
			con.connect();
			String retorno = lerRetorno(con);
			con.disconnect();
			System.out.println(retorno);
			return retorno;
			
		} catch(Exception e) {
			e.printStackTrace();
			return tratarRetornoErro(obj, e.getMessage());
		}
	}
	
	@Override
	public String executePost(String dados) {
		JsonObject obj = JsonUtil.strToObject(dados);
		try {
			JsonUtil.validarParametrosObrigatorios(obj, "url");
			HttpURLConnection con = getConnection(obj);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
			escreverHeader(con, obj);
			escreverBody(con, obj);
			
			con.connect();
			String retorno = lerRetorno(con);
			con.disconnect();
			
			System.out.println(retorno);
			return retorno;
			
		} catch(Exception e) {
			e.printStackTrace();
			return tratarRetornoErro(obj, e.getMessage());
		}
	}
	
	private HttpURLConnection getConnection(JsonObject objDados) throws Exception {
		URL url = new URL(JsonUtil.getString(objDados, "url", ""));
		HttpURLConnection con;
		
		if(JsonUtil.getBoolean(objDados, "proxy", false)) { 
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.datacoper.com.br", 3128));
			con = (HttpURLConnection) url.openConnection(proxy);
		} else
			con = (HttpURLConnection) url.openConnection();
		
		if(url.getProtocol().toLowerCase().equals("https") && JsonUtil.getBoolean(objDados, "cert", false)) {
			HttpsURLConnection httpurlc = (HttpsURLConnection) con;
			GerenciadorCertificados gc = new GerenciadorCertificados();
			httpurlc.setSSLSocketFactory(gc.getSSLFactory());
			httpurlc.addRequestProperty("X-Client-Cert", gc.getCertificadoEnconded());
			con = httpurlc;
		}
		return con;
	}
	
	private void escreverHeader(HttpURLConnection con, JsonObject obj) {
		JsonElement headers = obj.get("headers");
		if(headers instanceof JsonArray) {
			JsonArray jaHeaders = headers.getAsJsonArray();
			for(int i = 0; i < jaHeaders.size(); i++) {
				JsonObject header = jaHeaders.get(i).getAsJsonObject();
				for(Entry<String, JsonElement> entrySet : header.entrySet()) {
					con.addRequestProperty(entrySet.getKey(), entrySet.getValue().getAsString());
				}
			}
		}
	}
	
	private void escreverBody(HttpURLConnection con, JsonObject obj) throws IOException {
		con.setDoOutput(true);
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		dos.writeUTF(JsonUtil.getString(obj, "body", ""));
		dos.close();
	}
	
	
	private String tratarRetornoErro(JsonObject obj, String mensagem) {
		String retorno = JsonUtil.gerarRetornoErro(1, mensagem);
		if(JsonUtil.getString(obj, "dataType", "").equals(MediaType.APPLICATION_XML)) {
			return XML.toString(new JSONObject(retorno), "root");
		} else
			return retorno;
	}
	
	private String lerRetorno(HttpURLConnection con) {
		BufferedReader br = null;
		StringBuffer dados = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")));
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
