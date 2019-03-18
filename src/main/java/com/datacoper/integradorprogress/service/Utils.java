package com.datacoper.integradorprogress.service;

import java.io.Closeable;
import java.io.IOException;

public class Utils {

	public static void close(Closeable c) {
		try {
            if (c != null) {
                c.close();
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	
	public static String substituir(String str) {
		return ((String)str)
				.replaceAll("[ãâàáä]",	"a")
				.replaceAll("[êèéë&]",	"e")
				.replaceAll("[îìíï]",	"i")
				.replaceAll("[õôòóö]",	"o")
				.replaceAll("[ûúùü]",	"u")
				.replaceAll("[ÃÂÀÁÄ]",	"A")
				.replaceAll("[ÊÈÉË]",	"E")
				.replaceAll("[ÎÌÍÏ]",	"I")
				.replaceAll("[ÕÔÒÓÖ]",	"O")
				.replaceAll("[ÛÙÚÜ]",	"U")
				.replace('ç',	'c')
				.replace('Ç',	'C')
				.replace('ñ',	'n')
				.replace('Ñ',	'N');
	}
	
}
