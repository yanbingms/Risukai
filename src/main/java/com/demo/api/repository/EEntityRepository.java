package com.demo.api.repository;

import com.demo.api.entity.EEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * Created by mhh on 2017/3/25.
 */
@Repository
public interface EEntityRepository extends JpaRepository<EEntity, String> {

    @Query(value = "from EEntity where renum > 0 and renum <= :renum and status != 'success'")
    List<EEntity> findErrors(@Param("renum") int renum);
}
