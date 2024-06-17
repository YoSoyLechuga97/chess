package websocket;

import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WSClient extends Endpoint {
    public static void run(int port) throws Exception {
        var ws = new WSClient(port);
    }

    public Session session;

    public WSClient(int port) throws Exception {
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        //Add Message handlers
    }

    public void send(UserGameCommand command) throws Exception{
        //Send Commands
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
