package server;

import hotelapp.Review;
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
 * Servlet to modify the review for a particular hotel and user logged-in
 */
public class ModifyReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        HttpSession httpSession = request.getSession();
        String userName = (String) httpSession.getAttribute("username");
        if(userName == null) {
            response.sendRedirect("/login");
            return;
        }
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("hotelId", hotelId);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        Review review = dbHandler.getReviewForAUser(hotelId, userName);
        String title = "", rating = "", text = "";
        if(review != null) {
            title = review.getTitle();
            rating = review.getRatingOverall();
            text = review.getReviewText();
        }
        context.put("userTitle", title);
        context.put("userRating", rating);
        context.put("userText", text);
        Template template = ve.getTemplate("static/modifyReviewNew.html");

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
            dbHandler.modifyReview(title, reviewText, timeStamp, userName, hotelId, rating);
            response.getWriter().println("Successfully modified the review");
            response.sendRedirect("/hotelInfo?hotelId="+hotelId);
            System.out.println(hotelId);
        }
    }
}
