package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to logout current user out of the application
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession();
        String lastLogin = (String)httpSession.getAttribute("lastLogin");
        String userName = (String) httpSession.getAttribute("username");
        DatabaseHandler handler = DatabaseHandler.getInstance();
        handler.updateLoginTimeStamp(userName, lastLogin);
        System.out.println("In Logout, "+ lastLogin + ", "+ userName);
        httpSession.removeAttribute("username");
        httpSession.invalidate();
        response.sendRedirect("/login");
    }
}
