package chat.client;

import chat.commons.Commands;
import chat.commons.MessageMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ClientCommands {

    public static boolean loginCommand(Client client) {
        if (client.isLogged()) {
            System.out.println("You are logged");
            return true;
        }

        System.out.printf("What's your name? %s", client.getPrompt());
        String clientName = new Scanner(System.in).nextLine();
        String loginMessage = MessageMapper.createLoginMessage(Commands.$LOGIN_REQUEST, clientName, client.getSocket());
//        Message message = new Message(COMMANDS.$LOGIN, new User(clientName, client.getSocket()));

        boolean result = Boolean.parseBoolean(WriterToServer.sendToServer2WithResponse(loginMessage, client));
        if (!result) System.out.println("You can't login because you're on black list");
        if (result) client.setClientName(clientName);
        return result;
    }

    public static void createChatCommand(Client client) {
        log.info("createChatCommand(Client client)");
        Thread thread = new Thread(() -> new ChatService(client).createAndBeginChat());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void joinToChatCommand(Client client) {
        log.info("joinToChatCommand(Client client)");
        Thread thread = new Thread(() -> new ChatService(client).joinToChat());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String findRoomIdByUserNameCommand(Client client) {
        String findIdRoomMessage = MessageMapper.createFindRoomIdByUserNameMessage(Commands.$FIND_ROOM_ID_BY_USERNAME_MSG, client.getClientName());
        return WriterToServer.sendToServer2WithResponse(findIdRoomMessage, client);
    }


    public static List<String> userListCommand(Client client) {
        String allUsersListMessage = MessageMapper.createAllUsersListMessage(Commands.$USERS_LIST_REQUEST);
//        Message message = new Message(COMMANDS.$USERS_LIST, new User(client.getClientName(), client.getSocket()));
        String serverResponse = WriterToServer.sendToServer2WithResponse(allUsersListMessage, client);
        List<String> usersList = Arrays.asList(serverResponse.split("\\|"));
        return usersList;
    }

    public static void printUserListCommand(Client client) {
        String list = userListCommand(client)
                .stream()
                .collect(Collectors.joining(", ", "On-Line users: ", "."));
        System.out.println(list);
    }


    public static boolean logoutCommand(Client client) {
        if (!client.isLogged()) {
            System.out.println("You are NOT logged");
            return false;
        }
        String exitMessage = MessageMapper.createExitMessage(Commands.$LOGOUT_REQUEST, client.getClientName());
        return Boolean.parseBoolean(WriterToServer.sendToServer2WithResponse(exitMessage, client));
    }
}