package com.hanna;

import java.io.IOException;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

/**
 * Hello world!
 */
public class App {

    private static final URI BASE_URI = URI.create("http://localhost:8080/");
    public static final String ROOT_PATH = "api";

    public static void main(String[] args) {
        try {
            System.out.println("Lancement de l'api CognitiveCommerce");

            final ResourceConfig resourceConfig = new ResourceConfig(restApi.class);
            resourceConfig.register(LoggingFeature.class);
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    server.shutdownNow();
                }
            }));
            server.start();

            System.out.println(String.format("Le serveur tourne sur "+BASE_URI+ROOT_PATH+", fermer le processus pour l'arreter.",
                    BASE_URI, ROOT_PATH));
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
