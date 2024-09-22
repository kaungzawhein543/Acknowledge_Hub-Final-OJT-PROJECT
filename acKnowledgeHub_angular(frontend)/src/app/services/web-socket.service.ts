import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { BehaviorSubject } from 'rxjs';
import { Client, Message , Stomp } from '@stomp/stompjs';
import { Notification } from '../models/Notification';
import { AuthService } from './auth.service';
import SockJS from 'sockjs-client';

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
  private isConnected = false;
  

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
        debug: (str) => console.log(`Notifications debug: ${str}`),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.onConnect = (frame) => {
        console.log('Connected to WebSocket:', frame);
        this.subscribeToNotifications(id);
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
  private requestAllNotifications(): void {
    const staffId = this.getIdFromLocalStorage();
    if (staffId) {
      this.client.publish({
        destination: '/app/getNotifications',
        headers: { 'X-Staff-Id': staffId.toString() }, 
        body: staffId.toString() 
      });
      console.log('Requested all notifications for staff ID:', staffId);
    } else {
      console.error('No staffId found for requesting notifications.');
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

  public getNotifications(): Observable<Notification[]> {
    return this.notificationsSubject.asObservable();
  }

  public getNewNotifications(): Observable<Notification> {
    return this.notificationSubject.asObservable();
  }

  public getStatusUpdates(): Observable<number[]> {
    return this.statusUpdateSubject.asObservable();
  }
  
}