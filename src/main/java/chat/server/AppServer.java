package chat.server;

import chat.server.repository.MessagesRepo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class AppServer {
    private int port;
    private int maxClients;
    private ExecutorService executorService;

    private MessagesRepo messagesRepo;
    private RoomsRepo roomsRepo;
    private UsersRepo usersRepo;

    public static void main(String[] args) {
        log.info("Application started...");
        new AppServer();
    }

    public AppServer() {
        this.messagesRepo = new MessagesRepo();
        this.roomsRepo = new RoomsRepo();
        this.usersRepo = new UsersRepo();

        loadServerConfiguration();
        startTheServer();
    }

    private void loadServerConfiguration() {
        HashMap<String, String> configMap = IOTools.loadConfigFile();
        port = Integer.parseInt(configMap.get("port"));
        maxClients = Integer.parseInt(configMap.get("threads"));
        executorService = Executors.newFixedThreadPool(maxClients);
        log.info("Config data for SERVER loaded...");
    }

    public void startTheServer() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                log.info("1 - Server is listening...");
                Socket socket = server.accept();
                log.info("2 - Server accepted connection with client...Socket:{}", socket);
                ServerWorker sw = new ServerWorker(messagesRepo, roomsRepo, usersRepo, socket);
                executorService.execute(sw);
                log.info("3 - New thread (session) created...");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}