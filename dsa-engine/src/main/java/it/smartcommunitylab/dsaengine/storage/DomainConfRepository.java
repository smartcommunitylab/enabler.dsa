package it.smartcommunitylab.dsaengine.storage;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.dsaengine.model.DomainConf;

public interface DomainConfRepository extends MongoRepository<DomainConf, String>,
	DomainConfRepositoryCustom {
	
	List<DomainConf> findAll(Sort sort);
	
	@Query(value="{id:?0}")
	DomainConf findById(String domain);
	
	@Query(value="{managers.email:?0}")
	List<DomainConf> findByManager(String email);
	
	@Query(value="{users.email:?0}")
	List<DomainConf> findByUser(String email);	
	
}
