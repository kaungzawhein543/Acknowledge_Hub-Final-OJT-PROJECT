package com.ace.utility.core.coreEntity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {

    @CreationTimestamp
    protected LocalDateTime createdAt;

    @Version
    protected int version;
}
