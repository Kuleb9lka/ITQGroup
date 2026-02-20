package com.ITQGroup.reposiroty;

import com.ITQGroup.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @Query("select doc from Document doc left join fetch doc.historyList where doc.id = :id")
    Optional<Document> findByIdWithHistory(@Param("id") Long id);

    Page<Document> findAllByIdIn(List<Long> ids, Pageable pageable);
}
