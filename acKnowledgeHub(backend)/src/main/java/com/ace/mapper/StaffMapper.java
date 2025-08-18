package com.ace.mapper;

import com.ace.dto.ProfileDTO;
import com.ace.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    StaffMapper INSTANCE = Mappers.getMapper(StaffMapper.class);

    @Mapping(source = "position.name", target = "position")
    @Mapping(source = "department.name", target = "department")
    @Mapping(source = "company.name", target = "company")
    @Mapping(target = "monthlyCount", ignore = true) // set manually later
    ProfileDTO toProfileDTO(Staff staff);
}
