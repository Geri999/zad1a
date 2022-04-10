package chat.client.view;

import chat.client.Client;
import chat.client.ClientCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class ClientGui {
    private Client client;
    Scanner sc = new Scanner(System.in);

    public ClientGui(Client client) {
        this.client = client;
    }

    public void menu() {
        while (true) {
            String info = String.format("[You are %slogged%s]", client.isLogged() ? "" : "NOT ", client.isLogged() ? " as " + client.getClientName() : "");

            String menu = "\n" + "-".repeat(4) + "MENU" + "-".repeat(10) + info + "-".repeat(10) + "\n"
                    + "1 - Login to server\n"
                    + "2 - Print list of connected users\n"
                    + "3 - Create private chat with chosen users\n"
                    + "4 - Join to chat room\n"
                    + "5 - List of your saved chats (under construction)\n"
                    + "6 - Print chosen chat (under construction)\n"
                    + "0 - Exit\n"
                    + "-".repeat(28 + info.length()) + "\n"
                    + "Please choice one option:" + client.getPrompt();
            System.out.print(menu);

            switch (sc.nextLine()) {
                case ("1"):
                    System.out.println("Login to server...");
                    client.setLogged(ClientCommands.loginCommand(client));
                    break;
                case ("2"):
                    System.out.println("Print list of connected users");
                    ClientCommands.printUserListCommand(client);
                    break;
                case ("3"):
                    System.out.println("Create private chat with chosen users");
                    ClientCommands.createChatCommand(client);
                    break;
                case ("4"):
                    System.out.println("Join to chat room");
                    ClientCommands.joinToChatCommand(client);
                    break;
                case ("5"):
                    System.out.println("List of your saved chats (under construction)");
                    break;
                case ("6"):
                    System.out.println("\nPrint chosen chat (under construction)");
                    break;
                case ("0"):
                    System.out.println("You are logged out...");
                    client.setLogged(ClientCommands.logoutCommand(client));
                    System.out.println("You have been successfully logged out");
                    System.exit(0);
                    break;
                default:
                    System.out.println("repeat please");
            }
        }
    }
}