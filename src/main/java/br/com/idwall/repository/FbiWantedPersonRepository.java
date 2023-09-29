package br.com.idwall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.idwall.domain.FbiPerson;

public interface FbiWantedPersonRepository extends JpaRepository<FbiPerson, Integer> {

}
