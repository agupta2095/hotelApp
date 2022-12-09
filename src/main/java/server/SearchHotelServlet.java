package server;

import hotelapp.HotelInformation;
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
import java.util.List;
import java.util.Map;

/** Servlet to search for hotels in the given application using a keyword entered
 * by the user
 * **/
public class SearchHotelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession httpSession = request.getSession();
        String userName = (String)httpSession.getAttribute("username");
        if(userName ==  null) {
            response.sendRedirect("/login");
            return;
        }
        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        Map<String, String> expediaLinks = databaseHandler.getExpediaLinks(userName);
        context.put("expediaLinks", expediaLinks);
        String lastLogin = databaseHandler.getLastLogin(userName);
        context.put("lastLogin", lastLogin);
        Template template = ve.getTemplate("static/searchHotelsNew.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String keyword = request.getParameter("keyword");
        keyword = StringEscapeUtils.escapeHtml4(keyword);
        System.out.println(keyword);

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        List<HotelInformation> searchedHotels = databaseHandler.getHotelsWithKeyWordInName(keyword);

        VelocityContext context = new VelocityContext();
        context.put("hotels", searchedHotels);


        HttpSession httpSession = request.getSession();
        String userName = (String)httpSession.getAttribute("username");
        String lastLogin = databaseHandler.getLastLogin(userName);

        context.put("lastLogin", lastLogin);

        Map<String, String> expediaLinks = databaseHandler.getExpediaLinks(userName);
        context.put("expediaLinks", expediaLinks);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");


        Template template = ve.getTemplate("static/searchHotelsNew.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
