package br.com.idwall.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.idwall.domain.User;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.exception.GenericException;
import br.com.idwall.repository.UserRepository;
import br.com.idwall.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	public User getById(int id) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (!optionalUser.isPresent()) {
			throw new GenericException(Exceptions.USER_NOT_FOUND);
		}

		return optionalUser.get();
	}

	public List<User> getAll() {
		List<User> users = userRepository.findAll();

		if(users.isEmpty()) {
			throw new GenericException(Exceptions.NO_USER_FOUND);
		}
		
		return users;
	}

	public User create(User user) {
		return userRepository.save(user);
	}

	public void delete(int id) {
		userRepository.deleteById(id);
	}
}
