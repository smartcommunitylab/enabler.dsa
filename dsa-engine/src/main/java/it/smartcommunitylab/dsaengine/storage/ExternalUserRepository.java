package it.smartcommunitylab.dsaengine.storage;

import it.smartcommunitylab.dsaengine.model.ExternalUser;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ExternalUserRepository extends MongoRepository<ExternalUser, String>,
	ExternalUserRepositoryCustom {
	
	List<ExternalUser> findAll(Sort sort);
	
	@Query(value="{email:?0}")
	ExternalUser findByEmail(String email);
	
}
