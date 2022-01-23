package com.ezez.pastery.controller;


import com.ezez.pastery.exception.AppException;
import com.ezez.pastery.exception.BadRequestException;
import com.ezez.pastery.exception.PasteryApiException;
import com.ezez.pastery.exception.RestControllerExceptionHandler;
import com.ezez.pastery.model.role.Role;
import com.ezez.pastery.model.role.RoleName;
import com.ezez.pastery.model.user.User;
import com.ezez.pastery.payload.request.LoginRequest;
import com.ezez.pastery.payload.request.SignUpRequest;
import com.ezez.pastery.payload.response.ApiResponse;
import com.ezez.pastery.payload.response.JwtAuthenticationResponse;
import com.ezez.pastery.repository.RoleRepository;
import com.ezez.pastery.repository.UserRepository;
import com.ezez.pastery.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final String USER_ROLE_NOT_SET = "User role not set";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private String generateUserToken(String usernameOrEmail, String password){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return jwtTokenProvider.generateToken(authentication);
	}

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		String jwt = generateUserToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

//		Authentication authentication = authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
//
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		String jwt = jwtTokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping("/signup")
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws BadRequestException {
		ApiResponse apiResponse = new ApiResponse();
		if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
			apiResponse.setMessage("Username is already taken");
			apiResponse.setSuccess(false);
			apiResponse.setStatus(HttpStatus.BAD_REQUEST);
			throw new BadRequestException(apiResponse);
		}

		if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
			apiResponse.setMessage("Email is already taken");
			apiResponse.setSuccess(false);
			apiResponse.setStatus(HttpStatus.BAD_REQUEST);
			throw new BadRequestException(apiResponse);
		}

		String firstName = signUpRequest.getFirstName().toLowerCase();

		String lastName = signUpRequest.getLastName().toLowerCase();

		String username = signUpRequest.getUsername().toLowerCase();

		String email = signUpRequest.getEmail().toLowerCase();

		String password = passwordEncoder.encode(signUpRequest.getPassword());

		User user = new User(firstName, lastName, username, email, password);

		List<Role> roles = new ArrayList<>();

		if (userRepository.count() == 0) {
			roles.add(roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
			roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
		} else {
			roles.add(roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
		}

		user.setRoles(roles);

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
				.buildAndExpand(result.getId()).toUri();
		String jwt = generateUserToken(signUpRequest.getUsername(), signUpRequest.getPassword());

		return ResponseEntity.created(location)
					.body(new ApiResponse(
							Boolean.TRUE,
							"User registered successfully",
							HttpStatus.OK,
							 new JwtAuthenticationResponse(jwt)));
	}
}
