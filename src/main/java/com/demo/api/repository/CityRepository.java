package com.demo.api.repository;

import com.demo.api.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * Created by mhh on 2017/3/26.
 */
public interface CityRepository extends JpaRepository<City, Long> {

    @Query(value = "SELECT * FROM city WHERE level = ?1 and name like concat(?2,'%')", nativeQuery = true)
    List<City> findByName(int level, String name);
}
