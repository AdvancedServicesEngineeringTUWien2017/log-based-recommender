package model;

import java.util.Date;

public class Event {
	
	private Date time;
	private int country;
	private int region;
	private int city;
	private double distance;
	private String user_id;
	private boolean is_mobile;
	private boolean is_package;
	private Date checkin;
	private Date checkout;
	private String destination_id;
	private int destination_type_id;
	private int hotel_continent;
	private int hotel_country;
	private int hotel_market;
	private boolean booking;
	private int hotel_cluster;
	private int similar_events;
    
	
	public Event (Date time, int country, int region, int city, 
			double distance,String user_id,boolean is_mobile, boolean is_package,
			Date checkin, Date checkout, String destination_id, int destination_type_id,
			int hotel_continent, int hotel_country,int hotel_market, boolean booking,
			int hotel_cluster, int similar_events){
		this.time=time;
		this.country=country;
		this.region=region;
		this.city=city;
		this.distance=distance;
		this.user_id=user_id;
		this.is_mobile=is_mobile;
		this.is_package=is_package;
		this.checkin=checkin;
		this.checkout=checkout;
		this.destination_id=destination_id;
		this.destination_type_id=destination_type_id;
		this.hotel_continent=hotel_continent;
		this.hotel_country=hotel_country;
		this.hotel_market=hotel_market;
		this.booking=booking;
		this.hotel_cluster=hotel_cluster;
		this.similar_events=similar_events;
	}
	
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getCountry() {
		return country;
	}
	public void setCountry(int country) {
		this.country = country;
	}
	public int getRegion() {
		return region;
	}
	public void setRegion(int region) {
		this.region = region;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public boolean isIs_mobile() {
		return is_mobile;
	}
	public void setIs_mobile(boolean is_mobile) {
		this.is_mobile = is_mobile;
	}
	public boolean isIs_package() {
		return is_package;
	}
	public void setIs_package(boolean is_package) {
		this.is_package = is_package;
	}
	public Date getCheckin() {
		return checkin;
	}
	public void setCheckin(Date checkin) {
		this.checkin = checkin;
	}
	public Date getCheckout() {
		return checkout;
	}
	public void setCheckout(Date checkout) {
		this.checkout = checkout;
	}
	public String getDestination_id() {
		return destination_id;
	}
	public void setDestination_id(String destination_id) {
		this.destination_id = destination_id;
	}
	public int getDestination_type_id() {
		return destination_type_id;
	}
	public void setDestination_type_id(int destination_type_id) {
		this.destination_type_id = destination_type_id;
	}
	public int getHotel_continent() {
		return hotel_continent;
	}
	public void setHotel_continent(int hotel_continent) {
		this.hotel_continent = hotel_continent;
	}
	public int getHotel_country() {
		return hotel_country;
	}
	public void setHotel_country(int hotel_country) {
		this.hotel_country = hotel_country;
	}
	public int getHotel_market() {
		return hotel_market;
	}
	public void setHotel_market(int hotel_market) {
		this.hotel_market = hotel_market;
	}
	public boolean isBooking() {
		return booking;
	}
	public void setBooking(boolean booking) {
		this.booking = booking;
	}
	public int getHotel_cluster() {
		return hotel_cluster;
	}
	public void setHotel_cluster(int hotel_cluster) {
		this.hotel_cluster = hotel_cluster;
	}
	public int getSimilar_events() {
		return similar_events;
	}
	public void setSimilar_events(int similar_events) {
		this.similar_events = similar_events;
	}
	
	public String toString(){
		return "User: "+user_id+"booking: "+booking;
		
	}
	
}
