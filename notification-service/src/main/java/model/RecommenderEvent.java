package model;

public class RecommenderEvent {
 private String eventType;
 private String entityType;
 private String entityId;
 private String eventTime;
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
public String getEventTime() {
	return eventTime;
}
public void setEventTime(String eventTime) {
	this.eventTime = eventTime;
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
public void setTargetEventType(String targetEventType) {
	this.targetEntityType = targetEventType;
}
}
