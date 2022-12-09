package server;

import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.json.JSONArray;
import org.json.JSONObject;
public class ReviewsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        Set<Review> reviews = databaseHandler.getReview(hotelId);

        JSONArray array = new JSONArray();

        if(reviews != null) {
            int index = 0;
            for (Review review : reviews) {
                JSONObject obj = new JSONObject();
                obj.put("reviewId", review.getReviewId());
                obj.put("title", review.getTitle());
                obj.put("text", review.getReviewText());
                obj.put("date", review.getTimeStamp());
                obj.put("username", review.getUserName());
                obj.put("rating", review.getRatingOverall());
                array.put(index, obj);
                index++;
            }
        }

        out.println(array);
    }
}
