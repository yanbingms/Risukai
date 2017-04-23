package com.demo.api.repository;

import com.demo.api.entity.City;
import com.demo.api.entity.EAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * Created by mhh on 2017/3/25.
 */
public interface EAccountRepository extends JpaRepository<EAccount, Long> {


    @Query(value = "SELECT * FROM e_account order by id desc limit 1", nativeQuery = true)
    EAccount findOne();
}
