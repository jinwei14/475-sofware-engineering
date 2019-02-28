package ic.doc.fe;

import ic.doc.web.IndexPage;
import org.apache.http.client.fluent.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FrontEndWebServer {

    private static final int DEFAULT_PORT = 8080;

    private static final String NEWS_URI = "http://localhost:5001/";
    private static final String WEATHER_URI = "http://localhost:5002/";

    public FrontEndWebServer(int port) throws Exception {
        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new Website()), "/");
        server.setHandler(handler);

        server.start();
    }

    static class Website extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            new IndexPage(retrieveNewsData(), retrieveWeatherData()).writeTo(resp);
        }

        private String retrieveNewsData() {
            return fetchDataFrom(NEWS_URI);
        }

        private String retrieveWeatherData() {
            return fetchDataFrom(WEATHER_URI);
        }

        private String fetchDataFrom(String newsUri) {
            try {
                return Request.Get(newsUri).execute().returnContent().asString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int portFrom(String[] args) {
        if (args.length < 1) {
            return DEFAULT_PORT;
        }
        return Integer.valueOf(args[0]);
    }

    public static void main(String[] args) throws Exception {
        new FrontEndWebServer(portFrom(args));
    }
}

