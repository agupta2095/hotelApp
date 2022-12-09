package server;

import hotelapp.HotelInformation;
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
import java.text.DecimalFormat;
import java.util.Map;
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
        HttpSession httpSession = request.getSession();
        String userName = (String)httpSession.getAttribute("username");
        if(userName == null) {
            response.sendRedirect("/login");
            return;
        }
        PrintWriter out = response.getWriter();

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        HotelInformation hotelInfoObj = dbHandler.getHotel(hotelId);

        VelocityContext context = new VelocityContext();
        context.put("hotelName", hotelInfoObj.getHotelName());
        context.put("hotelId", hotelId);
        context.put("hotelAddress", hotelInfoObj.getAddress());
        context.put("expediaLink", hotelInfoObj.getExpediaLink("https://www.expedia.com/"));
        context.put("latitude", hotelInfoObj.getLatitude());
        context.put("longitude", hotelInfoObj.getLongitude());

        context.put("userName", userName);
        boolean isFavAdded = false;
        Map<String, String> hotels = dbHandler.getFavouriteHotels(userName);
        if(hotels.containsKey(hotelId)) {
            isFavAdded = true;
        }
        context.put("isFavAdded", isFavAdded);
        Review review = dbHandler.getReviewForAUser(hotelId, userName);
        boolean isAdd = true;
        if(review != null) {
            isAdd = false;
        }
        context.put("displayAdd", isAdd);
        context.put("lastLogin", dbHandler.getLastLogin(userName));
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");

        Template template = ve.getTemplate("static/hotelInfoNew.html");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

}
