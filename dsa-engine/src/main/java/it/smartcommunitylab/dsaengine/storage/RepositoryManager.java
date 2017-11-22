package it.smartcommunitylab.dsaengine.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.ExternalUser;

public class RepositoryManager {
	
	@Autowired
	private DataSetConfRepository dataSetConfRepository;
	
	@Autowired
	private ExternalUserRepository externalUserRepository;
	
	public DataSetConf addDataSetConf(DataSetConf conf) throws StorageException {
		DataSetConf dataSetConfDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
		if(dataSetConfDb != null) {
			throw new StorageException("entity already present");
		}
		Date now = new Date();
		conf.setId(Utils.getUUID());
		String elasticUser = "dsa_" + conf.getDomain() + "_" + conf.getDataset(); 
		conf.setElasticUser(elasticUser.toLowerCase());
		conf.setElasticPassword(RandomStringUtils.randomAlphanumeric(12));
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
	
	public DataSetConf setDataSetConfUsers(String domain, String dataset, List<ExternalUser> users) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(domain, dataset);
		if(confDb == null) {
			throw new EntityNotFoundException("entity not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setUsers(users);
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

	public List<DataSetConf> getDataSetConf(String domain, String token, String email) {
		if(Utils.isNotEmpty(email)) {
			List<DataSetConf> result = dataSetConfRepository.findByUserEmail(domain, email);
			return result;
		}
		return new ArrayList<DataSetConf>();
	}

	public List<DataSetConf> getDataSetConf(String domain) {
		List<DataSetConf> result = dataSetConfRepository.findByDomain(domain);
		return result;
	}
	
	public DataSetConf getDataSetConf(String domain, String dataset) {
		return dataSetConfRepository.findByDataset(domain, dataset);
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
	
	public ExternalUser findByEmail(String email) {
		return externalUserRepository.findByEmail(email);
	}
	
	public List<ExternalUser> findByDomain(String domain) {
		return externalUserRepository.findByDomain(domain);
	}
	
	public List<ExternalUser> findByDomain(String domain, String dataset) {
		return externalUserRepository.findByDataset(domain, dataset);
	}

}
