package com.cloudpocket.services;

import com.cloudpocket.exceptions.UserAlreadyExistException;
import com.cloudpocket.model.User;
import com.cloudpocket.model.dto.UserDto;
import com.cloudpocket.repository.UserRepository;

import com.cloudpocket.utils.FSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private Md5PasswordEncoder md5Encoder;

    @Value("${cloudpocket.storage}")
    private String PATH_TO_STORAGE;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public List<User> getUsers(Integer page, Integer itemsPerPage) {
        if (page == null) {
            page = 0;
        }
        if (itemsPerPage == null) {
            itemsPerPage = 10;
        }

        Pageable pageable = new PageRequest(page, itemsPerPage);
        return repository.findAll(pageable).getContent();
    }

    public Long countUsers() {
        return repository.count();
    }

    public User getUserById(long id) {
        return repository.findOne(id);
    }

    public User getUserByLogin(String login) {
        return repository.findUserByLogin(login);
    }

    /**
     * Adds new user.
     * Creates for new user his home dir. If it already exist
     *  then try to remove ald folder and create new one.
     *
     * @param newUserInfo
     *         new user's data
     * @return data about just created user
     * @throws IOException
     *         if error occurs while creating user's home directory
     * @throws UserAlreadyExistException
     *         if user with given login already exists
     */
    public User addUser(UserDto newUserInfo) throws IOException {
        if (repository.findUserByLogin(newUserInfo.getLogin()) != null) {
            throw new UserAlreadyExistException();
        }

        Path userHomeDir = Paths.get(PATH_TO_STORAGE, newUserInfo.getLogin());
        if (Files.exists(userHomeDir)) {
            FSUtils.delete(userHomeDir);
        }
        Files.createDirectories(userHomeDir);

        User user = new User();
        user.setLogin(newUserInfo.getLogin());
        user.setEmail(newUserInfo.getEmail());
        user.setPasswordHash(md5Encoder.encodePassword(newUserInfo.getPassword(), null));
        user.setJoinDate(new Timestamp(new Date().getTime()));

        return repository.saveAndFlush(user);
    }

    /**
     * Updates all information about user.
     * Only admin must be able to use this method.
     * If a field not set, then it will not be updated.
     * Note, that user's id must be the same.
     *
     * @param user
     *        new data
     * @return updated user info
     * @throws IOException
     *         if problems occurs with renaming of user's home directory
     */
    public User updateUser(User user) throws IOException {
        User oldUserData = repository.findOne(user.getId());

        if (user.getLogin() == null) {
            user.setLogin(oldUserData.getLogin());
        } else {
            if (!oldUserData.getLogin().equals(user.getLogin())) {
                Path oldHomeDirName = Paths.get(PATH_TO_STORAGE, oldUserData.getLogin());
                Files.move(oldHomeDirName, oldHomeDirName.resolveSibling(user.getLogin()));
            }
        }
        if (user.getPasswordHash() == null) {
            user.setPasswordHash(oldUserData.getPasswordHash());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUserData.getEmail());
        }
        if (user.getJoinDate() == null) {
            user.setJoinDate(oldUserData.getJoinDate());
        }

        return repository.saveAndFlush(user);
    }

    /**
     * Updates information about user, which user can change by himself.
     * If a field not set, then it will not be updated.
     * Note, that user's login is identifier of a user and it must be the same,
     *  otherwise method throw RuntimeException.
     *
     * @param userDto
     *         new user's data
     * @return update info about user
     * @throws RuntimeException
     *         if user with given login does not exist
     */
    public UserDto updateUser(UserDto userDto) {
        User userData = repository.findUserByLogin(userDto.getLogin());
        if (userData == null) {
            throw new RuntimeException("User cannot change him login");
        }

        if (userDto.getPassword() != null) {
            userData.setPasswordHash(md5Encoder.encodePassword(userDto.getPassword(), null));
        }
        if (userDto.getEmail() != null) {
            userData.setEmail(userDto.getEmail());
        }
        repository.saveAndFlush(userData);

        return userDto;
    }

    public void deleteUserById(long id) {
        String userLogin = repository.findOne(id).getLogin();
        repository.delete(id);
        deleteUserData(userLogin);
    }

    @Transactional
    public void deleteUserByLogin(String login) {
        repository.deleteUserByLogin(login);
        deleteUserData(login);
    }

    /**
     * Deletes user's files from server file system.
     *
     * @param login
     *        user's login
     */
    private void deleteUserData(String login) {
        try {
            FSUtils.delete(Paths.get(PATH_TO_STORAGE, login));
        } catch (IOException e) {
            // TODO log errors
        }
    }

}
