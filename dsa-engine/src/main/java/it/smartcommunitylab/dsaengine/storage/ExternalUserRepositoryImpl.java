package it.smartcommunitylab.dsaengine.storage;

import it.smartcommunitylab.dsaengine.model.ExternalUser;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ExternalUserRepositoryImpl implements ExternalUserRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ExternalUser> findByDomain(String domain) {
		Criteria criteria = new Criteria("domainRoles.domain").is(domain);
		Query query = new Query(criteria);
		return mongoTemplate.find(query, ExternalUser.class);
	}

	@Override
	public List<ExternalUser> findByDataset(String domain, String dataset) {
		Criteria criteria = new Criteria("datasetRoles.domain").is(domain)
				.and("datasetRoles.dataset").is(dataset);
		Query query = new Query(criteria);
		return mongoTemplate.find(query, ExternalUser.class);
	}

}
