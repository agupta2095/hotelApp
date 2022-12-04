package server;

import hotelapp.AppInterface;
import hotelapp.CommandLineParser;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;


public class HotelServer {
    public static final int PORT = 8083;

    public static void main(String[] args) throws Exception {
        CommandLineParser cp = new CommandLineParser();
        cp.parseCommandLineArguments(args);

        AppInterface appInterface = new AppInterface();
        if(!cp.processCmdLineArguments(appInterface)) {
            return;
        }
        appInterface.fillAvgRating();

        Server server = new Server(PORT);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        handler.setAttribute("templateEngine", velocity);
        handler.setAttribute("interface", appInterface);

        handler.addServlet(RegistrationServlet.class, "/register");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(SearchHotelServlet.class, "/search");
        handler.addServlet(HotelInfoServlet.class, "/hotelInfo");
        handler.addServlet(AddReviewServlet.class, "/addReview");
        handler.addServlet(HomeServlet.class, "/home");
        handler.addServlet(ModifyReviewServlet.class, "/modifyReview");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(DeleteReviewServlet.class, "/deleteReview");
        handler.addServlet(ExpediaLinksServlet.class, "/expediaLinks");
        handler.addServlet(ReviewsServlet.class, "/reviews");
        server.setHandler(handler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
