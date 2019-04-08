package org.eclipse.jetty.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest
{
    private Server server;

    @BeforeEach
    public void startServer() throws Exception
    {
        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // let connector pick port

        server.addConnector(connector);

        // Add support for annotation scanning
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault( server );
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        // Init webapp
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("src/main/webapp"); // use source tree for main webapp tree
        webapp.setExtraClasspath("target/classes"); // use compiled classes location from maven to run with

        HandlerList handlers = new HandlerList();
        handlers.addHandler(webapp);
        handlers.addHandler(new DefaultHandler()); // to see errors in config faster

        server.setHandler(handlers);
        server.start();
    }

    @AfterEach
    public void stopServer() throws Exception
    {
        server.stop();
    }

    @Test
    public void testGet() throws IOException
    {
        URL destUrl = server.getURI().resolve("/hello").toURL();
        HttpURLConnection http = (HttpURLConnection) destUrl.openConnection();

        assertEquals(HttpURLConnection.HTTP_OK, http.getResponseCode());
    }
}
