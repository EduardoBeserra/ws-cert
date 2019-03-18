package com.datacoper.integradorprogress.cert;

import java.io.File;
import java.io.FilenameFilter;

public class ConfigCertificado {

	private static ConfigCertificado cc;
	private String diretorioCertificados;
	
	public static ConfigCertificado getInstance() {
		if(cc == null)
			cc = new ConfigCertificado();
		return cc;
	}
	
	public static void main(String[] args) {
		ConfigCertificado obj = new ConfigCertificado();
		System.out.println(obj.getCertificadoPrivado());
	}
	
	private ConfigCertificado() {
		diretorioCertificados = getDiretorioDefault();
	}
	
	public String getDiretorioDefault() {
		String diretorio = this.getClass().getResource("").getPath().split("com/")[0]
				+ "META-INF/cert/";
		return diretorio;
	}
	
	public String getDiretorioCertificados() {
		if(diretorioCertificados == null)
			diretorioCertificados = getDiretorioDefault();
		return diretorioCertificados;
	}
	
	public File[] getCertificadosPublicos() {
		File diretorio = new File(diretorioCertificados + "publicos/");
		return diretorio.listFiles();
	}
	
	public File[] getCertificadosPrivados() {
		File diretorio = new File(diretorioCertificados + "privados/");
		return diretorio.listFiles();
	}
	
	public File getCertificadoPrivado() {
		File diretorio = new File(diretorioCertificados + "privados/");
		File[] arquivos = diretorio.listFiles(getFilter(".pfx"));
		if(arquivos != null && arquivos.length > 0)
			return arquivos[0];
		else
			return null;
	}
	
	public File getCertificadoPublico() {
		File diretorio = new File(diretorioCertificados + "publicos/");
		File[] arquivos = diretorio.listFiles(getFilter(".pfx"));
		if(arquivos != null && arquivos.length > 0)
			return arquivos[0];
		else
			return null;
	}
	
	private FilenameFilter getFilter(String extensao) {
		return new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".pfx");
			}
		};
	}
}
