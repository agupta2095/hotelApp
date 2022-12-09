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
import java.util.Map;

public class ExpediaLinksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if(username == null) {
            response.sendRedirect("/login");
            return;
        }
        String link = request.getParameter("link");
        link = StringEscapeUtils.escapeHtml4(link);

        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.addExpediaLink(username, link, hotelName);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userName = (String)session.getAttribute("username");
        PrintWriter out = response.getWriter();
        if(userName != null) {
            DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
            databaseHandler.clearExpediaLinks(userName);
        }
        response.sendRedirect("/search?username="+userName);
    }
}
