package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return storage.remove(id) != null;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(idCounter.incrementAndGet());
            storage.put(user.getId(), user);
        }
        return storage.computeIfPresent(user.getId(), (id, prevUser) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return storage.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return new ArrayList<>(storage.values()).stream()
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        for (User user : storage.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
