package com.example.safe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.safe.dao.entity.Car;

/**
 * @author cblecken
 *
 * Data interface layer - will only use findAll for data extraction
 */
public interface CarDao extends JpaRepository<Car, Long> {
}
