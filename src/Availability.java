
public class Availability {
	private final String url;
	private Boolean state;
	
	public Availability(String url, Boolean state) {
		this.url = url;
		this.state = state;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Boolean getState() {
		return state;
	}
	
	public void setState(Boolean state) {
		this.state = state;
	}
}
