package com.socialnetwork.connecthub.backend.persistence.json;

import com.socialnetwork.connecthub.backend.model.User;
import com.socialnetwork.connecthub.backend.persistence.repository.UserRepository;
import com.socialnetwork.connecthub.util.idgenerator.IdGenerator;
import com.socialnetwork.connecthub.util.idgenerator.IdGeneratorFactory;
import com.socialnetwork.connecthub.util.JsonFileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUserRepository implements UserRepository {
    private static final String FILE_PATH = "src/com/socialnetwork/connecthub/resources/data/users.json";
    private static JsonUserRepository instance;
    private final JsonFileUtil<User> jsonFileUtil = new JsonFileUtil<>(User[].class);
    private List<User> users;
    private final IdGenerator idGenerator;

    private JsonUserRepository() {
        users = new ArrayList<>(jsonFileUtil.loadFromFile(FILE_PATH));
        this.idGenerator = IdGeneratorFactory.getIdGenerator("uuid");
    }

    public static synchronized JsonUserRepository getInstance() {
        if (instance == null) {
            instance = new JsonUserRepository();
        }
        return instance;
    }

    @Override
    public Optional<User> findById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(User user) {
        boolean userExists = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                userExists = true;
                break;
            }
        }
        if (!userExists) {
            user.setUserId(idGenerator.generateId());
            users.add(user);
        }
        jsonFileUtil.saveToFile(FILE_PATH, users);
    }

    @Override
    public void delete(String userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(userId)) {
                users.remove(i);
                break;
            }
        }
        jsonFileUtil.saveToFile(FILE_PATH, users);
    }

    @Override
    public List<User> findAllFriends(String userId) {
        List<User> friends = new ArrayList<>();
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            List<String> friendIds = user.get().getFriends();
            for (String friendId : friendIds) {
                Optional<User> friend = findById(friendId);
                if (friend.isPresent()) {
                    friends.add(friend.get());
                }
            }
        }
        return friends;
    }

    @Override
    public List<User> findAllBlockedUsers(String userId) {
        List<User> blockedUsers = new ArrayList<>();
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            List<String> blockedIds = user.get().getBlockedUsers();
            for (String blockedId : blockedIds) {
                Optional<User> blockedUser = findById(blockedId);
                if (blockedUser.isPresent()) {
                    blockedUsers.add(blockedUser.get());
                }
            }
        }
        return blockedUsers;
    }

    @Override
    public List<String> getReceivedFriendRequests(String userId) {
        List<String> receivedRequests = new ArrayList<>();
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            receivedRequests = user.get().getReceivedFriendRequests();
        }
        return receivedRequests;
    }

    @Override
    public List<String> getPostsByUserId(String userId) {
        List<String> postIds = new ArrayList<>();
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            postIds = user.get().getPosts();
        }
        return postIds;
    }

    @Override
    public List<String> getStoriesByUserId(String userId) {
        List<String> storyIds = new ArrayList<>();
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            storyIds = user.get().getStories();
        }
        return storyIds;
    }
}