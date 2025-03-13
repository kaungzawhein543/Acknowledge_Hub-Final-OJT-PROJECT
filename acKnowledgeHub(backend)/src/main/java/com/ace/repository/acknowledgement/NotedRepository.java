package com.ace.repository.acknowledgement;

import com.ace.entity.Announcement.Announcement;
import com.ace.entity.organization.Staff;
import com.ace.entity.acknowledgement.StaffNotedAnnouncement;
import com.ace.utility.core.coreRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotedRepository extends BaseRepository<StaffNotedAnnouncement,Integer> {

    Optional<StaffNotedAnnouncement> findByStaffAndAnnouncement(Staff staff, Announcement announcement);

    List<StaffNotedAnnouncement> findByStaff(Staff staff);


}
