package it.smartcommunitylab.dsaengine.scheduled;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.dsaengine.common.Const;
import it.smartcommunitylab.dsaengine.elastic.ElasticManager;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;

@Component
public class CheckIndex {
	private static final transient Logger logger = LoggerFactory.getLogger(CheckIndex.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ElasticManager elasticManager;

	@Scheduled(cron = "0 0 1 * * *") // second, minute, hour, day, month, weekday
	//@Scheduled(cron = "0 */1 * * * *") // second, minute, hour, day, month, weekday
	public void ckeckIndexToClose() {
		if(logger.isInfoEnabled()) {
			logger.info("ckeckIndexToClose");
		}
		List<DataSetConf> listConf = dataManager.getAllDataSetConf();
		Date now = new Date();
		for(DataSetConf conf : listConf) {
			try {
				List<String> indexes = elasticManager.getIndexes(conf.getDomain(), conf.getDataset());
				for(String indexName : indexes) {
					if(logger.isInfoEnabled()) {
						logger.info(String.format("check index %s", indexName));
					}
					Date indexDate = getIndexDate(indexName, conf);
					if(indexDate != null) {
						Date archiveDate = getArchiveDate(indexDate, conf);
						if(now.after(archiveDate)) {
							if(logger.isInfoEnabled()) {
								logger.info(String.format("close index %s", indexName));
							}
							elasticManager.closeIndex(indexName);
							dataManager.updateLastCheck(conf.getId(), now);
						}
					}
				}
			} catch (Exception e) {
				logger.warn("Exception:" + e.getMessage());
			}
		}
	}
	
	private Date getIndexDate(String indexName, DataSetConf conf) throws ParseException {
		if(conf.getConfigurationProperties().getIndexFormat().equals(Const.IDX_MONTHLY)) {
			String indexDate = indexName.substring(indexName.length() 
					- elasticManager.indexDateMonthly.length());
			return elasticManager.sdfMonthly.parse(indexDate);
		} else if(conf.getConfigurationProperties().getIndexFormat().equals(Const.IDX_WEEKLY)) {
			String indexDate = indexName.substring(indexName.length() 
					- elasticManager.indexDateWeekly.length());
			return elasticManager.sdfWeekly.parse(indexDate);
		}
		return null;
	}
	
	private Date getArchiveDate(Date indexDate, DataSetConf conf) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(indexDate);
		int offset = Integer.valueOf(conf.getConfigurationProperties().getArchivePolicy().
				substring(0, conf.getConfigurationProperties().getArchivePolicy().length() - 1));
		if(conf.getConfigurationProperties().getArchivePolicy().endsWith("M")) {
			cal.add(Calendar.MONTH, offset);
		} else if(conf.getConfigurationProperties().getArchivePolicy().endsWith("D")) {
			cal.add(Calendar.DATE, offset);
		}
		return cal.getTime();
	}

}
