package server;

import hotelapp.HotelInformation;
import hotelapp.Review;
import hotelapp.AppInterface;
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
import java.text.DecimalFormat;
import java.util.Set;

/**
 * Servlet to access the hotel Information and reviews for a particular hotel
 */
public class HotelInfoServlet extends HttpServlet {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        AppInterface appInterface = (AppInterface) request.getServletContext().getAttribute("interface");
        HotelInformation hotelInfoObj = appInterface.getHotel(hotelId);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        Set<Review> reviews = appInterface.getReviewsForAHotel(hotelId);

        Set<Review> newReviews = dbHandler.getReview(hotelId);
        if(reviews != null) {
            reviews.addAll(newReviews);
        } else if(newReviews != null && !newReviews.isEmpty()) {
            reviews = newReviews;
        }
        double avgRating = appInterface.getAverageRating(hotelId, newReviews);

        VelocityContext context = new VelocityContext();
        context.put("hotelName", hotelInfoObj.getHotelName());
        context.put("hotelId", hotelId);
        context.put("hotelAddress", hotelInfoObj.getAddress());
        context.put("expediaLink", hotelInfoObj.getExpediaLink("https://www.expedia.com/"));
        context.put("latitude", hotelInfoObj.getLatitude());
        context.put("longitude", hotelInfoObj.getLongitude());
        HttpSession httpSession = request.getSession();
        String userName = (String)httpSession.getAttribute("username");
        context.put("userName", userName);
        context.put("avgRating", decimalFormat.format(avgRating));
        if(reviews != null) {
            context.put("reviews", reviews);
        }

        Review review = dbHandler.getReviewForAUser(hotelId, userName);
        boolean isAdd = true;
        if(review != null) {
            isAdd = false;
        }
        context.put("displayAdd", isAdd);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");

        Template template = ve.getTemplate("static/hotelInfo.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

}
