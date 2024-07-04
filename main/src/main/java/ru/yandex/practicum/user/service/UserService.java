package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.user.UserMapper;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserShortDto;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public List<UserDto> getUsers(Integer[] ids,
                                  Integer from,
                                  Integer size) {
        if (ids == null) {
            return userMapper.toUserDto(userRepository.findAllFromSize(from, size));
        } else {
            return userMapper.toUserDto(userRepository.findAllByIdsFromSize(ids, from, size));
        }
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    public void deleteUser(@PathVariable int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            log.info("User with id={} was not found", userId);
            throw new ObjectNotFoundException("User with id=" + userId + " was not found", new Throwable("The required object was not found."));

        }

    }

    public User getUserById(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            return userRepository.findById(userId).get();
        } else {
            log.info("User with id={} was not found", userId);
            throw new ObjectNotFoundException("User with id=" + userId + " was not found", new Throwable("The required object was not found."));

        }
    }

}
