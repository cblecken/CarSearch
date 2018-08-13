package com.example.safe.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.safe.dao.CarDocDao;

/**
 * @author cblecken
 * 
 * exposed REST API
 * 
 * Provides two operations to query the cars by year and by make. 
 * The query string itself will be entered as a http parameter
 * 
 * sample : http://localhost:5000/api/make?query=Chevrolet
 *
 */
@RestController
@RequestMapping("/api")
public class CarController {
	
    @Autowired
    private CarDocDao carES2Dao;
	
    @GetMapping("/make")    
    public List findByMake(@RequestParam(value = "query", required = true) String query) {
        return carES2Dao.findByMake(query);
    }
    
    @GetMapping("/year")
    public List findByYear(@RequestParam(value = "query", required = true) String query) {
        return carES2Dao.findByYear(query);
    }
}
