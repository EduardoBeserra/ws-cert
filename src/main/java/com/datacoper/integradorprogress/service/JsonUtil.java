package com.datacoper.integradorprogress.service;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {

	public static JsonObject retornoToJson(RetornoServico retorno) {

		JsonObject retJson = new JsonObject();

		retJson.addProperty("id", retorno.getId());
		retJson.addProperty("descricao", retorno.getDescricao());
		retJson.addProperty("dataHora", retorno.getDataHora().toString());
		retJson.addProperty("status", retorno.getStatus());

		retJson.add("dados", getDados(retorno, null));

		return retJson;
	}

	public static JsonObject retornoDadosToJson(RetornoServico retorno) {
		if (retorno.getParametros().size() > 0)
			return parametroToJson(retorno.getParametros().get(0));
		else {
			JsonObject obj = new JsonObject();
			obj.addProperty("mensagem", retorno.getDescricao());
			return obj;
		}
	}

	public static String jsonToString(JsonObject obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	public static JsonObject strToObject(String str) {
		Gson gson = new Gson();
		return gson.fromJson(str, JsonObject.class);
	}

	private static JsonElement getDados(RetornoServico retorno, ParametroRetornoServico parametroSuperior) {

		JsonArray dados = new JsonArray();
		if (retorno.getParametros() != null) {
			for (ParametroRetornoServico parametro : retorno.getParametros()) {

				if (parametro.getParametroSuperior() == parametroSuperior) {
					if (parametro.getNome().equals("registro")) {
						dados.add(parametrosToJson(parametro
								.getParametrosFilhos()));
					} else {
						dados.add(parametroToJson(parametro));
					}
				}
			}
		}
		return dados;
	}

	public static JsonObject parametroToJson(ParametroRetornoServico parametro) {
		JsonObject obj = new JsonObject();
		try {
			Gson gson = new Gson();
			JsonObject dados = gson.fromJson(parametro.getConteudo(),
					JsonObject.class);
			obj.add(parametro.getNome(), dados);
		} catch (Exception e) {
			obj.addProperty(parametro.getNome(), parametro.getConteudo());
		}
		return obj;
	}

	public static JsonObject parametrosToJson(
			List<ParametroRetornoServico> lista) {
		JsonObject obj = new JsonObject();

		for (ParametroRetornoServico p : lista)
			obj.addProperty(p.getNome(), p.getConteudo());

		return obj;
	}

	public static String getDados(JsonElement dados, boolean propertyDados) {
		if (!propertyDados) {
			if(dados.isJsonObject()) {
				JsonObject dadosObj = dados.getAsJsonObject();
				if(dadosObj.get("dados") != null) {
					dados = dadosObj.get("dados");
					dadosObj = dados.getAsJsonObject();
					if(dadosObj.get("registros") != null)
						dados = dadosObj.get("registros");
				}
			}
		}
		return dados.toString();
	}
	
	public static String gerarRetornoErro(int codigo, String mensagem) {
		JsonObject obj = new JsonObject();
		obj.addProperty("status", "Erro");
		obj.addProperty("codigo", codigo);
		obj.addProperty("msg", mensagem);
		return obj.toString();
	}
	
	public static String getString(JsonObject obj, String campo, String valorDefault) {
		String value = (String) getInfo(obj, campo, "STRING");
		if (value == null)
			value = valorDefault;
		return value;
	}
	
	public static Integer getInteger(JsonObject obj, String campo) {
		return (Integer) getInfo(obj, campo, "INTEGER");
	}
	
	public static Boolean getBoolean(JsonObject obj, String campo, Boolean valorDefault) {
		Object retorno = getInfo(obj, campo, "BOOLEAN");
		if(retorno == null)
			return valorDefault;
		return (Boolean) retorno;
	}
	
	public static Object getInfo(JsonObject obj, String campo, String tipo) {
		String[] campos = campo.split("/");
		JsonObject aux = obj;
		Object valor = null;
		for(int i = 0; i < campos.length; i++) {
			if(i < campos.length - 1) {
				if (aux.get(campos[i].trim()) != null)
					aux = aux.get(campos[i].trim()).getAsJsonObject();
				else
					if(tipo == null)
						return new Boolean(false);
					else
						return null;
			} else { 
				if (aux.get(campos[i].trim()) != null) {
					if (aux.get(campos[i].trim()) instanceof JsonObject)
						valor = aux.get(campos[i].trim()).toString();
					else {
						if(tipo == null)
							valor = new Boolean((aux.get(campos[i]) != null));
						else if(tipo.toUpperCase().equals("STRING"))
							valor = aux.get(campos[i].trim()).getAsString();
						else if(tipo.toUpperCase().equals("INTEGER"))
							valor = new Integer(aux.get(campos[i].trim()).getAsInt());
						else if(tipo.toUpperCase().equals("BOOLEAN"))
							valor = new Boolean(aux.get(campos[i].trim()).getAsBoolean());
					}
				} else
					if(tipo == null)
						return new Boolean(false);
					else
						return null;
			}
		}
		return valor;
	}

	public static void validarParametrosObrigatorios(JsonObject obj, String campos) {
		for(String campo : campos.split(",")) {
			if (!(Boolean)JsonUtil.getInfo(obj, campo.trim(), null))
				throw new RuntimeException("Parametro [" + campo + "] e obrigatorio.");
		}
	}
}
