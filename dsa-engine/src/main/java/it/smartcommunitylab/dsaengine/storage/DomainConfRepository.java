package it.smartcommunitylab.dsaengine.storage;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.dsaengine.model.DomainConf;

public interface DomainConfRepository extends MongoRepository<DomainConf, String>,
	DomainConfRepositoryCustom {
	
	List<DomainConf> findAll(Sort sort);
	
	@Query(value="{domain:?0}")
	DomainConf findByDomain(String domain);

}
