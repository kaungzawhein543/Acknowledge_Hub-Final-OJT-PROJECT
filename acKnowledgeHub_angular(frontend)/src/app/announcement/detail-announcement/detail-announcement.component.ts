import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AnnouncementService } from '../../services/announcement.service';

@Component({
  selector: 'app-detail-announcement',
  templateUrl: './detail-announcement.component.html',
  styleUrls: ['./detail-announcement.component.css']
})
export class DetailAnnouncementComponent implements OnInit {
  announcement: any = {
    id: 1,
    title: 'Sample Announcement',
    description: 'This is a detailed description of the announcement.',
    date: new Date()
  };

  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.getAnnouncementDetails(+id);
    }
  }

  getAnnouncementDetails(id: number): void {
    // Call service to get the announcement details
    this.announcementService.getAnnouncementById(id).subscribe(data => {
      this.announcement = data;
    });
  }

  editAnnouncement(): void {
    // Logic to edit announcement
    console.log('Edit clicked for announcement:', this.announcement.id);
  }

  deleteAnnouncement(): void {
    // Logic to delete announcement
    console.log('Delete clicked for announcement:', this.announcement.id);
  }
}
