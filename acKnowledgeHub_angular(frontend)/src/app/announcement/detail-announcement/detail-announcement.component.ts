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
import { forkJoin, map, Subject, Subscription, switchMap, takeUntil, tap } from 'rxjs';
import { Category } from '../../models/category';
import { WebSocketService } from '../../services/web-socket.service';
import { GroupService } from '../../services/group.service';
import { Group } from '../../models/Group';
import { staffList } from '../../models/staff';

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
  isHumanResourceMain : boolean = false;
  createdStaff : number = 1;
  accessStaffs : number[] = [];
  noted : boolean = false;
  notNoted : boolean = false;
  notedCount : number = 0;
  unNotedCount : number = 0;
  errorMessage: string | null = null; 
  submittedReply = false; 
  private destroy$ = new Subject<void>();
  isTyping: boolean = false;
  typingSubscription!: Subscription;
  typingUserId : number = 0;
  typingAnnouncementId : number = 0;
  publishedOrNot : boolean = false;
  notedPermission : boolean = false;
  annnounceDate : string = "";
  announcementStatus : string = '';
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
 visibleQuestions: number = 3; 
 step: number = 10;
 showUpArrow = false;
  showScrollTopButton: boolean = false; 
  staffsByAnnouncement: staffList[] = [];
  groupsByAnnouncement: Group[] = [];
  showConfirmBox: boolean = false;
  showGroupConfirmBox: boolean = false;

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
   private elRef: ElementRef,
   private webSocketService: WebSocketService,
   private groupService :GroupService
 ) {}

 ngOnInit(): void {
  this.route.paramMap.subscribe(paramMap => {
    const id = paramMap.get('id');
    if (id) {
      const decodedId = atob(id);
      this.loadAnnouncementData(decodedId);
      this.webSocketService.getNewFeedbacks().pipe(takeUntil(this.destroy$)).subscribe({
        next: (feedback) => {
          if (feedback.announcementId === Number(decodedId)) {
            const existingFeedbackIndex = this.questionList.findIndex(q => q.id === feedback.id);
            feedback.photoPath = 'http://localhost:8080'+feedback.photoPath+'?'+Date.now()
            feedback.replyPhotoPath = 'http://localhost:8080'+feedback.replyPhotoPath+'?'+Date.now()
            if (existingFeedbackIndex !== -1) {
              this.questionList[existingFeedbackIndex] = feedback; // Replace the existing feedback
            } else {
              // Add new feedback
              this.questionList.unshift(feedback); // Add to the front of the list
            }
          }
        },
        error: (error) => {
          console.error('Error receiving feedback:', error);
        }
      });
      this.typingSubscription = this.webSocketService.getTypingStatus().subscribe(
        (status) => {
          this.typingAnnouncementId = status.announcementId; 
          this.typingUserId = status.staffId;
          this.isTyping = Boolean(status.typing);
        }
      );
      
      
      
    }
  });
}


