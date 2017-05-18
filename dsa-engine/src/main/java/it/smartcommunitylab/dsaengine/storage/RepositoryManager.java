package it.smartcommunitylab.dsaengine.storage;

import org.springframework.data.mongodb.core.MongoTemplate;

public class RepositoryManager {
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;

	public RepositoryManager(MongoTemplate mongoTemplate, String defaultLang) {
		this.mongoTemplate = mongoTemplate;
		this.defaultLang = defaultLang;
	}

}
