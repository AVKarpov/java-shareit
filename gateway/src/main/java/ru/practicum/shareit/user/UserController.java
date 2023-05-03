package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

	private final UserClient userClient;

	@GetMapping
	public ResponseEntity<Object> getAllUsers() {
		log.info("Get all users");
		return userClient.getAllUsers();
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable Long id) {
		log.info("Get user by id = {}", id);
		return userClient.getUserById(id);
	}

	@PostMapping
	public ResponseEntity<Object> createUser(@RequestBody @Validated UserDto userDto) {
		log.info("Create new user: {}", userDto);
		return userClient.createUser(userDto);
	}

	@PatchMapping(value = "/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id,
							  @RequestBody UserDto userDto) {
		log.info("Update user with id = {} : {}", id, userDto);
		return userClient.updateUser(id, userDto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		log.info("Delete user with id = {}", id);
		return userClient.deleteUser(id);
	}

}