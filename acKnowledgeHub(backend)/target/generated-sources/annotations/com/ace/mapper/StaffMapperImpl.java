package com.ace.mapper;

import com.ace.dto.ProfileDTO;
import com.ace.entity.Company;
import com.ace.entity.Department;
import com.ace.entity.Position;
import com.ace.entity.Staff;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-14T00:15:05+0630",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Arch Linux)"
)
@Component
public class StaffMapperImpl implements StaffMapper {

    @Override
    public ProfileDTO toProfileDTO(Staff staff) {
        if ( staff == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setPosition( staffPositionName( staff ) );
        profileDTO.setDepartment( staffDepartmentName( staff ) );
        profileDTO.setCompany( staffCompanyName( staff ) );
        profileDTO.setId( staff.getId() );
        profileDTO.setName( staff.getName() );
        profileDTO.setCompanyStaffId( staff.getCompanyStaffId() );
        profileDTO.setEmail( staff.getEmail() );
        profileDTO.setPassword( staff.getPassword() );
        profileDTO.setStatus( staff.getStatus() );
        profileDTO.setRole( staff.getRole() );
        profileDTO.setPhotoPath( staff.getPhotoPath() );
        profileDTO.setCreatedAt( staff.getCreatedAt() );
        profileDTO.setChatId( staff.getChatId() );

        return profileDTO;
    }

    private String staffPositionName(Staff staff) {
        if ( staff == null ) {
            return null;
        }
        Position position = staff.getPosition();
        if ( position == null ) {
            return null;
        }
        String name = position.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String staffDepartmentName(Staff staff) {
        if ( staff == null ) {
            return null;
        }
        Department department = staff.getDepartment();
        if ( department == null ) {
            return null;
        }
        String name = department.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String staffCompanyName(Staff staff) {
        if ( staff == null ) {
            return null;
        }
        Company company = staff.getCompany();
        if ( company == null ) {
            return null;
        }
        String name = company.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
