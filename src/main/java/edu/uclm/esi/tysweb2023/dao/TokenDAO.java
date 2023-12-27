package edu.uclm.esi.tysweb2023.dao;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.tysweb2023.model.Token;

public interface TokenDAO extends CrudRepository<Token, String> {

}
