package server;

import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class FavHotelsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String username = (String) session.getAttribute("username");

        String clear = request.getParameter("clear");
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        if(clear != null) {
            databaseHandler.clearFavHotels(username);
            return;
        }

        Map<String, String> hotels = databaseHandler.getFavouriteHotels(username);


        //System.out.println(hotels);
        JSONArray array = new JSONArray();
        if(hotels != null) {
            int index = 0;
            for (String id : hotels.keySet()) {
                String name = hotels.get(id);
                JSONObject obj = new JSONObject();
                obj.put("hotelId", id);
                obj.put("hotelName", name);
                array.put(index, obj);
                index++;
            }
        }
        System.out.println(array.length());
        out.println(array);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userName = (String)session.getAttribute("username");

        System.out.println("Post in Favourite Hotels " + userName);
        if(userName != null) {
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            String hotelName = request.getParameter("hotelName");
            hotelName = StringEscapeUtils.escapeHtml4(hotelName);
            DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
            databaseHandler.addFavouriteHotel(userName, hotelId, hotelName);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
