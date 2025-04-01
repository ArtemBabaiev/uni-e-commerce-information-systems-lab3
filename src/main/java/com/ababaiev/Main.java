package com.ababaiev;

import com.ababaiev.web.SocketEndpoint;
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
            serverContainer.setDefaultMaxSessionIdleTimeout(10 * 60 * 1000L); // 10 minutes
            serverContainer.setDefaultMaxBinaryMessageBufferSize(10 * 1024 * 1024); // 1 MB
            serverContainer.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
            serverContainer.addEndpoint(SocketEndpoint.class);
        });

        server.setHandler(context);
        server.start();
    }
}