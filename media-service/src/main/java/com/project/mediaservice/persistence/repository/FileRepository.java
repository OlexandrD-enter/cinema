package com.project.mediaservice.persistence.repository;

import com.project.mediaservice.persistence.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing File entities in the database.
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
  @Query("select f.name from FileEntity f where f.id =:id")
  String getFileNameById(@Param("id") Long id);
}
