package edu.uclm.esi.tysweb2023.dao;

import org.springframework.data.repository.CrudRepository;
import edu.uclm.esi.tysweb2023.model.Match;

public interface MatchDAO extends CrudRepository<Match, String> {
	Match findByIdUser(String userId);
}
