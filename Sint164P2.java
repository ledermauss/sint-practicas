//TODO: arreglar lo de "int", en la cadena de cosas recorridas, al volver hacia atrás
//TODO: arreglar que al volver hacia atrás desde la consulta 2, aparece la cadena dos veces
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;


public class Sint164P2 extends HttpServlet {
		Vista vista = new Vista();
		ArrayList<String> ListaFases = new ArrayList<String>();
		ArrayList<String> valoresConsultas = new ArrayList<String>();
		XML_DTD_Parser parseador;
		private XPath xpath;
		private String  fichinicial= "http://clave.det.uvigo.es:8080/~sintprof/15-16/p2j/tvml-20-12-2004.xml";
		HashMap<String,Document> mapaDocs = new HashMap<String,Document>();
		ArrayList<String> TVMLleidos = new ArrayList<String>();
		ArrayList<String> listaErrores = new ArrayList<String>();
		
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{	
		parseador = new XML_DTD_Parser();
		xpath = XPathFactory.newInstance().newXPath();
		try{
			getArbolInicial(fichinicial);
		}catch(XPathExpressionException e){
		}
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		vista.replyConsultaInicial(out, listaErrores);
	}
	

	public void getArbolInicial(String fichero) throws XPathExpressionException{
		Document documento = documento = parseador.getDocument(fichero);
		XML_DTD_ErrorHandler gestorErrores = parseador.getErrorHandler();
		if(gestorErrores.huboError()){
			listaErrores.add("Error en: " + fichero + ": " + gestorErrores.getMensaje());
			return;
		}
		String id = (String) xpath.evaluate("/Programacion/Fecha", documento.getDocumentElement(), XPathConstants.STRING);
		mapaDocs.put(id, documento);
		//Vamos con conseguir los xml
		//esto es para un artista
		TVMLleidos.add(fichero);
		NodeList ArchivosTVML = (NodeList) xpath.evaluate("/Programacion/Canal/Programa/Intervalo/OtraEmision/UrlTVML", 
				documento.getDocumentElement(), XPathConstants.NODESET);
		if(ArchivosTVML.getLength() != 0){
			for(int i = 0; i < ArchivosTVML.getLength(); i++){
				String TVML = ArchivosTVML.item(i).getTextContent().trim();
				if (!TVML.startsWith("http"))
					TVML = "http://clave.det.uvigo.es:8080/~sintprof/15-16/p2j/" + TVML;
				if(TVMLleidos.contains(TVML)){
					continue;
				}else{
					fichero = TVML;
					System.out.println("Se parseará y añadirá el fichero " + fichero);
					getArbolInicial(fichero);
				}
			}
		}
	}
	
