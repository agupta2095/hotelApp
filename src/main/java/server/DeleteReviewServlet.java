package server;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Servlet to delete review for a logged in user
 */
public class DeleteReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("username");
        if(userName == null) {
            response.sendRedirect("/login");
            return;
        }

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        dbHandler.deleteReview(userName,hotelId);
        response.sendRedirect("/hotelInfo?hotelId="+hotelId);
    }

}
