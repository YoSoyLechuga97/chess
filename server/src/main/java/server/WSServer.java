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
    public void run(int port) {
        Spark.webSocket("/ws", WSServer.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        //Save session with authToken
        //Save Session with gameID
        session.getRemote().sendString("WebSocket response: " + message);
    }
}
