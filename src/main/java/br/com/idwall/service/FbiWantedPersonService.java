package br.com.idwall.service;

import java.util.List;

import br.com.idwall.domain.FbiPerson;

public interface FbiWantedPersonService {
    List<FbiPerson> getAll();
    
    FbiPerson create(FbiPerson person);
    
    FbiPerson getById(int id);
}
