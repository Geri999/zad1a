package chat.server;

import chat.commons.MessageMapper;
import chat.commons.Room;
import chat.commons.User;
import chat.server.repository.MessagesRepo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Builder
public class ServerAPI {

    private final MessagesRepo messagesRepo;
    private final RoomsRepo roomsRepo;
    private final UsersRepo usersRepo;
    private final Socket socket;

    public void commandInterpreter(String message, PrintWriter output) {
        String command = message.split("\\|")[0];
        log.info(command);
        switch (command) {
            case "$LOGIN_REQUEST":
                login(message, output);
                break;
            case "$USERS_LIST_REQUEST":
                userList(output);
                break;
            case "$LOGOUT_REQUEST":
                exit(message, output);
                break;
            case "$FIND_ROOM_ID_BY_USERNAME_MSG":
                findRoomIdByUsername(message, output);
                break;
            case "$CREATE_ROOM_REQUEST":
                requestForRoom(message, output);
                break;
            case "$BROADCAST_TEXT_MSG":
                chat_Text(message);
                break;
        }
    }

    private void chat_Text(String message) {
        String[] splitChat = message.split("\\|");
        String roomId = splitChat[2];
        String text = splitChat[3];

        log.info("case=$BROADCAST_TEXT_MSG, room id={}", roomId);

        Room roomById = roomsRepo.findRoomById(roomId);

        roomById.broadcastToAllRoomParticipant(message);
        log.info("broadcasting text={}", message);
    }

    private void requestForRoom(String message, PrintWriter output) {
        String s = message.split("\\|")[1];
        List<String> userNameForRoomList = MessageMapper.stringToListParser(s);

        Room room = new Room();

        for (String userN : userNameForRoomList) {
            User userByName1 = usersRepo.findUserByName(userN);
            room.getUserListInRoom().add(userByName1);
        }

        //todo: sprawdz czy już taki pokój istnieje, jesli nie to nie ma co dodawać, tylko zwróc jego ID
        roomsRepo.getRoomsList().add(room);
        output.println(room.getRoomId());
    }

    private void findRoomIdByUsername(String message, PrintWriter output) {
        //find one room only
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        List<Room> rooms = roomsRepo.inWhichRoomsIsSender(user.getName());
        String roomId;
        if (rooms.size() < 1) {
            roomId = "empty";
        } else {
            roomId = rooms.stream().findFirst().get().getRoomId();
        }
        output.println(roomId);
    }

    private void exit(String message, PrintWriter output) {
        User userByName = usersRepo.findUserByName(message.split("\\|")[1]);
        boolean operationResult = usersRepo.getUserLists().remove(userByName);
        output.println(operationResult);
        //todo zamknij pokój gdy było ich tylko 2, odejmij z pokojów wielosobowych, odejmij z listy klientów
        log.info("User removed from usersRepo"); //todo second condition, when "false"
    }

    private void userList(PrintWriter output) {
        String collect = usersRepo.getUserLists().stream().map(User::getName).collect(Collectors.joining("|"));
        output.println(collect);
        log.info("User list prepared and sent");
    }

    private void login(String message, PrintWriter output) {
        String[] split = message.split("\\|");
        String userName = split[1];
        Socket socketFromInput = socket;

        usersRepo.getUserLists().add(new User(userName, socketFromInput));
        output.println("true");
        log.info("User added");
    }
}