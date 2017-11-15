package it.smartcommunitylab.dsaengine.storage;

import it.smartcommunitylab.dsaengine.model.DataSetConf;

import java.util.List;

public interface DataSetConfRepositoryCustom {
	List<DataSetConf> findByUserEmail(String domain, String email);
}
