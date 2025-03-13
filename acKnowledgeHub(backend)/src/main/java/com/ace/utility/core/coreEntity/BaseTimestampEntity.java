package com.ace.utility.core.coreEntity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class BaseTimestampEntity extends BaseEntity{

    @CreationTimestamp
    private LocalDateTime updatedAt;

    private boolean status;
}
