package chat.server.repository;

import chat.commons.Room;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
//method #2 by "synchronized" artefact
public class RoomsRepo {
    List<Room> roomsList = new ArrayList();

    public synchronized Room findRoomById(String id) {
        Room room = roomsList.stream().filter(s -> id.equals(s.getRoomId())).findFirst().get();
        log.info(room.toString());
        return room;
    }

    public synchronized Room isTheRoomWithAllAskedUsers(List<String> userList) {

        return null;
    }

    public synchronized List<Room> inWhichRoomsIsSender(String senderName) {
  /*      List<Room> rooms = null;
        for (Room room : roomsList) {
            if (room.getUserListInRoom().stream().filter(s->s.getName().equals(senderName)).count()>0) rooms.add(room);
        }*/

        List<Room> collect = roomsList.stream()
                .filter(s -> (s.getUserListInRoom()
                        .stream()
                        .filter(u -> u.getName().equals(senderName))
                        .count() > 0))
                .collect(Collectors.toList());

        log.info("Co zostało znalezione gdy szukano RoomByUserName=={}",collect.size());

        return collect;
    }
}