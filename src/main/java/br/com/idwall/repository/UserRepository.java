package br.com.idwall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.idwall.domain.User;

public interface UserRepository  extends JpaRepository<User, Integer> {

}
