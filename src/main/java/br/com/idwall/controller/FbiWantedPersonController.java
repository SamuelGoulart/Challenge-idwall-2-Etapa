package br.com.idwall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.idwall.domain.ResponseBody;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.service.impl.FbiWantedPersonServiceImpl;
import br.com.idwall.util.HttpResponse;

@RestController
@RequestMapping("/api/v1")
public class FbiWantedPersonController {
	
	@Autowired
	private FbiWantedPersonServiceImpl fbiWantedPersonServiceImpl;
	
	
	@GetMapping("/fbi/persons/wanted")
	public ResponseEntity<ResponseBody> getAllPersons() {
		try {
			
			return HttpResponse.ok("Pessoas procuradas encontradas com sucesso", fbiWantedPersonServiceImpl.getAll());
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.FBI_WANTED_PERSON_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.FBI_WANTED_PERSON_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}
	
	@GetMapping("/fbi/persons/{id}/wanted")
	public ResponseEntity<ResponseBody> getPersonById(@PathVariable int id) {
		try {
			return HttpResponse.ok("Usuário(a) listado com sucesso", fbiWantedPersonServiceImpl.getById(id));
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.WANTED_BY_FBI_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.WANTED_BY_FBI_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}
}
