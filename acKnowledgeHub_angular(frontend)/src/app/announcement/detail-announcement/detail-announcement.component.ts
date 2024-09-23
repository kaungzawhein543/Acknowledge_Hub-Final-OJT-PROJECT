import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
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
  @ViewChild('askQuestionSection', { static: false }) askQuestionSection!: ElementRef;
  @ViewChild('topContainer') topContainer!: ElementRef;

  
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
  notedCount : number = 0;
  unNotedCount : number = 0;
  errorMessage: string | null = null; 
  submittedReply = false; 
  announcement: any = {
    id: 1,
    title: '',
    description: '',
    createdAt: '',
    category : Category,
    createStaff : ''
  };
 feedback: Feedback = {
   staffId: 0,
   announcementId: 0,
   content: ''
 };
 visibleQuestions: number = 3; 
 step: number = 10;
 showUpArrow = false;
  showScrollTopButton: boolean = false; 

  @HostListener('window:scroll', [])
  onWindowScroll() {
    // Show button if scrolled down 200px or more
    this.showScrollTopButton = window.scrollY > 200;
  }
  
 constructor(
   private route: ActivatedRoute,
   private announcementService: AnnouncementService,
   private feedbackService: FeedbackService,
   private authService: AuthService,
   private router : Router,
   private staffService : StaffService,
   private elRef: ElementRef
 ) {}

 ngOnInit(): void {
  const id = this.route.snapshot.paramMap.get('id');
  if (id) {
    const decodedId = atob(id);

    this.authService.getUserInfo().pipe(
      switchMap((userData: { user: { id: number; role: string; }; }) => {
        this.currentUserId = userData.user.id;
        this.isAdmin = userData.user.role === 'ADMIN';

        return this.announcementService.getAnnouncementById(Number(decodedId)).pipe(
          tap(announcement => {
            console.log(announcement)
            this.announcement = announcement;
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
            this.noted = noted;
            this.notedCount = notedStaff.length;
            this.unNotedCount = unNotedStaff.length;
            this.questionList = feedbackData.map((feedback: any) => ({
              ...feedback,
              showInput: false,
              replyText: ''
            }));
            setTimeout(() => {
              this.isloading = false;
            }, 1000);
          })
        );
      })
    ).subscribe({
      error: (e) => console.log(e)
    });
  }
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

 

sendReply(question: any, feedback: feedbackResponse, index: number) {
  // Mark the reply as submitted to trigger validation messages
  this.submittedReply = true;

  // Validate the reply input
  if (!question.replyText || question.replyText.trim().length < 5) {
    this.errorMessage = 'Reply is required and must be at least 5 characters long.';
    return;
  }

  // Clear any previous error message
  this.errorMessage = null;

  // Create the feedback reply object
  const feedbackReply: FeedbackReply = {
    replyText: question.replyText!,
    replyBy: this.currentUserId,
    feedbackId: feedback.feedbackId
  };

  // Send the reply via the service
  this.feedbackService.sendRepliedFeedback(feedbackReply).subscribe({
    next: (data) => {
      // Update the local question list with the reply
      this.questionList![index].reply = feedbackReply.replyText;
      this.questionList![index].replyBy = feedbackReply.replyBy;
      this.questionList![index].showInput = false;

      // Fetch updated announcement data
      this.fetchAnnouncementData();

      // Optionally refresh feedback and reply list
      this.feedbackService.getFeedbackAndReplyByAnnouncement(this.announcement.id).subscribe({
        next: (data) => {
          this.questionList = data.map(feedback => ({
            ...feedback,
            showInput: false,
            replyText: ''
          }));
        },
        error: (err) => console.error('Error fetching feedback:', err)
      });
    },
    error: (e) => {
      console.log('Error sending feedback reply:', e);
      this.errorMessage = 'Failed to send your reply. Please try again.';
    }
  });
}



 onSubmit(form: NgForm): void {
   if (form.invalid) {
    form.controls['question'].markAsTouched();

    return; 
  }

  // Proceed with form submission if valid
  this.feedback.staffId = this.currentUserId;
  this.feedback.announcementId = this.announcement.id;

  this.feedbackService.sendFeedback(this.feedback).subscribe({
    next: (data) => {
      this.fetchAnnouncementData();  // Refresh data after successful submission
      form.resetForm();  // Reset the form and clear the error messages
    },
    error: (e) => console.log(e)
  });
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
      console.log(data);
      this.fetchAnnouncementData();
    }
  )
}
  downloadFile(version: string): void {
    this.announcementService.downloadFile(version);
  }

  scrollToAskQuestion() {
    this.askQuestionSection.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  showMoreQuestions() {
    this.visibleQuestions += 5; // Show more questions
    this.showScrollTopButton = true; // Show Scroll to Top button on the first click
  }

  scrollToTop(): void {
    this.topContainer.nativeElement.scrollIntoView({ behavior: 'smooth' }); // Scroll to the top container
  }

}

