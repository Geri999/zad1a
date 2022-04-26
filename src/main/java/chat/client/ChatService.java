package chat.client;

import chat.commons.Commands;
import chat.commons.IOTools;
import chat.commons.MessageMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class ChatService {
    private final Client client;

    public ChatService(Client client) {
        this.client = client;
    }

    public void joinToChat() {
        log.info("ChatService joinToChat() method started");

        String roomId = checkingIfUserHasActiveChat();
        if ("empty".equals(roomId)) {
            System.out.println("You are not in any room right now");
            return;
        }
        readingThreadDemonCreation();
        chatConversation(roomId);
    }

    public void createAndBeginChat() {
        log.info("ChatService chat() method started");

        String roomId = chatCreation();
        if (roomId == null) return;

        readingThreadDemonCreation();
        chatConversation(roomId);
    }
//#################################################################################################################################################

    private String checkingIfUserHasActiveChat() {
        String findRoomIdByUserNameMessage = MessageMapper.createFindRoomIdByUserNameMessage(Commands.$FIND_ROOM_ID_BY_USERNAME_MSG, client.getClientName());
        String roomId = WriterToServer.sendToServer2WithResponse(findRoomIdByUserNameMessage, client);
        log.info("ID room by UserName={}", roomId);
        return "empty".equals(roomId) ? "empty" : roomId;
    }

    private String chatCreation() {
        Scanner sc = new Scanner(System.in);
        List<String> usersInvitedToChat = new ArrayList<>();
        usersInvitedToChat.add(client.getClientName());

        List<String> allUsersOnline = ClientCommands.userListCommand(client);

        String inputUserName = "";

        System.out.println(allUsersOnline.stream().collect(Collectors.joining(", ", "Users on-Line: ", ".")));
        System.out.println("\n*** ADDING USERS TO ROOM ***");
        System.out.printf("(Type \"exit\" to finish, \"menu\" to return to main menu.)\n");

        while (!(inputUserName.equalsIgnoreCase("exit"))) {
            System.out.print("Please type name to add user to room (\"exit\" to finish): " + client.getPrompt());
            inputUserName = sc.nextLine();
            if (inputUserName.equals("menu")) return null;

            boolean isUserOnLine = allUsersOnline.contains(inputUserName);
            if (isUserOnLine) usersInvitedToChat.add(inputUserName);
            System.out.println(isUserOnLine
                    ? String.format("Ok, %s is added to room.", inputUserName)
                    : String.format("Sorry, %s is NOT on-line.", inputUserName));
        }

        String requestForRoomMessage = MessageMapper.createRequestForRoomMessage(Commands.$CREATE_ROOM_REQUEST, usersInvitedToChat);
        String roomId = WriterToServer.sendToServer2WithResponse(requestForRoomMessage, client);

        return roomId;
    }

    private void readingThreadDemonCreation() {
        log.info("New thread for reading from server and print on console");
        Thread thread = new Thread(() -> new ReaderFromServer(client).readFromServerAndPrintOnConsole());
        thread.setDaemon(true);
        thread.start();
    }

    private void chatConversation(String roomId) {
        System.out.println("\n" + "*".repeat(20) + "CHAT STARTED:" + "*".repeat(20));
        System.out.println("(type @END to stop conversation)");
        System.out.println("(type @SEND to enter file sending menu)");
        System.out.println("Ask your other chat participants to enter the room (menu item 4)");

        String text;
        Scanner sc = new Scanner(System.in);
        boolean loopCondition = true;
        String message;
        while (loopCondition) {
            switch (text = sc.nextLine()) {
                case "@end":
                case "@END":
                    loopCondition = false;
                    message = MessageMapper.createChatTxtMessage(Commands.$LEAVING_THE_ROOM_REQUEST, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer2(message, client);
                    break;
                case "@send":
                case "@SEND":
                    message = MessageMapper.createChatTxtMessage(Commands.$SEND_FILE_MSG, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer2(message, client);
                    IOTools.sendFile(client.getSocket());
                    log.info("End of @SEND command");
                    break;
                default:
                    message = MessageMapper.createChatTxtMessage(Commands.$BROADCAST_TEXT_MSG, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer2(message, client);
                    log.info(Commands.$BROADCAST_TEXT_MSG.toString());
                    log.info(client.getClientName());
                    log.info(roomId);
                    log.info(text);
            }
        }
        System.out.println("**** End of conversation ****");
    }

    @Deprecated
    private void chatConversation1(String roomId) {
        System.out.println("\n" + "*".repeat(20) + "CHAT STARTED:" + "*".repeat(20));
        System.out.println("(type @END to stop conversation)");
        System.out.println("(type @SEND to enter file sending menu)");
        System.out.println("Ask your other chat participants to enter the room (menu item 4)");
        String text;
        Scanner sc = new Scanner(System.in);
        while (!(text = sc.nextLine()).matches("@end|@END")) {
            //todo: sprawdzenie różnych komend wysłanych z serwera np. koniec rozmowy "@END" i itd. easy! Wysłać do servera komenda zakończenia, itp
            String message = MessageMapper.createChatTxtMessage(Commands.$BROADCAST_TEXT_MSG, client.getClientName(), roomId, text);
            log.info(Commands.$BROADCAST_TEXT_MSG.toString());
            log.info(client.getClientName());
            log.info(roomId);
            log.info(text);
            WriterToServer.sendToServer2(message, client);
        }
        System.out.println("**** End of conversation ****");
    }


}