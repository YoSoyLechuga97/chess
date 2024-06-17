package server;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;
import spark.Spark;

import javax.websocket.server.ServerContainer;

@WebSocket
public class WSServer {
    public WSServer(int port) {
        //Spark.port(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        try {

        }
        Spark.webSocket("/ws", WSServer.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getRemote().sendString("WebSocket response: " + message);
    }
}
