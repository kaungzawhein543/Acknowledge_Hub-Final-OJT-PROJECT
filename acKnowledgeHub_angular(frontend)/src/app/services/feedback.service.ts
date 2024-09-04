import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Feedback } from '../models/feedback';
import { feedbackResponse } from '../models/feedResponse';
import { FeedbackReply } from '../models/feedbackReply';
import { feedbackList } from '../models/feedback-list';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  private baseURL = 'http://localhost:8080/api/v1/feedback'
  private baseURL2 = 'http://localhost:8080/api/v1/feedback-reply'
  constructor(private http: HttpClient) { }

  getFeedbackList(id: number): Observable<feedbackList[]> {
    return this.http.get<feedbackList[]>(`${this.baseURL}/list/${id}`);
  }

  getFeedbackAndReplyByAnnouncement(id: number): Observable<feedbackResponse[]> {
    return this.http.get<feedbackResponse[]>(`${this.baseURL}/all-by-announcement/${id}`);
  }

  sendFeedback(feedback: Feedback): Observable<any> {
    return this.http.post(`${this.baseURL}`, feedback);
  }

  deleteFeedback(id: number): Observable<any> {
    return this.http.delete(`${this.baseURL}/${id}`);
  }

  updateFeedback(feedback: Feedback): Observable<any> {
    return this.http.put(`${this.baseURL}`, feedback);
  }

  sendRepliedFeedback(feedbackReply: FeedbackReply): Observable<any> {
    return this.http.post(`${this.baseURL2}`, feedbackReply);
  }

  updateRepliedFeedback(feedbackReply: FeedbackReply): Observable<any> {
    return this.http.put(`${this.baseURL2}`, feedbackReply);
  }

  deleteRepliedFeedback(id: number): Observable<any> {
    return this.http.delete(`${this.baseURL2}/${id}`);
  }

}
