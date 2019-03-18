package com.datacoper.integradorprogress.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParametroRetornoServico implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private RetornoServico retorno;
	
	private String nome;
	
	private String conteudo;
	
	private ParametroRetornoServico parametroSuperior;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RetornoServico getRetorno() {
		return retorno;
	}
	public void setRetorno(RetornoServico retorno) {
		this.retorno = retorno;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public ParametroRetornoServico getParametroSuperior() {
		return parametroSuperior;
	}
	public void setParametroSuperior(ParametroRetornoServico parametroSuperior) {
		this.parametroSuperior = parametroSuperior;
	}
	
	public List<ParametroRetornoServico> getParametrosFilhos() {
		List<ParametroRetornoServico> listRetorno = new ArrayList<>();
		
		for(ParametroRetornoServico p : retorno.getParametros()) {
			if(p.getParametroSuperior().equals(this))
				listRetorno.add(p);
		}
		
		return listRetorno;
	}
}
