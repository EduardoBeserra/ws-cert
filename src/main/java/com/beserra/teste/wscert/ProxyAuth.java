package com.beserra.teste.wscert;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuth extends Authenticator {
    private PasswordAuthentication auth;

    ProxyAuth(String user, String password) {
        auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return auth;
    }
}
