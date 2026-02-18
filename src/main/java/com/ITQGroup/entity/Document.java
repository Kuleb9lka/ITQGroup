package com.ITQGroup.entity;

import com.ITQGroup.annotation.ValidDateRange;
import com.ITQGroup.enums.DocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(startField = "createDate", endField = "updateDate")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "unique_number", nullable = false, unique = true, updatable = false)
    @UuidGenerator
    @NotNull(message = "Unique number can't be null")
    private UUID uniqueNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name can't be null or blank")
    @Size(min = 5, message = "Minimal document name length 5 symbols")
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull(message = "Status can't be null")
    private DocumentStatus documentStatus;

    @Column(name = "create_date", nullable = false)
    @NotNull(message = "Create date can't be null")
    @PastOrPresent(message = "Create date can't be in future")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    @PastOrPresent(message = "Update date can't be in future")
    private LocalDateTime updateDate;
}
