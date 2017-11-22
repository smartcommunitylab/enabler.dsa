package it.smartcommunitylab.dsaengine.storage;

import it.smartcommunitylab.dsaengine.model.ExternalUser;

import java.util.List;

public interface ExternalUserRepositoryCustom {
	
	List<ExternalUser> findByDomain(String domain);
	
	List<ExternalUser> findByDataset(String domain, String dataset);
}
