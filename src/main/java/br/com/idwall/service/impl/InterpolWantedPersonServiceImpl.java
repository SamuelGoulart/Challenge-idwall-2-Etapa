package br.com.idwall.service.impl;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.idwall.domain.InterpolPerson;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.exception.GenericException;
import br.com.idwall.repository.InterpolWantedPersonRepository;
import br.com.idwall.service.InterpolWantedPersonService;

@Service
public class InterpolWantedPersonServiceImpl implements InterpolWantedPersonService {
	
	@Autowired
	InterpolWantedPersonRepository interpolWantedPersonRepository;

	
	public List<InterpolPerson> getAll() {
		List<InterpolPerson> persons = interpolWantedPersonRepository.findAll();

		if(persons.isEmpty()) {
			throw new GenericException(Exceptions.INTERPOL_WANTED_PERSON_NOT_FOUND);
		}
		
		return persons;
	}
	
	public InterpolPerson create(InterpolPerson person) {
		return interpolWantedPersonRepository.save(person);
	}
	

	public InterpolPerson getById(int id) {
		Optional<InterpolPerson> optionalUser = interpolWantedPersonRepository.findById(id);

		if (!optionalUser.isPresent()) {
			throw new GenericException(Exceptions.WANTED_BY_INTERPOL_NOT_FOUND);
		}

		return optionalUser.get();
	}
	
	public Optional<InterpolPerson> getByName(String name) {
		return Optional.ofNullable(interpolWantedPersonRepository.findByName(name));
	}
	
	public void updateById(InterpolPerson person) {
		 interpolWantedPersonRepository.save(person);
	}

}
