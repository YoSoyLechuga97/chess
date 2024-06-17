package client;

import facade.ServerFacade;
import facade.TerminalUI;

public class Main {
    static ServerFacade facade;
    static TerminalUI terminal;

    public static void main(String[] args) throws Exception {
        facade = new ServerFacade(8080);
        terminal = new TerminalUI();
        terminal.run(8080);
    }
}
