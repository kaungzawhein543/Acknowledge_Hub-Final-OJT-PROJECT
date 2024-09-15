import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { feedbackResponse } from '../../models/feedResponse';
import { FeedbackReply } from '../../models/feedbackReply';
import { NgForm } from '@angular/forms';
import { Feedback } from '../../models/feedback';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {
  feedbackList?: feedbackResponse[];
  loginId !: number;
  role: string = 'admin';
  feedback: Feedback = {
    staffId: 0,
    announcementId: 0,
    content: ''
  };
  constructor(
    private feedbackService: FeedbackService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        this.loginId = data.user.id;
      },
      error: (e) => console.log(e)
    });
    this.feedbackService.getFeedbackAndReplyByAnnouncement(1).subscribe({
      next: (data) => {
        this.feedbackList = data.map(feedback => ({
          ...feedback,
          showInput: false,
          replyText: ''
        }));
      }
    });
  }
  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.feedback.staffId = this.loginId;
      this.feedback.announcementId = 1;
      this.feedbackService.sendFeedback(this.feedback).subscribe({
        next: (data) => {
          form.reset();
        },
        error: (e) => console.log(e)
      });
    }
  }

  sendReply(feedback: feedbackResponse, index: number) {
    const feedbackReply: FeedbackReply = {
      replyText: feedback.replyText!,
      replyBy: this.loginId,
      feedbackId: feedback.feedbackId
    };
    console.log(feedbackReply.replyText)
    this.feedbackService.sendRepliedFeedback(feedbackReply).subscribe({
      next: (data) => {
        this.feedbackList![index].reply = feedbackReply.replyText;
        this.feedbackList![index].replyBy = feedbackReply.replyBy;
        this.feedbackList![index].showInput = false;
      },
      error: (e) => console.log(e)
    });
  }

  showInput(index: number) {
    this.feedbackList![index].showInput = true;
  }

  deleteFeedback(id: number) {
    console.log("delete id " + id)
    this.feedbackService.deleteFeedback(id).subscribe({
      next: (data) => {
        console.log("delete feedback is successful");
      },
      error: (e) => console.log(e)
    });
  }

  updateFeedback(feedback: Feedback) {
    this.feedbackService.updateFeedback(feedback).subscribe({
      next: (data) => {
        console.log("update feedback is successful ");
      },
      error: (e) => console.log(e)
    });
  }

  deleteFeedbackReply(id: number) {
    this.feedbackService.deleteRepliedFeedback(id).subscribe({
      next: (data) => {
        console.log('delete feedback reply is successful');
      },
      error: (e) => console.log(e)
    })
  }

  updateFeedbackReply(feedbackReply: FeedbackReply) {
    this.feedbackService.updateRepliedFeedback(feedbackReply).subscribe({
      next: (data) => {
        console.log('delete feedback reply is successful');
      },
      error: (e) => console.log(e)
    })
  }
}
