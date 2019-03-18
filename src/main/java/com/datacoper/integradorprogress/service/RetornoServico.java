package com.datacoper.integradorprogress.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RetornoServico implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String descricao;
	
	private Date dataHora;
	
	private int  status;
	
	private List<ParametroRetornoServico> parametros;
	
	public List<ParametroRetornoServico> getParametros() {
		return parametros != null ? parametros :  new ArrayList<ParametroRetornoServico>();
	}
	public void setParametros(List<ParametroRetornoServico> parametros) {
		this.parametros = parametros;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void addParametro(String nome, String conteudo) {
		ParametroRetornoServico p = new ParametroRetornoServico();
		p.setRetorno(this);
		p.setNome(nome);
		p.setConteudo(conteudo);
		if(this.parametros == null)
			this.parametros = new ArrayList<>();
		this.parametros.add(p);
	}
	
	public void removeParametro(ParametroRetornoServico p) {
		parametros.remove(p);
	}
}
