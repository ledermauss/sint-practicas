import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import java.lang.IllegalArgumentException;
import java.io.IOException;
import javax.xml.xpath.*;
import java.net.URL;

public class XML_DTD_Parser {
	private Document doc;
	private XML_DTD_ErrorHandler errorhandlerDTD;
	private String localizacionDTD = "http://localhost:8080/";

	
	public  Document getDocument(String archivo) {
		//se crea la "factoría de parsers
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   	dbf.setValidating(true);
		//se crea el parser concreto
	   	DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch(ParserConfigurationException se){
			se.printStackTrace();	
		}
		//clase gestora errores
		errorhandlerDTD = new XML_DTD_ErrorHandler();
	   	db.setErrorHandler(errorhandlerDTD);
	   	//Leemos y generamos el arbol dom (doc) de un documento
		try{
			doc = db.parse(new URL(archivo).openStream(), localizacionDTD);
			//TODO: añadir un catch para file not found exception?
		} catch(SAXException se){
			se.printStackTrace();
		} catch(IOException se){
			se.printStackTrace();
		} catch(IllegalArgumentException se){
			se.printStackTrace();
		}
		
		String tipo = doc.getDoctype().getName();
	   	System.out.println("El tipo de documento es: "+tipo);
		Element raiz = doc.getDocumentElement();
		System.out.println("El elemento raíz es: " + raiz.getTagName()); 
		return doc;
	}	
	
	public XML_DTD_ErrorHandler getErrorHandler(){
		return errorhandlerDTD;
	}
	
	
}

class XML_DTD_ErrorHandler extends DefaultHandler {
	private boolean flagError; 
	private String mensaje;
	public XML_DTD_ErrorHandler () {
		flagError = false;
		mensaje = "";
	}
	//nombre de fichero no válido
	public void warning(SAXParseException spe) {
		System.out.println("Warning: "+spe.toString());
		flagError = true;
		mensaje = spe.toString();
	}
	//documento mal formateado
	public void error(SAXParseException spe) {
		System.out.println("Error: "+spe.toString());
		flagError = true;
		mensaje = spe.toString();
	}
	public void fatalError(SAXParseException spe) { 
		System.out.println("Fatal Error: "+spe.toString());
		flagError = true;
		mensaje = spe.toString();
	} 
	public boolean huboError(){
		return flagError;
	}
	public String getMensaje(){
		return mensaje;
	}
}
