package chat.client.view;

import chat.client.Client;
import chat.client.ClientCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class ClientGui {
    private Client client;
    Scanner sc = new Scanner(System.in);

    public ClientGui(Client client) {
        this.client = client;
    }

    private List<String> menuPositionGenerator() {
        ArrayList<String> menuList = new ArrayList<>();
//        menuList.add("1 - Login to server\n");
        menuList.add("2 - Print list of connected users\n");
        menuList.add("3 - Create private chat with chosen users\n");
        menuList.add("4 - Join to created chat room\n");
        menuList.add("5 - Load and print all my chats\n");
        menuList.add("0 - Exit\n");
        return menuList;
    }

    public void menu() {
        ClientCommands.loginCommand(client);
        while (true) {
            String info = String.format("[You are %slogged%s]", client.isLogged() ? "" : "NOT ", client.isLogged() ? " as " + client.getClientName() : "");
            String menuHeader = "\n" + "-".repeat(4) + "MENU" + "-".repeat(10) + info + "-".repeat(10) + "\n";
            String menu = menuPositionGenerator().stream().collect(Collectors.joining());
            String menuFooter = "-".repeat(28 + info.length()) + "\n" + "Please choice one option: " + client.getPrompt();
            System.out.print(menuHeader + menu + menuFooter);

            int userInput;
            switch (userInput = Integer.parseInt(sc.nextLine())) {
/*                case (1):
                    System.out.println(menuPositionGenerator().get(userInput - 1));
                    client.setLogged(ClientCommands.loginCommand(client));
                    break;*/
                case (2):
                    System.out.println(menuPositionGenerator().get(userInput - 1));
//                    if (!checkLoggingStatus()) break;
                    ClientCommands.printUserListCommand(client);
                    break;
                case (3):
                    System.out.println(menuPositionGenerator().get(userInput - 1));
//                    if (!checkLoggingStatus()) break;
                    ClientCommands.createChatCommand(client);
                    break;
                case (4):
                    System.out.println(menuPositionGenerator().get(userInput - 1));
//                    if (!checkLoggingStatus()) break;
                    ClientCommands.joinToChatCommand(client);
                    break;
                case (5):
                    System.out.println(menuPositionGenerator().get(userInput - 1));
//                    if (!checkLoggingStatus()) break;
                    ClientCommands.printAllMyChats(client);
                    break;

                case (0):
                    System.out.println(menuPositionGenerator().get(menuPositionGenerator().size() - 1));
                    client.setLogged(ClientCommands.logoutCommand(client));
                    System.out.println("You have been successfully logged out");
                    System.exit(0);
                    break;
                default:
                    System.out.println("repeat please");
            }
        }
    }

    private boolean checkLoggingStatus() {
        if (client.isLogged()) return true;

        System.out.println("You are not logged!");
        return false;
    }
}