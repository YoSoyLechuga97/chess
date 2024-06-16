package client;

import facade.ServerFacade;
import facade.TerminalUI;
import server.Server;

public class Main {
    static ServerFacade facade;
    static TerminalUI terminal;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        terminal = new TerminalUI();
        terminal.run(port);
    }
}
