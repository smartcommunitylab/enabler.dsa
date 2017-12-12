package it.smartcommunitylab.dsaengine.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class DataSetConfRepositoryImpl implements DataSetConfRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

/*	@Override
	public List<DataSetConf> findByUserEmail(String domain, String email) {
		Criteria criteria = new Criteria("domain").is(domain).and("users.email").is(email);
		Query query = new Query(criteria);
		return mongoTemplate.find(query, DataSetConf.class);
	}*/

}
