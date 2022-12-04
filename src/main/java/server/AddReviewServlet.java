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
 * Servlet to add review for a logged in user
 */
public class AddReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("hotelId", hotelId);
        Template template = ve.getTemplate("templates/addReview.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userName = (String)session.getAttribute("username");
        if(userName != null) {
            String rating = request.getParameter("rating");
            rating = StringEscapeUtils.escapeHtml4(rating);
            String title = request.getParameter("title");
            title = StringEscapeUtils.escapeHtml4(title);
            String reviewText = request.getParameter("text");
            reviewText = StringEscapeUtils.escapeHtml4(reviewText);
            String hotelId = request.getParameter("hotelId");
            String timeStamp = java.time.LocalDateTime.now().toString();
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            dbHandler.addReview(title, reviewText, timeStamp, userName, hotelId, rating);
            response.getWriter().println("Successfully added the review");
            response.sendRedirect("/hotelInfo?hotelId="+hotelId);
        }
    }
}
