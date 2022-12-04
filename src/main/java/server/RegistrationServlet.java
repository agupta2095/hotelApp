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


/** Servlet to register a new user into the application
 */
@SuppressWarnings("serial")
public class RegistrationServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		String errorCode = request.getParameter("error");
		if(errorCode != null) {
			errorCode = StringEscapeUtils.escapeHtml4(errorCode);
			Enums.ErrorCode error = Enums.ErrorCode.DEFAULT;
			int errorInt = Integer.parseInt(errorCode);
			if (errorInt == 1) {
				error = Enums.ErrorCode.USER_EXISTS;
			} else if (errorInt == 2) {
				error = Enums.ErrorCode.PASSWORD_MISMATCH;
			} else if (errorInt == 3) {
				error = Enums.ErrorCode.PASSWORD_INSUFFICIENT;
			}
			context.put("error", error);
		}
		HttpSession httpSession = request.getSession();
		Template template = ve.getTemplate("static/registration.html");
		String username = (String)httpSession.getAttribute("username");
		PrintWriter out = response.getWriter();
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		DatabaseHandler dbHandler = DatabaseHandler.getInstance();
		String usernameParam = request.getParameter("username");
		usernameParam = StringEscapeUtils.escapeHtml4(usernameParam);

		if(dbHandler.userNameExists(usernameParam)) {
			response.getWriter().println("Username already taken, try different username");
			response.sendRedirect("/register?error=1");
			return;

		}
		String password = request.getParameter("pass");
		password = StringEscapeUtils.escapeHtml4(password);
        String rePassword = request.getParameter("repass");
		rePassword = StringEscapeUtils.escapeHtml4(rePassword);

		if(!password.equals(rePassword)) {
			response.getWriter().println("Password don't match");
			response.sendRedirect("/register?error=2");
			return;
		}
		Password passObj = new Password();
		if(!passObj.validatePassword(password)) {
			response.getWriter().println("Password should be minimum eight characters, at least one letter, one number and one special character:");
			response.sendRedirect("/register?error=3");
			return;
		}

		dbHandler.registerUser(usernameParam, password);
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute("username", usernameParam);

		response.getWriter().println("Successfully registered the user " + usernameParam);
		response.sendRedirect("/search");
	}

}