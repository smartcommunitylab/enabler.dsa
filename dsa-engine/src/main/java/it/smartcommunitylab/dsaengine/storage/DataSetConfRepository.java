package it.smartcommunitylab.dsaengine.storage;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.dsaengine.model.DataSetConf;

public interface DataSetConfRepository extends MongoRepository<DataSetConf, String>,
	DataSetConfRepositoryCustom {
	
	List<DataSetConf> findAll(Sort sort);
	
	@Query(value="{domain:?0}")
	List<DataSetConf> findByDomain(String domain);
	
	@Query(value="{domain:?0, dataset:?1}")
	DataSetConf findByDataset(String domain, String dataset);
	
	@Query(value="{domain:?0, id:?1}")
	DataSetConf findById(String domain, String datasetId);	
	
}
