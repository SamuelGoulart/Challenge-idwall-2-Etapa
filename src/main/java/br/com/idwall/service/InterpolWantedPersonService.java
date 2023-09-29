package br.com.idwall.service;

import java.util.List;

import br.com.idwall.domain.InterpolPerson;

public interface InterpolWantedPersonService {
    List<InterpolPerson> getAll();
    
    InterpolPerson create(InterpolPerson person);
    
    InterpolPerson getById(int id);
}
