package br.com.idwall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.idwall.domain.ResponseBody;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.service.impl.InterpolWantedPersonServiceImpl;
import br.com.idwall.util.HttpResponse;

@RestController
@RequestMapping("/api/v1")
public class InterpolWantedPersonController {
	
	@Autowired
	private InterpolWantedPersonServiceImpl interpolWantedPersonServiceImpl;
	
	@GetMapping("/interpol/persons/wanted")
	public ResponseEntity<ResponseBody> getAllPersons() {
		try {
			
			return HttpResponse.ok("Pessoas procuradas pela Interpol listado com sucesso", interpolWantedPersonServiceImpl.getAll());
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.INTERPOL_WANTED_PERSON_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.INTERPOL_WANTED_PERSON_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}
	
	@GetMapping("/interpol/persons/{id}/wanted")
	public ResponseEntity<ResponseBody> getPersonById(@PathVariable int id) {
		try {
			return HttpResponse.ok("Pessoas procuradas pelo Interpol listado com sucesso", interpolWantedPersonServiceImpl.getById(id));
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.WANTED_BY_INTERPOL_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.WANTED_BY_INTERPOL_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

}
