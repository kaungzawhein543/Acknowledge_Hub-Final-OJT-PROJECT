import { Component } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { AnnouncementService } from '../../services/announcement.service';
import { feedbackResponse } from '../../models/feedResponse';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';
import { FeedbackReply } from '../../models/feedbackReply';
import { NgForm } from '@angular/forms';
import { Feedback } from '../../models/feedback';
import { StaffService } from '../../services/staff.service';

@Component({
  selector: 'app-detail-announcement',
  templateUrl: './detail-announcement.component.html',
  styleUrl: './detail-announcement.component.css'
})
export class DetailAnnouncementComponent {
  isReportDropdownOpen = false;
  isFilterDropdownOpen = false;
  isQAModalOpen = false;
  replyText: { [key: number]: string } = {};
  replyFormsVisible: { [key: number]: boolean } = {};
  questionList: feedbackResponse[] = [];
  loginId !: number;
  currentUserId !: number;
  replyPermission: boolean = false;
  isAdmin: boolean = false;
  createdStaff: number = 1;
  accessStaffs: number[] = [];
  noted: boolean = false;
  announcementId !: number;
  notedCount: number = 0;
  unNotedCount: number = 0;
  announcement: any = {
    id: 1,
    title: 'Sample Announcement',
    description: 'This is a detailed description of the announcement.',
    createdAt: ''
  };
  feedback: Feedback = {
    staffId: 0,
    announcementId: 0,
    content: ''
  };

  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    private feedbackService: FeedbackService,
    private authService: AuthService,
    private router: Router,
    private staffService: StaffService
  ) { }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        this.currentUserId = data.user.id;
        console.log(this.currentUserId)
        if (data.user.role === 'ADMIN') {
          this.isAdmin = true;
        } else {
          this.isAdmin = false;
        }
        this.route.params.subscribe((params: Params) => {
          const decodedStringId = atob(params['id']);
          this.announcementId = parseInt(decodedStringId, 10);
        });
        if (this.announcementId) {
          this.announcementService.getAnnouncementById(this.announcementId).subscribe(detailAnnouncement => {
            this.announcement = detailAnnouncement;
            this.staffService.checkNotedAnnouncement(this.currentUserId, this.announcement.id).subscribe(
              data => {
                this.noted = data;
                this.staffService.getNotedUserByAnnouncementList(this.announcement.id).subscribe(
                  notedStaff => {
                    this.notedCount = notedStaff.length;
                  }
                )
                this.staffService.getUnNotedStaffByAnnouncementList(this.announcement.id, this.announcement.groupStatus).subscribe(
                  unNotedStaff => {
                    this.unNotedCount = unNotedStaff.length;
                  }
                )
              }
            )
            this.createdStaff = detailAnnouncement.createdStaffId;
            if (detailAnnouncement.groupStatus == 1) {
              this.accessStaffs = detailAnnouncement.staffInGroups;
            } else {
              this.accessStaffs = detailAnnouncement.staff;
            }
            if (this.currentUserId === detailAnnouncement.createdStaffId) {
              this.replyPermission = true;
            } else {
              this.replyPermission = false;
            }
            if (!this.isAdmin) {
              if (this.currentUserId === this.announcement.createdStaffId) {
                return;
              } else if (!this.accessStaffs.some(staff => staff === this.currentUserId)) {
                this.router.navigate(['/404']); // Redirect if not in access list
              }
            }

            this.feedbackService.getFeedbackAndReplyByAnnouncement(this.announcement.id).subscribe({
              next: (data) => {
                this.questionList = data.map(feedback => ({
                  ...feedback,
                  showInput: false,
                  replyText: ''
                }));
              }
            });
          });
        }

      },
      error: (e) => console.log(e)
    });


  }
  openQAModal() {
    this.isQAModalOpen = true;
  }

  closeQAModal() {
    this.isQAModalOpen = false;
  }


  editAnnouncement(): void {
    console.log('Edit clicked for announcement:', this.announcement.id);
  }


  toggleReplyForm(id: number) {
    this.replyFormsVisible[id] = !this.replyFormsVisible[id];
  }
  sendReply(feedback: feedbackResponse, index: number) {
    const feedbackReply: FeedbackReply = {
      replyText: feedback.replyText!,
      replyBy: this.currentUserId,
      feedbackId: feedback.feedbackId
    };
    this.feedbackService.sendRepliedFeedback(feedbackReply).subscribe({
      next: (data) => {
        this.questionList![index].reply = feedbackReply.replyText;
        this.questionList![index].replyBy = feedbackReply.replyBy;
        this.questionList![index].showInput = false;
        this.fetchAnnouncementData();
        this.feedbackService.getFeedbackAndReplyByAnnouncement(this.announcement.id).subscribe({
          next: (data) => {
            this.questionList = data.map(feedback => ({
              ...feedback,
              showInput: false,
              replyText: ''
            }));
          }
        });
      },
      error: (e) => console.log(e)
    });
  }
  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.feedback.staffId = this.currentUserId;
      this.feedback.announcementId = this.announcement.id;
      this.feedbackService.sendFeedback(this.feedback).subscribe({
        next: (data) => {
          this.fetchAnnouncementData();
          form.reset();
        },
        error: (e) => console.log(e)
      });
    }
  }
  private fetchAnnouncementData(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.announcementService.getAnnouncementById(Number(id)).subscribe(data => {
        this.announcement = data;
        this.replyPermission = this.currentUserId === data.createdStaffId;

        this.feedbackService.getFeedbackAndReplyByAnnouncement(this.announcement.id).subscribe({
          next: (data) => {
            this.questionList = data.map(feedback => ({
              ...feedback,
              showInput: false,
              replyText: ''
            }));
          },
          error: (e) => console.log(e)
        });
      });
    }
  }
  notedAnnouncement(userId: number, announcementId: number) {
    this.staffService.makeNotedAnnouncement(userId, announcementId).subscribe(
      (data) => {
        console.log(data);
        this.fetchAnnouncementData();
      }
    )
  }
  downloadFile(version: string): void {
    this.announcementService.downloadFile(version);
  }

}
