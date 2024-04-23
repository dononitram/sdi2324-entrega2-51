package com.uniovi.sdi2223entrega2test.51.pageobjects;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

public class PO_Properties {

	private static String Path;
	
	public static final int SPANISH = 0;
	public static final int ENGLISH = 1;

	public static final int GERMAN = 2;

	static final Locale[] languages = new Locale[] {new Locale("ES"), new Locale("EN"),
			new Locale("DE"), new Locale("FR")};

	public PO_Properties(String Path) //throws FileNotFoundException, IOException 
	{
		PO_Properties.Path = Path;
	}

	/**
	 * Método que devuelve el valor de una propiedad de un fichero de propiedades
	 * @param prop propiedad a retornar
	 * @param locale el índice del array de idiomas
	 * @return el valor de la propiedad
	 */
    public String getString(String prop, int locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(Path, languages[locale]);
		String value = bundle.getString(prop);
		String result;
		//result = new String(value.getBytes(StandardCharsets.ISO_8859_1),  StandardCharsets.UTF_8);
		result = new String(value.getBytes(StandardCharsets.UTF_8),  StandardCharsets.UTF_8);
		return result;
	}

	
}
