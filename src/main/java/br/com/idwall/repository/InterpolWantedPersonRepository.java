package br.com.idwall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.idwall.domain.InterpolPerson;

public interface InterpolWantedPersonRepository  extends JpaRepository<InterpolPerson, Integer> {
    InterpolPerson findByName(String name);

    @Query("SELECT p FROM InterpolPerson p ORDER BY p.personId DESC")
    List<InterpolPerson> findAll();
}
