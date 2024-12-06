package com.socialnetwork.connecthub.backend.service.java;

import com.socialnetwork.connecthub.backend.interfaces.services.NewsFeedService;
import com.socialnetwork.connecthub.backend.model.User;
import com.socialnetwork.connecthub.backend.persistence.json.JsonBlockRepository;
import com.socialnetwork.connecthub.backend.persistence.json.JsonUserRepository;
import com.socialnetwork.connecthub.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class JavaNewsFeedService implements NewsFeedService {
    @Override
    public List<UserDTO> getOnlineFriends(String userId) {
        List<User> friends = getFriends(userId);
        List<UserDTO> onlineFriends = new ArrayList<>();
        for (User friend : friends) {
            if (friend.isOnlineStatus()) {
                onlineFriends.add(
                    new UserDTO(
                        friend.getUserId(),
                        friend.getUsername(),
                        friend.getProfilePhotoPath(),
                        friend.getCoverPhotoPath(),
                        friend.getBio(), true
                    )
                );
            }
        }
        return onlineFriends;
    }

    @Override
    public List<UserDTO> getFriendSuggestions(String userId) {
        List<User> friends = getFriends(userId);
        List<UserDTO> suggestions = new ArrayList<>();
        for (User friend : friends) {
            List<User> friendsOfFriend = getFriends(friend.getUserId());
            for (User secondFriend : friendsOfFriend) {
                suggestions.add(
                    new UserDTO(
                        secondFriend.getUserId(),
                        secondFriend.getUsername(),
                        secondFriend.getProfilePhotoPath(),
                        secondFriend.getCoverPhotoPath(),
                        secondFriend.getBio(), true
                    )
                );
            }
        }

        return suggestions;
    }

    private List<User> getFriends(String userId) {
        // Get the user's friends
        User user = JsonUserRepository.getInstance().findById(userId).orElseThrow();
        List<User> friends = new ArrayList<>();

        // Retrieve friends using a for loop
        for (String friendId : user.getFriends()) {
            User friend = JsonUserRepository.getInstance().findById(friendId).orElseThrow();
            // Don't get content from blocked users in the content service
            if(JsonBlockRepository.getInstance().findByIds(userId, friendId).isEmpty())
                friends.add(friend);
        }

        return friends;
    }

}