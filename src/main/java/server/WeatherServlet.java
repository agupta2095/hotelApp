package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.text.StringEscapeUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

public class WeatherServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String lat = request.getParameter("lat");
        lat = StringEscapeUtils.escapeHtml4(lat);
        String longt = request.getParameter("lng");
        longt = StringEscapeUtils.escapeHtml4(longt);
        String host = "https://api.open-meteo.com";
        String pathResource = "/v1/forecast?latitude=" + lat + "&longitude=" + longt;
        pathResource += "&daily=rain_sum&timezone=PST&current_weather=true";
        String urlString = host + pathResource;

        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        JsonObject obj = new JsonObject();
        try {
            URL url = new URL(urlString);
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String weatherRequest = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
            out.println(weatherRequest);
            out.flush();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{")) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jo = (JsonObject) jsonParser.parse(line);
                    JsonObject currentWeatherObj = (JsonObject) jo.get("current_weather");
                    String temp = currentWeatherObj.get("temperature").getAsString();
                    String windSpeed = currentWeatherObj.get("windspeed").getAsString();
                    obj.addProperty("temperature", temp);
                    obj.addProperty("windSpeed", windSpeed);
                    JsonObject dailyWeather = (JsonObject) jo.get("daily");
                    JsonArray rainArr = dailyWeather.getAsJsonArray("rain_sum");
                    double sumRain = 0;
                    for(JsonElement elem : rainArr) {
                        sumRain += elem.getAsDouble();
                    }
                    sumRain /= 7;
                    obj.addProperty("rain", sumRain);
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException | NullPointerException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }
        PrintWriter writer = response.getWriter();
        writer.println(obj);
    }
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

}
