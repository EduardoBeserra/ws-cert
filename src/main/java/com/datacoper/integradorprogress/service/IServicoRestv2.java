package com.datacoper.integradorprogress.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.ws.rs.Produces;

@Produces(value = "application/xml;charset=UTF-8")
@WebService(serviceName = "ServicoRestv2")
@SOAPBinding(style = Style.RPC)
public interface IServicoRestv2 {

	@WebMethod(operationName = "executeGet")
	public String executeGet(String dados);
	
	@WebMethod(operationName = "executePost")
	public String executePost(String dados);
}
