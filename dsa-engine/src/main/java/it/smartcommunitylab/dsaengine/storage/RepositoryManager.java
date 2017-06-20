package it.smartcommunitylab.dsaengine.storage;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.model.DataSetConf;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class RepositoryManager {
	
	@Autowired
	private DataSetConfRepository dataSetConfRepository;
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;
	
	public RepositoryManager(MongoTemplate mongoTemplate, String defaultLang) {
		this.mongoTemplate = mongoTemplate;
		this.defaultLang = defaultLang;
	}

	public DataSetConf addDataSetConf(DataSetConf conf) throws StorageException {
		DataSetConf dataSetConfDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
		if(dataSetConfDb != null) {
			throw new StorageException("entity already present");
		}
		Date now = new Date();
		conf.setId(Utils.getUUID());
		conf.setCreationDate(now);
		conf.setLastUpdate(now);
		dataSetConfRepository.save(conf);
		return conf;
	}
	
	public DataSetConf updateDataSetConf(DataSetConf conf) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
		if(confDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setArchivePolicy(conf.getArchivePolicy());
		confDb.setClients(conf.getClients());
		confDb.setDataMapping(conf.getDataMapping());
		confDb.setIndexFormat(conf.getIndexFormat());
		dataSetConfRepository.save(confDb);
		return confDb;
	}
	
	public DataSetConf removeDataSetConf(String domain, String dataset) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(domain, dataset);
		if(confDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		dataSetConfRepository.delete(confDb);
		return confDb;
	}

	public DataSetConf getDataSetConf(String domain, String dataset) throws EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(domain, dataset);
		if(confDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		return confDb;
	}

	public List<DataSetConf> getAllDataSetConf() {
		return dataSetConfRepository.findAll();
	}

	public void updateLastCheck(String id, Date lastCheck) throws EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findOne(id);
		if(confDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setLastCheck(lastCheck);
		dataSetConfRepository.save(confDb);
	}

}
