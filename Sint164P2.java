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
		String nextfase = req.getParameter("nextfase");
		String thisfase = req.getParameter("thisfase");
		String prevfase = req.getParameter("prevfase");
		System.out.println("SIGUIENTE FASE:" + nextfase);
		System.out.println("VALOR ELEGIDO:" + select);
		System.out.println("CONSULTAS PREVIAS: " + valoresConsultas.size());
		for(String s : valoresConsultas){
			System.out.println(s);
		}
		if(req.getParameter("accion").equals("Atras")){
			//decimos que la siguiente fase es la anterior
                        if(!prevfase.equals("0")){
                                ListaFases.remove(ListaFases.size() - 1);
                                valoresConsultas.remove(valoresConsultas.size() -1);
                        }
			nextfase = prevfase;
                        System.out.println("Alla vamos otra vez: valore de las fases y la nextfase");
                        for(String s : valoresConsultas){
                                System.out.println(s);
                        }
		}else if(req.getParameter("accion").equals("Inicio")){
			ListaFases.clear();
			valoresConsultas.clear();
			nextfase = "0";
		}else if(!thisfase.equals("0")){//se escogió enviar y no era la primera 
			valoresConsultas.add(select);
                        ListaFases.add(select);
		}
		try{
			if(nextfase.equals("0")){
				vista.replyConsultaInicial(out, listaErrores);
			}else
				if(nextfase.equals("11")){
				String fasesRecorridas = ArrayListToHtml(ListaFases);
				vista.replyConsulta1(res, out, ArrayListToHtml(ListaFases), mapaDocs);
			}else if(nextfase.equals("12")){
				ArrayList<String> albumes = getCanalesPorFecha(exprXpath(valoresConsultas, nextfase),
						valoresConsultas.get(0)); //y mapaDocs (para el controlador si usara MVC)
				vista.replyCanalesPorFecha(res, out, ArrayListToHtml(ListaFases), albumes);
			}else if(nextfase.equals("13")){
				ArrayList<String>  canciones = getPeliculasPorCanal(exprXpath(valoresConsultas, nextfase),
                                                valoresConsultas.get(0));
				vista.replyPelis(res, out, ArrayListToHtml(ListaFases), canciones);

                        }else if(nextfase.equals("21")){
                                ArrayList<String> categorias = getConsulta2();
                                vista.replyConsulta2(res, out, ArrayListToHtml(ListaFases), categorias);
			}else if(nextfase.equals("22")){
				ArrayList<String> langs  = getLangsPorPrograma(exprXpath(valoresConsultas, nextfase));
				vista.replyAlbumesPorYear(res, out, ArrayListToHtml(ListaFases), langs);
			}else if(nextfase.equals("23")){
				ArrayList<String> estilos  = getProgramasPorLangs( exprXpath(valoresConsultas, nextfase), valoresConsultas);
				vista.replyEstilos(res, out, ArrayListToHtml(ListaFases), estilos);
			}
		}catch(XPathExpressionException xe){
			xe.printStackTrace();
		}
	}
	
	public String exprXpath(ArrayList<String> consultasprevias, String nextfase){
		int faseSig = Integer.parseInt(nextfase);	
		switch(faseSig){
		case 12:
			return "/Programacion/Canal/NombreCanal"; 
		case 13:
			if(consultasprevias.get(1).equals("todos"))
				return  "/Programacion/Canal/Programa[Categoria='Cine']";
			else
				return "/Programacion/Canal[NombreCanal='" + consultasprevias.get(1) + "']/Programa[Categoria='Cine']";

		case 22:
                case 23: //es lo mismo: saco los canales. En el método de la consulta los iterare para sacar sus lenguajes
                        //o los de sus canales
			if(consultasprevias.get(0).equals("todos")){
				return  "/Programacion/Canal/Programa";
                        }else{ 
				return "/Programacion/Canal/Programa[Categoria='" + consultasprevias.get(0) + "']";	}
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
                                if(!canalesNombre.contains(nombreCanal)){
                                        canalesNombre.add(nombreCanal);
                                        System.out.println(nombreCanal);
                                }
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

		for(Node fecha: fechas) {
			NodeList peliculas = (NodeList) xpath.evaluate(expr, fecha, XPathConstants.NODESET);
			for (int i = 0; i < peliculas.getLength(); i++) {
                                String sinopsis="";
                                String nombrePeli = (String) xpath.evaluate("./NombrePrograma", peliculas.item(i),
                                                XPathConstants.STRING);
                                NodeList hijos = peliculas.item(i).getChildNodes();
                                for (int j=0;j<hijos.getLength();j++){
                                        if(hijos.item(j).getNodeName().equals("#text") && 
                                                        hijos.item(j).getTextContent().trim().equals("")==false){
                                                sinopsis += " "+ hijos.item(j).getTextContent().trim();
                                        }
                                }
				pelisNombre.add(nombrePeli + " - " + sinopsis);
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
	public ArrayList<String> getLangsPorPrograma(String expr) throws XPathExpressionException{
		ArrayList<String> langs = new ArrayList<String>();
		for(String key: mapaDocs.keySet()){
			Node interprete = mapaDocs.get(key);
			//Si sé que solo hay un album por año, podría sacar solo Node o String y ahorrar el for
			NodeList programasCategoria = (NodeList) xpath.evaluate(expr, interprete, XPathConstants.NODESET);
			for(int i = 0; i < programasCategoria.getLength(); i++){
                                //voy programa a programa y saco los idiomas
                                String langsPrograma = ((Element)programasCategoria.item(i)).getAttribute("langs");
                                if(langsPrograma.isEmpty()){
                                        //si no tiene, cojo los del canal
                                        Element padre  = (Element)programasCategoria.item(i).getParentNode();
                                        String langCanal = padre.getAttribute("lang");
                                        if(!langs.contains(langCanal))
                                                langs.add(langCanal);
                                } else{
                                        String[] parts = langsPrograma.split("\\s+");
                                        for(String s : parts)
                                                if(!langs.contains(s))
                                                        langs.add(s);
                                }
			}
		}
		return langs;
	}
	
	public ArrayList<String> getProgramasPorLangs(String expr, ArrayList<String> consultasprevias) throws XPathExpressionException{
		ArrayList<String> progs = new ArrayList<String>();
		for(String key: mapaDocs.keySet()){
			Node interprete = mapaDocs.get(key);
			//Si sé que solo hay un album por año, podría sacar solo Node o String y ahorrar el for
			NodeList programasCategoria = (NodeList) xpath.evaluate(expr, interprete, XPathConstants.NODESET);
			for(int i = 0; i < programasCategoria.getLength(); i++){
                                Node prog = programasCategoria.item(i);
                                //voy programa a programa y saco los idiomas
                                String langsPrograma = ((Element)prog).getAttribute("langs"); 
                                String edadMinima = ((Element)prog).getAttribute("edadminima"); 
                                String nombreProg = (String) xpath.evaluate("./NombrePrograma", prog, XPathConstants.STRING);
                                if(consultasprevias.get(1).equals("todos")){
                                        progs.add(nombreProg +", edad mínima: " + edadMinima + " años" );
                                }else{
                                        //si no tiene langs, el correspondiente es el del canal
                                        if(langsPrograma.isEmpty()){ 
                                                Element padre  = (Element)prog.getParentNode();
                                                langsPrograma = padre.getAttribute("lang");
                                        }
                                        if(langsPrograma.contains(consultasprevias.get(1))){
                                                progs.add(nombreProg +", Apto para mayores de: " + edadMinima + "años" );
                                        }
                                }
			}
		}
		return progs;
	}
	
}

