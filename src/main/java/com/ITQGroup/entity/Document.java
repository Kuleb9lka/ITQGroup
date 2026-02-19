package com.ITQGroup.entity;

import com.ITQGroup.enums.DocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "unique_number", nullable = false, unique = true, updatable = false)
    @UuidGenerator
    @NotNull(message = "Unique number can't be null")
    private UUID uniqueNumber;

    @Column(name = "author_id")
    @NotNull(message = "Author ID can't be null")
    @Positive(message = "Author ID can't be negative")
    private Long authorId;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name can't be null or blank")
    @Size(min = 5, message = "Minimal document name length 5 symbols")
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull(message = "Status can't be null")
    private DocumentStatus status;

    @Column(name = "create_date", nullable = false)
    @NotNull(message = "Create date can't be null")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<History> historyList;
}
