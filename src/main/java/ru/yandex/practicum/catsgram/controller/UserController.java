package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping()
    public Collection<User> get() {
        return users.values();
    }

    @PostMapping()
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (isEmailOccupied(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = %d не найден".formatted(newUser.getId()));
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getPassword() != null) {
            oldUser.setPassword(newUser.getPassword());
        }

        if (newUser.getUsername() != null) {
            oldUser.setUsername(newUser.getUsername());
        }

        if (newUser.getEmail() != null && !(newUser.getEmail().isBlank())) {
            if (isEmailOccupied(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            oldUser.setEmail(newUser.getEmail());
        }

        return oldUser;
    }

    private boolean isEmailOccupied(String userEmail) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(userEmail));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

