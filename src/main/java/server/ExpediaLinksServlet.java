package server;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ExpediaLinksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        String link = request.getParameter("link");
        link = StringEscapeUtils.escapeHtml4(link);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.addExpediaLink(username, link);
    }
}
