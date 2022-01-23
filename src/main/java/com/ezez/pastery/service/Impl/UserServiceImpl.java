package com.ezez.pastery.service.Impl;


import com.ezez.pastery.exception.*;
import com.ezez.pastery.model.role.Role;
import com.ezez.pastery.model.role.RoleName;
import com.ezez.pastery.model.user.Address;
import com.ezez.pastery.model.user.User;
import com.ezez.pastery.payload.UserIdentityAvailability;
import com.ezez.pastery.payload.UserProfile;
import com.ezez.pastery.payload.UserSummary;
import com.ezez.pastery.payload.request.InfoRequest;
import com.ezez.pastery.payload.response.ApiResponse;
import com.ezez.pastery.repository.RoleRepository;
import com.ezez.pastery.repository.UserRepository;
import com.ezez.pastery.security.UserPrincipal;
import com.ezez.pastery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserSummary getCurrentUser(UserPrincipal currentUser) {
		return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(),
				currentUser.getLastName());
	}

	@Override
	public UserIdentityAvailability checkUsernameAvailability(String username) {
		Boolean isAvailable = !userRepository.existsByUsername(username);
		return new UserIdentityAvailability(isAvailable);
	}

	@Override
	public UserIdentityAvailability checkEmailAvailability(String email) {
		Boolean isAvailable = !userRepository.existsByEmail(email);
		return new UserIdentityAvailability(isAvailable);
	}

	@Override
	public UserProfile getUserProfile(String username) {
		User user = userRepository.getUserByName(username);


		return new UserProfile(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
				user.getCreatedAt(), user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite());
	}

	@Override
	public User addUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken");
			throw new BadRequestException(apiResponse);
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Email is already taken");
			throw new BadRequestException(apiResponse);
		}

		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User newUser, String username, UserPrincipal currentUser) {
		User user = userRepository.getUserByName(username);
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			user.setAddress(newUser.getAddress());
			user.setPhone(newUser.getPhone());
			user.setWebsite(newUser.getWebsite());

			return userRepository.save(user);

		}

		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + username);
		throw new UnauthorizedException(apiResponse);

	}

	@Override
	public ApiResponse deleteUser(String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
		if (!user.getId().equals(currentUser.getId()) || !currentUser.getAuthorities()
				.contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + username);
			throw new AccessDeniedException(apiResponse);
		}

		userRepository.deleteById(user.getId());

		return new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + username);
	}

	@Override
	public ApiResponse giveAdmin(String username) {
		User user = userRepository.getUserByName(username);
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
				.orElseThrow(() -> new AppException("User role not set")));
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);
		userRepository.save(user);
		return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);
	}

	@Override
	public ApiResponse removeAdmin(String username) {
		User user = userRepository.getUserByName(username);
		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);
		userRepository.save(user);
		return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);
	}

	@Override
	public UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
				infoRequest.getZipcode());
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setAddress(address);
			user.setPhone(infoRequest.getPhone());
			User updatedUser = userRepository.save(user);


			return new UserProfile(updatedUser.getId(), updatedUser.getUsername(),
					updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getCreatedAt(),
					updatedUser.getEmail(), updatedUser.getAddress(), updatedUser.getPhone(), updatedUser.getWebsite());
		}

		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update users profile", HttpStatus.FORBIDDEN);
		throw new AccessDeniedException(apiResponse);
	}
}
