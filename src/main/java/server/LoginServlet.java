package server;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Servlet to login into the Hotel Application
 */
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		Template template = ve.getTemplate("static/homePage.html");
		String errorCode = request.getParameter("error");
		if(errorCode != null) {
			errorCode = StringEscapeUtils.escapeHtml4(errorCode);
			Enums.ErrorCode error = Enums.ErrorCode.DEFAULT;
			int errorInt = Integer.parseInt(errorCode);
			if (errorInt == 4) {
				error = Enums.ErrorCode.LOGIN_ERROR;
			}
			context.put("error", error);
		}

        HttpSession httpSession = request.getSession();
		String username = (String)httpSession.getAttribute("username");
		if (username == null) {
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			out.println(writer);
		}
		else  {
			response.sendRedirect("/search?username="+username);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("username");
		String pass = request.getParameter("pass");

		DatabaseHandler dbHandler = DatabaseHandler.getInstance();
		boolean flag = dbHandler.authenticateUser(user, pass);
		String timeStamp = java.time.LocalDateTime.now().toString();
		HttpSession session = request.getSession();
		if (flag) {
			session.setAttribute("username", user);
			session.setAttribute("lastLogin", timeStamp);
			response.sendRedirect("/search?username="+user);
		}
		else {
			PrintWriter out = response.getWriter();
			out.println("Incorrect Username or Password.");
			response.sendRedirect("/login?error=4");
		}
	}
}