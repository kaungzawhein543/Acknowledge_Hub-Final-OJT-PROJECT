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
    return this.http.get<feedbackList[]>(`${this.baseURL}/HRM/list/${id}`,{ withCredentials: true});
  }

  getFeedbackAndReplyByAnnouncement(id: number): Observable<feedbackResponse[]> {
    return this.http.get<feedbackResponse[]>(`${this.baseURL}/all/all-by-announcement/${id}`,{ withCredentials: true});
  }

  sendFeedback(feedback: Feedback): Observable<any> {
    return this.http.post(`${this.baseURL}/all/sendFeedback`, feedback,{ withCredentials : true, responseType : 'text' as 'json'});
  }

  deleteFeedback(id: number): Observable<any> {
    return this.http.delete(`${this.baseURL}/${id}`,{ withCredentials: true});
  }

  updateFeedback(feedback: Feedback): Observable<any> {
    return this.http.put(`${this.baseURL}`, feedback,{ withCredentials: true});
  }

  sendRepliedFeedback(feedbackReply: FeedbackReply): Observable<any> {
    return this.http.post(`${this.baseURL2}/all/saveFeedbackReply`, feedbackReply,{withCredentials : true});
  }

  updateRepliedFeedback(feedbackReply: FeedbackReply): Observable<any> {
    return this.http.put(`${this.baseURL2}`, feedbackReply,{ withCredentials: true});
  }

  deleteRepliedFeedback(id: number): Observable<any> {
    return this.http.delete(`${this.baseURL2}/${id},`,{ withCredentials: true});
  }

}
