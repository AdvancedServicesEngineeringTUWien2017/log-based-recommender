package model;

public class RecommenderRequest {

	private String userId;
	private String nbOfItems;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNbOfItems() {
		return nbOfItems;
	}
	public void setNbOfItems(String nbOfItems) {
		this.nbOfItems = nbOfItems;
	}


}
