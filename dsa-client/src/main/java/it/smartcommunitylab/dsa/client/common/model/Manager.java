package it.smartcommunitylab.dsa.client.common.model;

public class Manager {

	private String id;
	
	private String email;	
	
	private boolean owner;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (owner ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Manager other = (Manager) obj;
		if (owner != other.owner)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Manager [id=" + id + ", email=" + email + ", owner=" + owner + "]";
	}	
	
}
