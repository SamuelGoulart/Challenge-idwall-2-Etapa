package br.com.idwall.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.idwall.domain.ResponseBody;
import br.com.idwall.domain.User;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.service.impl.UserServiceImpl;
import br.com.idwall.util.HttpResponse;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@GetMapping("/user")
	public ResponseEntity<ResponseBody> getAllUsers() {
		try {
			return HttpResponse.ok("Usuários listados com sucesso", userServiceImpl.getAll());
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.NO_USER_FOUND:
				return HttpResponse.notFound(Exceptions.NO_USER_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<ResponseBody> getCliente(@PathVariable int id) {
		try {
			return HttpResponse.ok("Usuário(a) listado com sucesso", userServiceImpl.getById(id));
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.USER_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.USER_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

	@PostMapping("/user")
	public ResponseEntity<ResponseBody> create(@RequestBody @Valid User user) {
		try {
			return HttpResponse.created("Usuário(a) criado com sucesso", userServiceImpl.create(user));
		} catch (Exception error) {
			return HttpResponse.serverError();
		}
	}

	@DeleteMapping("/user/{id}")
	public ResponseEntity<ResponseBody> delete(@PathVariable int id) {
		try {
			User user = userServiceImpl.getById(id);
			userServiceImpl.delete(user.getUserId());
			
			return HttpResponse.ok("Usuário(a) deletado com sucesso", new JSONObject());
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.USER_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.USER_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}
}