	public String ArrayListToHtml(ArrayList<String> Lista){
		String html = "";
		for(String s : Lista)
			html += s + ", "; 
		return html;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		//Trampa para atacar: enviar atras con todo unchecked en la segunda pantalla (out of bounds)
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String select = req.getParameter("select");
		System.out.println("El vaor elegido es:" + select);
		String nextfase = req.getParameter("nextfase");
		System.out.println("La nextfase debería ser:" + nextfase);
		if(select != null){//Habrá que cambiarlo cuando reestructure todo
			valoresConsultas.add(select);
		}
		System.out.println("El append funcionó: " + valoresConsultas.size());
		for(String s : valoresConsultas){
			System.out.println(s);
		}
		if(req.getParameter("accion").equals("Atras")){
			//decimos que la siguiente fase es la anterior
			ListaFases.remove(ListaFases.size() - 1);
			valoresConsultas.remove(valoresConsultas.size() -1);
			if(!ListaFases.isEmpty())
				ListaFases.remove(ListaFases.size() - 1);
			nextfase = req.getParameter("prevfase");
		}else if(req.getParameter("accion").equals("Inicio")){
			ListaFases.clear();
			valoresConsultas.clear();
			nextfase = "0";
		}
		try{
			if(nextfase.equals("0")){
				vista.replyConsultaInicial(out, listaErrores);
			}else
				if(nextfase.equals("11")){
				ListaFases.add("Consulta 1");
				String fasesRecorridas = ArrayListToHtml(ListaFases);
				vista.replyConsulta1(res, out, ArrayListToHtml(ListaFases), mapaDocs);
			}else if(nextfase.equals("12")){
				ListaFases.add("Fecha  = " + valoresConsultas.get(valoresConsultas.size()-1));
				ArrayList<String> albumes = getPeliculasPorCanal(exprXpath(valoresConsultas, nextfase),
						valoresConsultas.get(0)); //y mapaDocs (para el controlador si usara MVC)
				vista.replyCanalesPorFecha(res, out, ArrayListToHtml(ListaFases), albumes);
			}else if(nextfase.equals("13")){
				ListaFases.add("Canal = " + valoresConsultas.get(valoresConsultas.size()-1));
				ArrayList<String>  canciones = getPeliculasPorCanal(exprXpath(valoresConsultas, nextfase),
                                                valoresConsultas.get(0));
				vista.replyPelis(res, out, ArrayListToHtml(ListaFases), canciones);

                            }else if(nextfase.equals("21")){
                                ListaFases.add("Consulta 2");
                                ArrayList<String> categorias = getConsulta2();
                                vista.replyConsulta2(res, out, ArrayListToHtml(ListaFases), categorias);
			}else if(nextfase.equals("22")){
				ListaFases.add("Año = " + valoresConsultas.get(valoresConsultas.size()-1));
				ArrayList<String> albumes  = getAlbumesPorAnho(exprXpath(valoresConsultas, nextfase));
				vista.replyAlbumesPorYear(res, out, ArrayListToHtml(ListaFases), albumes);
			}else if(nextfase.equals("23")){
				ListaFases.add("Album = " + valoresConsultas.get(valoresConsultas.size()-1));
				ArrayList<String> estilos  = getEstilos(exprXpath(valoresConsultas, nextfase));
				vista.replyEstilos(res, out, ArrayListToHtml(ListaFases), estilos);
			}else if(nextfase.equals("24")){
				ListaFases.add("Estilo = " + valoresConsultas.get(valoresConsultas.size()-1));
				int numeroCanciones = getNumeroCanciones(exprXpath(valoresConsultas, nextfase));
				vista.replyNumeroCanciones(res, out, ArrayListToHtml(ListaFases), numeroCanciones);
			}
		}catch(XPathExpressionException xe){
			xe.printStackTrace();
		}
	}
	
	public String exprXpath(ArrayList<String> consultasprevias, String nextfase){
		int faseSig = Integer.parseInt(nextfase);	
		String penultimaConsulta = "";
		switch(faseSig){
		case 12:
			return "/Programacion/Canal/NombreCanal"; 
		case 13:
			if(consultasprevias.get(1).equals("todos"))
				return "/Programacion/Canal/Programa[Categoria='Cine']";
			else
				return "/Programacion/Canal[NombreCanal='" + consultasprevias.get(1) + "']/Programa[Categoria='Cine']";
		case 22:
			if(consultasprevias.get(0).equals("todos"))
				return "/Interprete/Album/NombreA";
			else 
				return "Interprete/Album[Año='" + consultasprevias.get(0) + "']/NombreA";	
		case 23:
		case 24:
			if(consultasprevias.get(0).equals("todos") && consultasprevias.get(1).equals("todos")){
			//todos los estilos
				penultimaConsulta = "/Interprete/Album/Cancion";
			}else if(!consultasprevias.get(0).equals("todos") && consultasprevias.get(1).equals("todos")){
			//Año específico, todos los albumes 
				penultimaConsulta = "Interprete/Album[Año='" + consultasprevias.get(0) +	"']/Cancion";
			}else if(consultasprevias.get(0).equals("todos") && !consultasprevias.get(1).equals("todos")){
			//Todos los años, album específico
				penultimaConsulta = "Interprete/Album[NombreA='" + consultasprevias.get(1) +	"']/Cancion";
			}else {
			//album y año específico
				penultimaConsulta = "Interprete/Album[Año='" + consultasprevias.get(0) +
					"' and NombreA='" + consultasprevias.get(1) +  "']/Cancion";
			}
		}
		if(faseSig == 23){
			return penultimaConsulta + "/@estilo";
		}else if (faseSig == 24){
				if(consultasprevias.get(2).equals("todos"))
					return penultimaConsulta;
			return penultimaConsulta + "[@estilo='" + consultasprevias.get(2) + "']";
		}
			
		return null;
	}
	
	
	public ArrayList<Node> getArrayListAllFechas(){
		//Probar a quitar el getDocumentElement (y entender por qué pasa lo que sea que pase)
		ArrayList<Node> interpretes = new ArrayList<Node>();
		for(String key: mapaDocs.keySet()){
			interpretes.add(mapaDocs.get(key).getDocumentElement());
		}
		return interpretes;
	}

