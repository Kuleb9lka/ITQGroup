package com.ITQGroup.dto.specification;

import com.ITQGroup.dto.filter.DocumentFilterDto;
import com.ITQGroup.entity.Document;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> withFilters(DocumentFilterDto filter) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            if (filter.getStatus() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), filter.getStatus())
                );
            }

            if (filter.getAuthorId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("authorId"), filter.getAuthorId())
                );
            }

            if (filter.getCreateDateFrom() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createDate"),
                                filter.getCreateDateFrom()
                        )
                );
            }

            if (filter.getCreateDateTo() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createDate"),
                                filter.getCreateDateTo()
                        )
                );
            }

            if (filter.getUpdateDateFrom() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("updateDate"),
                                filter.getUpdateDateFrom()
                        )
                );
            }

            if (filter.getUpdateDateTo() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("updateDate"),
                                filter.getUpdateDateTo()
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