private loadAnnouncementData(decodedId: string): void {
  this.authService.getUserInfo().pipe(
    switchMap((data: { isLoggedIn: boolean; company: string; position: string; user: { id: number; role: string; position: string; }; }) => {
      // Access the 'user' and 'position' data from the response
      this.currentUserId = data.user.id;
      this.isAdmin = data.user.role === 'ADMIN';
      this.isHumanResourceMain = data.position === 'Human Resource(Main)'; // Position from the root level
    
  
      return this.announcementService.getAnnouncementById(Number(decodedId)).pipe(
        tap(announcement => {
          this.announcement = announcement;
          this.publishedOrNot = announcement.published;
          if(!this.publishedOrNot){
            this.annnounceDate = this.announcement.announcedAt.toString();
            this.announcementStatus = "(Not announced yet)";
          }else if (new Date(this.announcement.announcedAt).getTime() > Date.now()) {
            this.annnounceDate = this.announcement.announcedAt.toString();
            this.announcementStatus = "(Not announced yet)";
          }else{
            this.annnounceDate = this.announcement.announcedAt.toString();
            this.announcementStatus = "";
          }
          this.createdStaff = announcement.createdStaffId;
          this.accessStaffs = announcement.groupStatus === 1 ? announcement.staffInGroups : announcement.staff;
          console.log(`Current user id ${this.currentUserId}`)
          console.log(`Announcement create user id ${this.currentUserId}`);
          this.replyPermission = this.currentUserId === announcement.createdStaffId;
          this.notedPermission = this.accessStaffs.includes(this.currentUserId);
          console.log(this.notedPermission);
        }),
        switchMap((announcement: any) => {
          if (!this.isAdmin && !this.isHumanResourceMain &&  this.currentUserId !== this.createdStaff && !this.accessStaffs.includes(this.currentUserId)) {
            this.router.navigate(['/acknowledgeHub/404']);
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
          if(String(noted) === "true"){ 
            this.noted = true;
            console.log(`Noted is ${this.noted}`);
          }else if(String(noted) === "false"){
            this.notNoted = true;
            console.log(`unNoted is ${this.notNoted}`);
          }
          console.log(feedbackData)
          this.notedCount = notedStaff.length;
          this.unNotedCount = unNotedStaff.length;
          this.questionList = feedbackData.map((feedback: any) => ({
            ...feedback,
            showInput: false,
            replyText: '',
            photoPath: 'http://localhost:8080'+feedback.photoPath+'?'+Date.now(),
            replyPhotoPath: 'http://localhost:8080'+feedback.replyPhotoPath+'?'+Date.now(),
          }));
          console.log(this.questionList)
          setTimeout(() => {
            this.isloading = false;
            this.isQuestionLoading = false;
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



 toggleReplyForm(id: number) {
   this.replyFormsVisible[id] = !this.replyFormsVisible[id];
 }

 

sendReply(question: any, feedback: feedbackResponse, index: number) {
  // Mark the reply as submitted to trigger validation messages
  this.submittedReply = true;
  var feedbackId : number = 0;
  // Validate the reply input
  if (!question.replyText || question.replyText.trim().length < 5) {
    this.errorMessage = 'Reply is required and must be at least 5 characters long.';
    return;
  }
  if(question.feedbackId === undefined){
    feedbackId = feedback.id;
  }else{
    feedback =  question.feedbackId;
  }



  // Clear any previous error message
  this.errorMessage = null;

  // Create the feedback reply object
  const feedbackReply: FeedbackReply = {
    replyText: question.replyText!,
    replyBy: this.currentUserId,
    feedbackId: feedbackId
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
  this.webSocketService.sendTypingStatus(false,this.announcement.id); 
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

goEditPage(announcementId : number){
  this.router.navigate(['/acknowledgeHub/announcement/update/'+btoa(announcementId.toString())])
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
            replyText: '',
            photoPath: 'http://localhost:8080'+feedback.photoPath+'?'+Date.now(),
            replyPhotoPath: 'http://localhost:8080'+feedback.replyPhotoPath+'?'+Date.now(),
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
  onTyping(event: Event): void {
    const inputValue = (event.target as HTMLInputElement).value;
    // Check if the user is typing (you can add more logic here, like if inputValue is not empty)
    if (inputValue.length > 0) {
      this.webSocketService.sendTypingStatus(true,this.announcement.id); // Send typing status to server
    } else {
      this.webSocketService.sendTypingStatus(false,this.announcement.id); // Stop typing if input is empty
    }
  }

  // Method to be called when the user stops typing (on blur)
  onStopTyping(): void {
    this.webSocketService.sendTypingStatus(false,this.announcement.id); // Send typing stopped status
  }

  ngOnDestroy(): void {
    // Clean up the subscription when the component is destroyed
    if (this.typingSubscription) {
      this.typingSubscription.unsubscribe();
    }
  }
  showStaffs(id: number): void {
    this.staffService.getStaffsByAnnouncementId(id).subscribe({
      next: (data) => {
        this.staffsByAnnouncement = data;
      },
      error: (e) => console.log(e)
    })
    this.showConfirmBox = !this.showConfirmBox;
  }

  showGroups(id: number) {
    this.groupService.getGroupsByAnnouncementId(id).subscribe({
      next: (data) => {
        this.groupsByAnnouncement = data;
      },
      error: (e) => console.log(e)
    })
    this.showGroupConfirmBox = !this.showGroupConfirmBox;
  }

  showSelectedStaff(): void {
    this.showConfirmBox = !this.showConfirmBox;
  }

  showSelectedGroup(): void {
    this.showGroupConfirmBox = !this.showGroupConfirmBox;
  }
}