	//Unificable con getCanciones por album
	public ArrayList<String> getCanalesPorFecha(String expr, String consulta1) throws XPathExpressionException{
		ArrayList<Node> fechas = new ArrayList<Node>();
		ArrayList<String> canalesNombre = new ArrayList<String>();
		if(consulta1.equals("todos"))
			fechas = getArrayListAllFechas();
		else
			fechas.add(mapaDocs.get(consulta1));
		for(Node fecha: fechas){
			NodeList canales = (NodeList) xpath.evaluate(expr, fecha, XPathConstants.NODESET);
			for (int i = 0; i < canales.getLength(); i++) {
				String nombreCanal = canales.item(i).getTextContent();
                                if(!canalesNombre.contains(nombreCanal))
                                        canalesNombre.add(nombreCanal);
			}
		}
		return canalesNombre;
	}

	public ArrayList<String> getPeliculasPorCanal(String expr, String consulta1) throws XPathExpressionException {
		ArrayList<Node> fechas = new ArrayList<Node>();
		ArrayList<String> pelisNombre = new ArrayList<String>();
		if(consulta1.equals("todos"))
			fechas = getArrayListAllFechas();
		else
			fechas.add(mapaDocs.get(consulta1));
		for(Node fecha: fechas){
			NodeList peliculas = (NodeList) xpath.evaluate(expr, fecha, XPathConstants.NODESET);
			for (int i = 0; i < peliculas.getLength(); i++) {
				String pelicula = peliculas.item(i).getTextContent();
				pelisNombre.add(pelicula);
			}
		}
		return pelisNombre;
	}
        
	//Unificable con los dos siguientes
	public ArrayList<String> getConsulta2() throws XPathExpressionException {
		ArrayList<String> categoriasNombre = new ArrayList<String>();
		for (String fecha: mapaDocs.keySet()){
			Node prog = mapaDocs.get(fecha);
			NodeList categorias = (NodeList) xpath.evaluate("/Programacion/Canal/Programa/Categoria", prog, 
                                        XPathConstants.NODESET);
			for(int i= 0; i < categorias.getLength();i++){
				String categoria = categorias.item(i).getTextContent();
				if(!categoriasNombre.contains(categoria))
					categoriasNombre.add(categoria);
			}
		}
		return categoriasNombre;
	}
	
	//Hay que ordenar por año los albumes!! Talvez sí sea buena idea mandarlos como Hashmap y que lo recupere
	public ArrayList<String> getAlbumesPorAnho(String expr) throws XPathExpressionException{
		ArrayList<String> albumes = new ArrayList<String>();
		for(String key: mapaDocs.keySet()){
			Node interprete = mapaDocs.get(key);
			//Si sé que solo hay un album por año, podría sacar solo Node o String y ahorrar el for
			NodeList albumesNodos = (NodeList) xpath.evaluate(expr, interprete, XPathConstants.NODESET);
			for(int i = 0; i < albumesNodos.getLength(); i++){
				Node album = albumesNodos.item(i);
				albumes.add(album.getTextContent());
			}
		}
		return albumes;
	}
	
	public ArrayList<String> getEstilos(String expr) throws XPathExpressionException{
		ArrayList<String> estilos = new ArrayList<String>();
		for(String key: mapaDocs.keySet()){
			Node interprete = mapaDocs.get(key);
			NodeList estiloNodos = (NodeList) xpath.evaluate(expr, interprete, XPathConstants.NODESET);
			for(int i = 0; i < estiloNodos.getLength(); i++){
				String estilo = estiloNodos.item(i).getTextContent();
				if(!estilos.contains(estilo))
					estilos.add(estilo);
			}
		}
		return estilos;
	}
	
	public int getNumeroCanciones(String expr) throws XPathExpressionException{
		int numeroCanciones = 0;
		for(String key: mapaDocs.keySet()){
			Node interprete = mapaDocs.get(key);
			NodeList canciones = (NodeList) xpath.evaluate(expr, interprete, XPathConstants.NODESET);
			//al no haber canciones repetidas, no tengo que comprobar nada
			numeroCanciones += canciones.getLength();
		}
		return numeroCanciones;
	}
}

