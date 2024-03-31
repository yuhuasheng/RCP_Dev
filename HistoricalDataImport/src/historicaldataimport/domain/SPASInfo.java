package historicaldataimport.domain;

public class SPASInfo {

	private String spasId;
	private String name;
	
	public String getSpasId() {
		return spasId;
	}
	public void setSpasId(String spasId) {
		this.spasId = spasId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Series2Info [spasId=" + spasId + ", name=" + name + "]";
	}
	
}
