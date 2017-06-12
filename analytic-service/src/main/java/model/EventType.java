package model;

public class EventType {

	private String newIndex;
	private String oldIndex;
	private String mapping;
	private String properties;

	public String getMapping() {
		return mapping;
	}
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String toString(){
	     return "New Index: "+newIndex+", mapping: "+mapping+", properties: "+properties;
	}
	public String getNewIndex() {
		return newIndex;
	}
	public void setNewIndex(String newIndex) {
		this.newIndex = newIndex;
	}
	public String getOldIndex() {
		return oldIndex;
	}
	public void setOldIndex(String oldIndex) {
		this.oldIndex = oldIndex;
	}
}
