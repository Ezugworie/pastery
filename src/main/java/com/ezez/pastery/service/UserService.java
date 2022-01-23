package com.ezez.pastery.service;


import com.ezez.pastery.model.user.User;
import com.ezez.pastery.payload.UserIdentityAvailability;
import com.ezez.pastery.payload.UserProfile;
import com.ezez.pastery.payload.UserSummary;
import com.ezez.pastery.payload.request.InfoRequest;
import com.ezez.pastery.payload.response.ApiResponse;
import com.ezez.pastery.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

	UserSummary getCurrentUser(UserPrincipal currentUser);

	UserIdentityAvailability checkUsernameAvailability(String username);

	UserIdentityAvailability checkEmailAvailability(String email);

	UserProfile getUserProfile(String username);

	User addUser(User user);

	User updateUser(User newUser, String username, UserPrincipal currentUser);

	ApiResponse deleteUser(String username, UserPrincipal currentUser);

	ApiResponse giveAdmin(String username);

	ApiResponse removeAdmin(String username);

	UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest);

}