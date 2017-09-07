package it.smartcommunitylab.dsa.client.common.model;

import it.smartcommunitylab.dsa.client.common.Utils;

import java.util.Date;
import java.util.Objects;

public class BaseObject {
	private String id;
	private Date creationDate;
	private Date lastUpdate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false; 
		if(o instanceof BaseObject) {
			BaseObject object = (BaseObject) o;
			if(Utils.isNotEmpty(object.getId())) {
				if(object.getId().equals(id)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	@Override
  public int hashCode() {
      return Objects.hash(id);
  }


}
