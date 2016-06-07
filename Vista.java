import java.io.*;
import javax.xml.xpath.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.w3c.dom.*;

//TODO: si ho hay nada, imprimir "error, no hay nada que mostrar
class Vista {
	private XPath xpath;
	
	public Vista(){
		xpath = XPathFactory.newInstance().newXPath();
	}

	//para qué el httpservlet response?
	public void replyConsultaInicial(PrintWriter out, ArrayList<String> errores){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Practica 2</title>");
		out.println("<link rel='stylesheet' href='iml.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form name='form' id='form' method='POST'><table id='tabla'>");
		for(String err: errores){
			out.println("<tr><td><u>" + err + "</u></td></tr>");
		}
		out.println("<tr><td><input type=\"radio\" name=\"nextfase\" value='11' checked=\"checked\" /> Consulta 1</td></tr>"
				+ "<tr><td><input type=\"radio\" name=\"nextfase\" value=\"21\" /> Consulta 2</td></tr>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"0\"/>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Enviar\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void replyConsulta1(HttpServletResponse res, PrintWriter out, String fasesRecorridas,
			HashMap<String, Document> fechas) throws XPathExpressionException{ 
		printCabeceraHtml(out, fasesRecorridas);
		TreeMap<String, Node> mapaOrdenado = new TreeMap<String, Node>();
		mapaOrdenado.putAll(fechas);
		for(String fecha: mapaOrdenado.keySet()){ 
			out.println("<tr><td><input type='radio' name='select' value='" + fecha + "'>" + fecha +"</td></tr>");
		}
		out.println("<tr><td><input type='radio' name='select' value='todos' checked>Todas las opciones mostradas </td></tr>");
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"0\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"11\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"12\"/>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Enviar\"/></td></tr>");
		out.println("<tr><td><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void replyCanalesPorFecha(HttpServletResponse res, PrintWriter out, String fasesRecorridas, 
			ArrayList<String> canales) throws XPathExpressionException{
		printCabeceraHtml(out, fasesRecorridas);
		Collections.sort(canales);
		for(String nombreCanal: canales){
                        out.println("<tr><td><input type='radio' name='select' value='" + nombreCanal + 
                                "'>" + nombreCanal + "</td></tr>");
		}
		out.println("<tr><td><input type='radio' name='select' value='todos' checked>Todas las opciones mostradas </td></tr>");
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"11\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"12\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"13\"/>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Enviar\"/></td></tr>");
		out.println("<tr><td><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void replyPelis(HttpServletResponse res, PrintWriter out, String fasesRecorridas, ArrayList<String> pelis)
		throws XPathExpressionException{
		printCabeceraHtml(out, fasesRecorridas);
		Collections.sort(pelis);
		for(String peli: pelis){
                        out.println("<tr><td><input type='radio' name='select' value='" + peli + 
                                "'>" + peli + "</td></tr>");
		}
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"12\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"13\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"0\"/>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}
	
	public String ArrayListToHtmlButtons (ArrayList<String> arrayTexto){
		//Si hay un error de programación, no se muestra nada. Tampoco la opción de enviar
		if(arrayTexto.isEmpty())
			return "</br>";
		Collections.sort(arrayTexto);
		String HtmlButtons = "<tr><td><input type='radio' name='select' value='" + arrayTexto.get(0) + 
				"'>" + arrayTexto.get(0) +"</td></tr>";
		for(int i = 1; i < arrayTexto.size(); i++){
			HtmlButtons += "<tr><td><input type='radio' name='select' value='" + arrayTexto.get(i) + 
					"'>" + arrayTexto.get(i) +"</td></tr>";
		}
		return HtmlButtons;
	}

	public void replyConsulta2(HttpServletResponse res, PrintWriter out, String fasesRecorridas, ArrayList<String> categorias) {
		printCabeceraHtml(out, fasesRecorridas);
		out.println(ArrayListToHtmlButtons(categorias));
                out.println("<tr><td><input type='radio' name='select' value='todos' checked >Todas las opciones mostradas </td></tr>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Enviar\"/></td></tr>");
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"0\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"21\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"22\"/>");
		out.println("<tr><td><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void replyAlbumesPorYear(HttpServletResponse res, PrintWriter out, String fasesRecorridas, ArrayList<String> albumes){
		printCabeceraHtml(out, fasesRecorridas);
		out.println(ArrayListToHtmlButtons(albumes));
                out.println("<tr><td><input type='radio' name='select' value='todos' checked >Todas las opciones mostradas </td></tr>");
		out.println("<tr><td></br><input type=\"submit\" name=\"accion\" value=\"Enviar\"/></td></tr>");
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"21\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"22\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"23\"/>");
		out.println("<tr><td><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void replyEstilos(HttpServletResponse res, PrintWriter out, String fasesRecorridas, ArrayList<String> estilos){
		printCabeceraHtml(out, fasesRecorridas);
		Collections.sort(estilos);
		for(String estilo: estilos){
                        out.println("<tr><td><input type='radio' name='select' value='" + estilo + 
                                "'>" + estilo + "</td></tr>");
		}
		out.println("<input type=\"hidden\" name=\"prevfase\" value=\"22\"/>");
		out.println("<input type=\"hidden\" name=\"thisfase\" value=\"23\"/>");
		out.println("<input type=\"hidden\" name=\"nextfase\" value=\"0\"/>");
		out.println("<tr><td><input type=\"submit\" name=\"accion\" value=\"Atras\"/>");
		out.println("<input type=\"submit\" name=\"accion\" value=\"Inicio\"/></td></tr>");
		out.println("</table></form>");
		out.println("</body></html>");
	}

	public void printCabeceraHtml(PrintWriter out, String fasesRecorridas){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Practica 2</title>");
		//out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=ISO-8859-8\">");
		//o mejor solo meta charset=UTF-8
		out.println("<link rel='stylesheet' href='iml.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form name='form' id='form' method='POST'><table id='tabla'>");
		out.println("<tr><td><h3>" + fasesRecorridas + "</h3></br></td></tr>");
	}
}

