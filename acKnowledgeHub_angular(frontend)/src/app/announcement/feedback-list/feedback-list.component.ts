import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { data } from 'jquery';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-feedback-list',
  templateUrl: './feedback-list.component.html',
  styleUrl: './feedback-list.component.css'
})
export class FeedbackListComponent implements OnInit {
  announcementId !: number;
  constructor(private feedbackService: FeedbackService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.announcementId = +this.route.snapshot.params['id'];
    this.feedbackService.getFeedbackList(this.announcementId).subscribe({
      next: (data) => {
        console.log(data)
      },
      error: (e) => console.log(e)
    })
  }

}
