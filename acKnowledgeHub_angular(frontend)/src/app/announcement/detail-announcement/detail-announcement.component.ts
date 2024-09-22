import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AnnouncementService } from '../../services/announcement.service';
import { feedbackResponse } from '../../models/feedResponse';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';
import { FeedbackReply } from '../../models/feedbackReply';
import { NgForm } from '@angular/forms';
import { Feedback } from '../../models/feedback';
import { StaffService } from '../../services/staff.service';
import { forkJoin, switchMap, tap } from 'rxjs';
import { Category } from '../../models/category';

@Component({
  selector: 'app-detail-announcement',
  templateUrl: './detail-announcement.component.html',
  styleUrl: './detail-announcement.component.css'
})
export class DetailAnnouncementComponent {
  isloading : boolean = true;
  isQuestionLoading : boolean = true;
  isReportDropdownOpen = false;
  isFilterDropdownOpen = false;
  isQAModalOpen = false;
  replyText: { [key: number]: string } = {};
  replyFormsVisible: { [key: number]: boolean } = {};
  questionList : feedbackResponse[] = [];
  loginId !: number;
  currentUserId !: number;
  replyPermission : boolean = false;
  isAdmin : boolean = false;
  createdStaff : number = 1;
  accessStaffs : number[] = [];
  noted : boolean = false;
  notNoted : boolean = false;
  notedCount : number = 0;
  unNotedCount : number = 0;
  announcement: any = {
    id: 1,
    title: '',
    description: '',
    createdAt: '',
    category : Category,
    createStaff : '',
    published : false,
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
   private router : Router,
   private staffService : StaffService
 ) {}

 ngOnInit(): void {
  this.route.paramMap.subscribe(paramMap => {
    const id = paramMap.get('id');
    if (id) {
      const decodedId = atob(id);
      this.loadAnnouncementData(decodedId);
    }
  });
}

private loadAnnouncementData(decodedId: string): void {
  this.authService.getUserInfo().pipe(
    switchMap((userData: { user: { id: number; role: string; }; }) => {
      this.currentUserId = userData.user.id;
      this.isAdmin = userData.user.role === 'ADMIN';

      return this.announcementService.getAnnouncementById(Number(decodedId)).pipe(
        tap(announcement => {
          this.announcement = announcement;
          console.log(this.announcement);
          this.createdStaff = announcement.createdStaffId;
          this.accessStaffs = announcement.groupStatus === 1 ? announcement.staffInGroups : announcement.staff;
          this.replyPermission = this.currentUserId === announcement.createdStaffId;
        }),
        switchMap((announcement: any) => {
          if (!this.isAdmin && this.currentUserId !== this.createdStaff && !this.accessStaffs.includes(this.currentUserId)) {
            this.router.navigate(['/404']);
            return [];
          }
          return forkJoin([
            this.staffService.checkNotedAnnouncement(this.currentUserId, this.announcement.id),
            this.staffService.getNotedUserByAnnouncementList(this.announcement.id),
            this.staffService.getUnNotedStaffByAnnouncementList(this.announcement.id, this.announcement.groupStatus),
            this.feedbackService.getFeedbackAndReplyByAnnouncement(this.announcement.id)
          ]);
        }),
        tap(([noted, notedStaff, unNotedStaff, feedbackData]) => {
          if(noted){ 
            this.noted = true
          }else{
            this.notNoted = true;
          }
          this.notedCount = notedStaff.length;
          this.unNotedCount = unNotedStaff.length;
          this.questionList = feedbackData.map((feedback: any) => ({
            ...feedback,
            showInput: false,
            replyText: ''
          }));
          setTimeout(() => {
            this.isloading = false;
          }, 300);
        })
      );
    })
  ).subscribe({
    error: (e) => console.log(e)
  });
}

 openQAModal() {
   this.isQAModalOpen = true;
   setTimeout(() => {
    this.isQuestionLoading = false;
  }, 1500);
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
  let id = this.route.snapshot.paramMap.get('id');
  if(id){
    id = atob(id);  
  }
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
notedAnnouncement(userId: number,announcementId : number){
  this.staffService.makeNotedAnnouncement(userId,announcementId).subscribe(
    (data) =>{
      this.noted = true;
      this.notNoted = false;
      this.fetchAnnouncementData();
    }
  )
}
  downloadFile(version: string): void {
    this.announcementService.downloadFile(version);
  }

}
