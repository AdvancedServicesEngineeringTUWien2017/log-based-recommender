package model;

public class RecommenderEvent {
 private String mapping;
 private String eventType;
 private String entityType;
 private String entityId;
 private String targetEntityId;
 private String targetEntityType;
 private String properties;
public String getEventType() {
	return eventType;
}
public void setEventType(String eventType) {
	this.eventType = eventType;
}
public String getEntityType() {
	return entityType;
}
public void setEntityType(String entityType) {
	this.entityType = entityType;
}
public String getEntityId() {
	return entityId;
}
public void setEntityId(String entityId) {
	this.entityId = entityId;
}

public String getTargetEntityId() {
	return targetEntityId;
}
public void setTargetEntityId(String targetEntityId) {
	this.targetEntityId = targetEntityId;
}
public String getProperties() {
	return properties;
}
public void setProperties(String properties) {
	this.properties = properties;
}
public String getTargetEntityType() {
	return targetEntityType;
}
public void setTargetEntityType(String targetEventType) {
	this.targetEntityType = targetEventType;
}
public String toString(){
	return "Recommender event, target entity type: "+targetEntityType;

}
public String getMapping() {
	return mapping;
}
public void setMapping(String mapping) {
	this.mapping = mapping;
}
}
