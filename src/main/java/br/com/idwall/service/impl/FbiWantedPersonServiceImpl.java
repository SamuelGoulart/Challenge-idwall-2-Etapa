package br.com.idwall.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.idwall.domain.FbiPerson;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.exception.GenericException;
import br.com.idwall.repository.FbiWantedPersonRepository;
import br.com.idwall.service.FbiWantedPersonService;

@Service
public class FbiWantedPersonServiceImpl implements FbiWantedPersonService {

    @Autowired
    FbiWantedPersonRepository fbiWantedPersonRepository;

    @Override
    public List<FbiPerson> getAll() {
        List<FbiPerson> persons = fbiWantedPersonRepository.findAll();

        if (persons.isEmpty()) {
            throw new GenericException(Exceptions.FBI_WANTED_PERSON_NOT_FOUND);
        }

        return persons;
    }

    @Override
    public FbiPerson create(FbiPerson person) {
        return fbiWantedPersonRepository.save(person);
    }

    @Override
    public FbiPerson getById(int id) {
        Optional<FbiPerson> optionalUser = fbiWantedPersonRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new GenericException(Exceptions.WANTED_BY_FBI_NOT_FOUND);
        }
            
        return optionalUser.get();
    }
}

