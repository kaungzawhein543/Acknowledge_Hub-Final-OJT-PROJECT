import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AnnouncementService } from '../../services/announcement.service';

@Component({
  selector: 'app-detail-announcement',
  templateUrl: './detail-announcement.component.html',
  styleUrl: './detail-announcement.component.css'
})
export class DetailAnnouncementComponent {
   feedback: string = '';
   isReportDropdownOpen = false;
   isFilterDropdownOpen = false;
  replyText: { [key: number]: string } = {};
  replyFormsVisible: { [key: number]: boolean } = {};
  feedbacks: Array<{ id: number, author: string, text: string, timestamp: Date, replies: Array<{ author: string, text: string, timestamp: Date }> }> = [
    // Sample feedback data
    { id: 1, author: 'Jane Smith', text: 'This announcement was very helpful. Looking forward to the orientation!', timestamp: new Date(), replies: [{ author: 'Admin', text: 'Thank you, Jane! We\'re glad you found it helpful.', timestamp: new Date() }] },
    { id: 2, author: 'Mark Johnson', text: 'Great information! The schedule fits perfectly with our team\'s planning.', timestamp: new Date(), replies: [] }
  ];
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
  toggleReportDropdown() {
    this.isReportDropdownOpen = !this.isReportDropdownOpen;
    if (this.isFilterDropdownOpen) this.isFilterDropdownOpen = false; // Close filter dropdown if open
  }

  editAnnouncement(): void {
    // Logic to edit announcement
    console.log('Edit clicked for announcement:', this.announcement.id);
  }

  deleteAnnouncement(): void {
    // Logic to delete announcement
    console.log('Delete clicked for announcement:', this.announcement.id);
  }

  submitFeedback() {
    // Handle feedback submission logic here
    console.log('Feedback submitted:', this.feedback);
    this.feedback = '';
  }

  toggleReplyForm(id: number) {
    this.replyFormsVisible[id] = !this.replyFormsVisible[id];
  }

  submitReply(feedbackId: number) {
    // Handle reply submission logic here
    console.log('Reply submitted for feedback id:', feedbackId, 'Reply text:', this.replyText[feedbackId]);
    this.replyText[feedbackId] = '';
    this.replyFormsVisible[feedbackId] = false;
  }
}
