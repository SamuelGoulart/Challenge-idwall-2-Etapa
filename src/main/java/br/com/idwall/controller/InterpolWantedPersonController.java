package br.com.idwall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<ResponseBody> getAllUsers() {
		try {
			
			return HttpResponse.ok("Pessoas procuradas com sucesso", interpolWantedPersonServiceImpl.getAll());
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.INTERPOL_WANTED_PERSON_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.INTERPOL_WANTED_PERSON_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

}
