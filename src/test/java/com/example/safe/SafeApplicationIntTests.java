package com.example.safe;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;


import io.restassured.RestAssured;
import io.restassured.response.Response;



@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.NONE)
public class SafeApplicationIntTests {

    private static final String API_ROOT = "http://localhost:5000/api";
	private String MAKEQUERY = "Chevrolet";
	private String YEARQUERY = "2000";

    @Test
    public void simpleQueryByMake_thenOK() {
        final Response response = RestAssured.get(API_ROOT + "/make?query=" + MAKEQUERY );
        System.out.println(response.getStatusCode() + " " + response.as(List.class).size());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertTrue(response.as(List.class).size()==10);
    }
    
    @Test
    public void simpleQueryByYear_thenOK() {
        final Response response = RestAssured.get(API_ROOT + "/year?query=" + YEARQUERY );
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        System.out.println(response.getStatusCode());
        assertTrue(response.as(List.class).size()==10);
    }
    
    @Test
    public void missingQueryByYear_thenError() {
        final Response response = RestAssured.get(API_ROOT + "/year");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }
    
    @Test
    public void missingQueryByMake_thenError() {
        final Response response = RestAssured.get(API_ROOT + "/make");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

}
