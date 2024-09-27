import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { BehaviorSubject } from 'rxjs';
import { Client, Message , Stomp } from '@stomp/stompjs';
import { Notification } from '../models/Notification';
import { AuthService } from './auth.service';
import SockJS from 'sockjs-client';
import { Feedback } from '../models/feedback';
import { FeedbackReply } from '../models/feedbackReply';
import { feedbackResponse } from '../models/feedResponse';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client!: Client;
  private socket: any;
  private stompClient: any;
  private statusUpdateSubject = new Subject<number[]>();
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  private notificationSubject = new Subject<Notification>();
  private feedbacksSubject = new BehaviorSubject<feedbackResponse[]>([]);
  private feedbackSubject = new Subject<feedbackResponse>();
  private isConnected = false;
  private typingSubject = new Subject<{staffId: number,typing: boolean ,announcementId : number }>();

  constructor(private authService: AuthService) {
    this.initializeWebSocketConnection();

    this.authService.onLogin().subscribe(() => {
      this.initializeWebSocketConnection();
    });

    this.authService.onLogout().subscribe(() => {
      this.disconnectWebSocket();
    });
  }

  private getIdFromLocalStorage(): number | null {
    if (typeof window !== 'undefined') { // Check if window is defined
      const id = localStorage.getItem('id');
      return id ? parseInt(id, 10) : null;
    }
    return null;
  }

  private initializeWebSocketConnection(): void {
    const id = this.getIdFromLocalStorage();
    if (id === null) {
        setTimeout(() => this.initializeWebSocketConnection(), 1000); 
        return;
    }
    this.disconnectWebSocket();
    this.notificationsSubject.next([]);
    console.log('Initializing WebSocket connection...');
    this.connectNotifications(id).then(() => {
        console.log('WebSocket connected successfully.');
        this.requestAllNotifications();
    }).catch((error) => {
        console.error('WebSocket connection failed, retrying...', error);
        setTimeout(() => this.initializeWebSocketConnection(), 1000);
    });
}

  private disconnectWebSocket(): void {
    if (this.client && this.client.active) {
      this.client.deactivate();
      console.log('WebSocket disconnected.');
      this.isConnected = false;
    }
  }

  connectNotifications(id: number): Promise<void> {
    return new Promise((resolve, reject) => {
      const socket = new SockJS(`http://localhost:8080/ws`);
      this.stompClient = Stomp.over(socket);
      
  
      this.client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          'X-Staff-Id': id.toString(),
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.onConnect = (frame) => {
        console.log('Connected to WebSocket:', frame);
        this.subscribeToNotifications(id);
        this.subscribeToFeedbacks(id); 
        this.subscribeToTyping();
        resolve();
      };

  
      this.client.onStompError = (frame) => {
        console.error('WebSocket error:', frame);
        reject(frame);
    };
      this.client.activate(); 
  });
}
private handleNotificationsMessage(message: Message): void {
  try {
    const data = JSON.parse(message.body);
    if (Array.isArray(data)) {
      const notifications: Notification[] = data;
      this.notificationsSubject.next(notifications);
    }
  } catch (error) {
    console.error('Error parsing notifications message:', error);
  }
}

//Subscribe Notificaitons
private subscribeToNotifications(staffId: number): void {
  this.client.subscribe(`/topic/notifications/${staffId}`, (message: Message) => {
    this.handleNotificationsMessage(message);
  });

  this.client.subscribe(`/topic/notification/${staffId}`, (message: Message) => {
    const notification = JSON.parse(message.body) as Notification;
    console.log('Received new notification:', notification);
    this.notificationSubject.next(notification);
  });
}

private subscribeToFeedbacks(staffId: number): void {
  this.client.subscribe(`/topic/feedback/`, (message: Message) => {
    console.log('Received feedback message:', message.body);
    try {
      const feedback: feedbackResponse = JSON.parse(message.body);
      this.feedbackSubject.next(feedback)
    } catch (error) {
      console.error('Error parsing feedback message:', error);
    }
  });
}
private subscribeToTyping(): void {
  this.client.subscribe('/topic/typing', (message: Message) => {
    const typingStatus = JSON.parse(message.body);
    this.typingSubject.next(typingStatus);
  });
}
  
  private requestAllNotifications(): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/getNotifications',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: staffId.toString() 
      });
    } else {
      console.error('No staffId found for requesting notifications.');
    }
  }

  private requestAllFeedbacks(): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/getFeedbacks',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: staffId.toString() 
      });
    } else {
      console.error('No staffId found for requesting feedbacks.');
    }
  }

  public sendNotification(notification: Notification): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/sendNotification',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: JSON.stringify(notification)
      });
    } else {
      console.error('No staffId found for sending notification.');
    }
  }

  public sendFeedback(feedback: Feedback): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/sendFeedback',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: JSON.stringify(feedback)
      });
    } else {
      console.error('No staffId found for sending feedback.');
    }
  }

  public sendTypingStatus(typing: boolean,announcementId:number): void {
    this.authService.getUserInfo().subscribe(
      data => {
        const staffId = data.user.id; // Access the staffId from the response
        
        if (staffId) {
          this.client.publish({
            destination: '/app/typing',
            headers: { 'X-Staff-Id': staffId.toString() },
            body: JSON.stringify({ staffId, typing ,announcementId})
          });
        } else {
          console.error('Staff ID not found. Cannot send typing status.');
        }
      },
      error => {
        console.error('Error fetching user info:', error);
      }
    );
  }
  
  public sendFeedbackReply(feedbackReply: FeedbackReply): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/sendFeedbackReply',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: JSON.stringify(feedbackReply)
      });
    } else {
      console.error('No staffId found for sending feedback reply.');
    }
  }

  public getNotifications(): Observable<Notification[]> {
    return this.notificationsSubject.asObservable();
  }

  public getNewNotifications(): Observable<Notification> {
    return this.notificationSubject.asObservable();
  }

  public getFeedbacks(): Observable<feedbackResponse[]>{
    return this.feedbacksSubject.asObservable();
  }
  
  
  public getNewFeedbacks(): Observable<feedbackResponse> {
    return this.feedbackSubject.asObservable();
  }

  public getTypingStatus(): Observable<{ staffId: number,  typing: boolean, announcementId : number }> {
    return this.typingSubject.asObservable();
  }

  public getStatusUpdates(): Observable<number[]> {
    return this.statusUpdateSubject.asObservable();
  }
  
}
