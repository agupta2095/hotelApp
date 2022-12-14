package server;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;


public class HotelServer {
    public static final int PORT = 8083;

    public static void main(String[] args) throws Exception {

        Server server = new Server(PORT);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        handler.setAttribute("templateEngine", velocity);


        handler.addServlet(RegistrationServlet.class, "/register");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(SearchHotelServlet.class, "/search");
        handler.addServlet(HotelInfoServlet.class, "/hotelInfo");
        handler.addServlet(AddReviewServlet.class, "/addReview");
        handler.addServlet(ModifyReviewServlet.class, "/modifyReview");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(DeleteReviewServlet.class, "/deleteReview");
        handler.addServlet(ExpediaLinksServlet.class, "/expediaLinks");
        handler.addServlet(ReviewsServlet.class, "/reviews");
        handler.addServlet(FavHotelsServlet.class, "/favHotels");
        handler.addServlet(WeatherServlet.class, "/weather");

        ResourceHandler resource_handler = new ResourceHandler(); // a handler for serving static pages
        resource_handler.setDirectoriesListed(true);

        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, handler });
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
