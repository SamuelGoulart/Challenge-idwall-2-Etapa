package br.com.idwall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.idwall.domain.InterpolPerson;

public interface InterpolWantedPersonRepository  extends JpaRepository<InterpolPerson, Integer> {

}
