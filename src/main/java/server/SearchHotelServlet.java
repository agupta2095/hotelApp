package server;

import hotelapp.HotelInformation;
import hotelapp.AppInterface;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/** Servlet to search for hotels in the given application using a keyword entered
 * by the user
 * **/
public class SearchHotelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("static/searchHotels.html");

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
        AppInterface appInterface = (AppInterface) request.getServletContext().getAttribute("interface");
        List<HotelInformation> searchedHotels = appInterface.getHotelsWithKeyWordInName(keyword);

        VelocityContext context = new VelocityContext();
        context.put("hotels", searchedHotels);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");

        Template template = ve.getTemplate("static/searchHotels.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
