package it.smartcommunitylab.dsaengine.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.model.BaseDataSetConf;
import it.smartcommunitylab.dsaengine.model.BaseDomainConf;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.DomainConf;

public class RepositoryManager {
	
	@Autowired
	private DataSetConfRepository dataSetConfRepository;

	@Autowired
	private DomainConfRepository domainConfRepository;	
	
	// TODO default from domain?
	public DataSetConf addDataSetConf(DataSetConf conf) throws StorageException {
		DataSetConf dataSetConfDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
		if(dataSetConfDb != null) {
			throw new StorageException("Dataset already present");
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
	
//	public DataSetConf updateDataSetConf(DataSetConf conf) throws StorageException, EntityNotFoundException {
//		DataSetConf confDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
//		if(confDb == null) {
//			throw new EntityNotFoundException("Dataset not found");
//		}
//		Date now = new Date();
//		confDb.setLastUpdate(now);
//		confDb.setConfigurationProperties(conf.getConfigurationProperties());
////		confDb.setArchivePolicy(conf.getArchivePolicy());
////		confDb.setClients(conf.getClients());
////		confDb.setDataMapping(conf.getDataMapping());
////		confDb.setIndexFormat(conf.getIndexFormat());
//		dataSetConfRepository.save(confDb);
//		return confDb;
//	}
	
	public DataSetConf updateDataSetConf(BaseDataSetConf baseConf, String domain, String datasetId) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findById(domain, datasetId);
		if(confDb == null) {
			throw new EntityNotFoundException("Dataset not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setConfigurationProperties(baseConf.getConfigurationProperties());
		dataSetConfRepository.save(confDb);
		return confDb;
	}	
	
	public DataSetConf updateDataSetConfUsers(DataSetConf conf) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(conf.getDomain(), conf.getDataset());
		if(confDb == null) {
			throw new EntityNotFoundException("Dataset not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setUsers(conf.getUsers());
		dataSetConfRepository.save(confDb);
		return confDb;
	}	
	
	public DataSetConf removeDataSetConf(String domain, String dataset) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findByDataset(domain, dataset);
		if(confDb == null) {
			throw new EntityNotFoundException("Dataset not found");
		}
		dataSetConfRepository.delete(confDb);
		return confDb;
	}

	public DataSetConf removeDataSetConfById(String domain, String datasetId) throws StorageException, EntityNotFoundException {
		DataSetConf confDb = dataSetConfRepository.findById(domain, datasetId);
		if(confDb == null) {
			throw new EntityNotFoundException("Dataset not found");
		}
		dataSetConfRepository.delete(confDb);
		return confDb;
	}	
	
	public List<DataSetConf> getDataSetConf(String domain, String token, String email) {
		if(Utils.isNotEmpty(email)) {
			List<DataSetConf> result = dataSetConfRepository.findByDomain(domain);
			DomainConf dom = domainConfRepository.findByDomain(domain);
			Set<String> ds = dom.getUsers().stream().filter(x -> email.equals(x.getEmail())).map(y -> y.getDataset()).collect(Collectors.toSet());
			result = result.stream().filter(x -> ds.contains(x.getDataset())).collect(Collectors.toList());
			
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
			throw new EntityNotFoundException("Dataset not found");
		}
		Date now = new Date();
		confDb.setLastUpdate(now);
		confDb.setLastCheck(lastCheck);
		dataSetConfRepository.save(confDb);
	}
	
/*	public ExternalUser findByEmail(String email) {
		return externalUserRepository.findByEmail(email);
	}
	
	public List<ExternalUser> findByDomain(String domain) {
		return externalUserRepository.findByDomain(domain);
	}
	
	public List<ExternalUser> findByDomain(String domain, String dataset) {
		return externalUserRepository.findByDataset(domain, dataset);
	}*/
	
	
	public DomainConf addDomainConf(DomainConf conf) throws StorageException {
		DomainConf domainConfDb = domainConfRepository.findByDomain(conf.getDomain());
		if(domainConfDb != null) {
			throw new StorageException("Domain already present");
		}
		Date now = new Date();
		conf.setId(Utils.getUUID());
		String elasticUser = "dsa_" + conf.getDomain(); 
		conf.setElasticUser(elasticUser.toLowerCase());
		conf.setElasticPassword(RandomStringUtils.randomAlphanumeric(12));
		conf.setCreationDate(now);
		conf.setLastUpdate(now);
		domainConfRepository.save(conf);
		return conf;
	}	
	
//	public DomainConf updateDomainConf(DomainConf conf) throws StorageException, EntityNotFoundException {
//		DomainConf domainConfDb = domainConfRepository.findByDomain(conf.getDomain());
//		if(domainConfDb == null) {
//			throw new EntityNotFoundException("Domain not found");
//		}
//		Date now = new Date();
//		domainConfDb.setLastUpdate(now);
//		domainConfDb.setDefaultConfigurationProperties(conf.getDefaultConfigurationProperties());
//		domainConfRepository.save(domainConfDb);
//		return domainConfDb;
//	}	
	
	public DomainConf updateDomainConf(BaseDomainConf baseConf, String domain) throws StorageException, EntityNotFoundException {
		DomainConf domainConfDb = domainConfRepository.findByDomain(domain);
		if(domainConfDb == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		Date now = new Date();
		domainConfDb.setLastUpdate(now);
		domainConfDb.setDefaultConfigurationProperties(baseConf.getDefaultConfigurationProperties());
		domainConfRepository.save(domainConfDb);
		return domainConfDb;
	}		
	
	public DomainConf updateDomainConfUsers(DomainConf conf) throws StorageException, EntityNotFoundException {
		DomainConf domainConfDb = domainConfRepository.findByDomain(conf.getDomain());
		if(domainConfDb == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		Date now = new Date();
		domainConfDb.setLastUpdate(now);
		domainConfDb.setUsers(conf.getUsers());
		domainConfRepository.save(domainConfDb);
		return domainConfDb;
	}	
	
	public DomainConf updateDomainConfManagers(DomainConf conf) throws StorageException, EntityNotFoundException {
		DomainConf domainConfDb = domainConfRepository.findByDomain(conf.getDomain());
		if(domainConfDb == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		Date now = new Date();
		domainConfDb.setLastUpdate(now);
		domainConfDb.setManagers(conf.getManagers());
		domainConfRepository.save(domainConfDb);
		return domainConfDb;
	}		
	
	public DomainConf removeDomainConf(String domain) throws StorageException, EntityNotFoundException {
		DomainConf domainConfDb = domainConfRepository.findByDomain(domain);
		if(domainConfDb == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		domainConfRepository.delete(domainConfDb);
		return domainConfDb;
	}	
	
	public DomainConf getDomainConf(String domain) {
		DomainConf result = domainConfRepository.findByDomain(domain);
		return result;
	}	
	

}
