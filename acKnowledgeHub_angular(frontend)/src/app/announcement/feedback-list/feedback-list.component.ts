import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { data } from 'jquery';
import { ActivatedRoute, Router } from '@angular/router';
import { Feedback } from '../../models/feedback';
import { feedbackList } from '../../models/feedback-list';

@Component({
  selector: 'app-feedback-list',
  templateUrl: './feedback-list.component.html',
  styleUrl: './feedback-list.component.css'
})
export class FeedbackListComponent implements OnInit {
  announcementId !: number;
  private itemIdToDelete: number | null = null;
  feedbackList: feedbackList[] = [];
  constructor(private feedbackService: FeedbackService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.announcementId = +this.route.snapshot.params['id'];
    this.feedbackService.getFeedbackList(this.announcementId).subscribe({
      next: (data) => {
        this.feedbackList = data;
      },
      error: (e) => console.log(e)
    })
  }
}
