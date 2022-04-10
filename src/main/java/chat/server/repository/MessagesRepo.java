package chat.server.repository;

import chat.commons.MessageMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Slf4j
// method #3 by ConcurrentQueue
public class MessagesRepo {
//    private List<Message2> conversationList;

    private Queue<MessageMapper> conversationListNotSavedOnDisk = new ConcurrentLinkedQueue<>();
    private Queue<MessageMapper> conversationListLoadedFromDisk = new ConcurrentLinkedQueue<>();


    public Queue<MessageMapper> findMessagesByUserName(String userName) {
        //todo body
        return null;
    }

    public void loadConversationListFromDisk() {
        //todo body
    }

    public void appendCurrentConversationListToFileOnDisk() {
        //todo body

    }


}
