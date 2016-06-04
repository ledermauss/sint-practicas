/*import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class hello extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws IOException, ServletException
	{
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Hello World!</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<input type=\"radio\" name=\"sexo\" value=\"Hombre\" /> Hombre" +
                   "<input type=\"radio\" name=\"sexo\" value=\"Mujer\" /> Mujer" +
                    "<input type=\"radio\" name=\"sexo\" value=\"Otro\" /> Otro ");
		out.println("<input type=\"submit\" name=\"enviar\" value=\"Enviar\"/>");
		out.println("</body>");
		out.println("</html>");
		

		
	}


    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException
    {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	out.println("<html>");
	out.println("<head>");
	out.println("<title>Respuestax</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<input type=\"radio\" name=\"sexo\" value=\"Hombre\" /> Hombre" +
                   "<input type=\"radio\" name=\"sexo\" value=\"Mujer\" /> Mujer" +
                    "<input type=\"radio\" name=\"sexo\" value=\"Otro\" /> Otro ");
	out.println("<input type=\"submit\" name=\"enviar\" value=\"Enviar\"/>");
	out.println("</body>");
	out.println("</html>");

}


}
*/
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class hello extends HttpServlet {
public void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException
{
res.setContentType("text/html");
PrintWriter out = res.getWriter();
out.println("<html>");
out.println("<head>");
out.println("<title>Hello World!</title>");
out.println("</head>");
out.println("<body>");
out.println("<h1>Hello World!</h1>");
out.println("</body>");
out.println("</html>");
}
}

