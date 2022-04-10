package chat.server.repository;

import chat.commons.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@Slf4j
//method #1 by ReadWriteLock
public class UsersRepo {

    ReadWriteLock locker = new ReentrantReadWriteLock();
    private List<User> userLists = new ArrayList<>();


    public boolean addUser(User user) {
        locker.writeLock().lock();
        boolean addResult = userLists.add(user);
        locker.writeLock().unlock();
        return addResult;
    }

    public boolean removeUser(User user) {
        locker.writeLock().lock();
        boolean removeResult = userLists.remove(user);
        locker.writeLock().unlock();
        return removeResult;
    }

    public User findUserByName(String name) {
        locker.readLock().lock();
        User user = userLists.stream().filter(s -> s.getName().equals(name)).findFirst().get();
        locker.readLock().unlock();
        return user;
    }

    public Socket findSocketByUserName(String name) {
        locker.readLock().lock();
        Socket socket = userLists.stream().filter(s -> s.getName().equals(name)).findFirst().map(User::getSocket).get();
        locker.readLock().unlock();
        return socket;
    }
}
