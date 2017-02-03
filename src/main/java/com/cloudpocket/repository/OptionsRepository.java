package com.cloudpocket.repository;

import com.cloudpocket.model.entity.CloudPocketOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Cloud Pocket system options.
 */
@Repository
public interface OptionsRepository extends JpaRepository<CloudPocketOption ,String> {

    String findValueByKey(String key);

    List<CloudPocketOption> findAll();

    List<String> getKeys();

}
