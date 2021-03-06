package it.smartcommunitylab.dsaengine.kibana.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.dsaengine.model.DomainConf;
import it.smartcommunitylab.dsaengine.storage.DomainConfRepository;

@Component
public class RolesUtils {

	@Autowired
	private DomainConfRepository domainRepo;

	public boolean isManagerManagementAllowed(String domain, String username) {
		DomainConf conf = domainRepo.findById(domain);
		if (conf != null) {
			return conf.getManagers().stream().filter(x -> username.equals(x.getUsername()) && x.isOwner()).findFirst().isPresent();
		}
		return false;
	}

	public boolean isDatasetManagementAllowed(String domain, String username) {
		DomainConf conf = domainRepo.findById(domain);
		if (conf != null) {
			return conf.getManagers().stream().filter(x -> username.equals(x.getUsername())).findFirst().isPresent();
		}
		return false;
	}

	public boolean isUserManagementAllowed(String domain, String username) {
		DomainConf conf = domainRepo.findById(domain);
		if (conf != null) {
			return conf.getManagers().stream().filter(x -> username.equals(x.getUsername())).findFirst().isPresent();
		}
		return false;
	}


	public boolean isDomainManager(String domain,String username) {
		DomainConf conf = domainRepo.findById(domain);
		if (conf != null) {
			return conf.getManagers().stream().filter(x -> username.equals(x.getUsername())).findFirst().isPresent();
		}
		return false;	
	}		
	
	public boolean isDatasetUser(String domain, String dataset, String username) {
		DomainConf conf = domainRepo.findById(domain);
		if (conf != null) {
			return conf.getUsers().stream().filter(x -> username.equals(x.getUsername()) && dataset.equals(x.getDataset())).findFirst().isPresent();
		}
		return false;		
	}

	
	
	
	
}
