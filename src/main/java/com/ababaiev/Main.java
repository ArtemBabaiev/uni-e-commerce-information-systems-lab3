package com.ababaiev;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;

public class Main {
    public static void main(String[] args) throws Exception {
        String log4jConfPath = Main.class.getClassLoader().getResource("log4j.properties").getFile();
        PropertyConfigurator.configure(log4jConfPath);
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, serverContainer) -> {
            serverContainer.addEndpoint(SocketEndpoint.class);
        });

        server.setHandler(context);
        server.start();
    }
}