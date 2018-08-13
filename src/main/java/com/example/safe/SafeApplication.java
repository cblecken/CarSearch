package com.example.safe;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.safe.adapter.ElasticsearchConfig;
import com.example.safe.dao.CarDao;
import com.example.safe.dao.CarDocDao;
import com.example.safe.dao.entity.Car;
import com.example.safe.dao.entity.CarDoc;

/**
 * @author cblecken
 *
 * Primary spring boot application initialization routine. It also has the 
 * initialization logic in the run() routine. 
 */
@EnableJpaRepositories("com.example.safe.dao") 
@EntityScan("com.example.safe.dao.entity")
@SpringBootApplication
public class SafeApplication extends SpringBootServletInitializer implements CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(SafeApplication.class);

	@Autowired
	private CarDao carDao;
	
	@Autowired
	private CarDocDao carDocDao;

	public static void main(String[] args) {
		SpringApplication.run(SafeApplication.class, args);
	}
	
	////////////////////////////////////////////////////////////
	// Primary Logic : The application when coming up will 
	//  delete the elastic search index (search index much 
	//  harder to keep in sync), and then pull the car data
	//  out of DB, create and populate the index
	////////////////////////////////////////////////////////////
	@Override
	public void run(String... args) throws Exception {
		
		this.carDocDao.deleteCarIndex();
		LOG.info("Deleted the cars ES index");		
		
		List<Car> cList = carDao.findAll();
		LOG.info("Retrieved cars from database : number =" + cList.size());
		
		this.carDocDao.insertCarList(convert(cList));
		LOG.info("Added cars to new index 'cars' -- Ready for queries");

	}

	// convert from JPA car entity to ES document entity
	private List<CarDoc> convert(List<Car> cList) {
		ArrayList<CarDoc> carList = new ArrayList<CarDoc>();
		for (Car car : cList) {
			carList.add(new CarDoc(car.getId(),car.getYear(),car.getMake(),car.getModel()));
		}
		return carList;
	}
}
