package com.example.safe.dao.entity;

import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;

// @Document(indexName="cares",type="cares",shards=1,replicas=0,refreshInterval="-1")
public class CarDoc {

	@Id
	private String id;

	private String year;
	private String make;
	private String name;
	
	
	public CarDoc() {
	}
	
	public CarDoc(long id, String year, String make, String name) {
		this.id = Long.toString(id);
		this.year = year;
		this.make = make;
		this.name = name;
	}
	
	public CarDoc(String id, String year, String make, String name) {
		this.id = id;
		this.year = year;
		this.make = make;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
