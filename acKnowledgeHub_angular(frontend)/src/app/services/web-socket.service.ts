import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notification } from '../models/Notification';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client!: Client;
  private notificationsSubject = new Subject<Notification[]>();
  private statusUpdateSubject = new Subject<number[]>();
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
    const id = localStorage.getItem('id');
    return id ? parseInt(id, 10) : null;
  }

  private initializeWebSocketConnection(): void {
    const id = this.getIdFromLocalStorage();
    if (id === null) {
      console.log('Staff ID not found, retrying...');
      setTimeout(() => this.initializeWebSocketConnection(), 1000); // Retry after 1 second
      return;
    }

    this.connectNotifications(id).then(() => {
      console.log('WebSocket connected successfully.');
    }).catch((error) => {
      console.error('WebSocket connection failed, retrying...', error);
      // Retry after a delay if the connection fails
      setTimeout(() => this.initializeWebSocketConnection(), 1000);
    });
  }
 
  private disconnectWebSocket(): void {
    if (this.client && this.client.active) {
      this.client.deactivate(); // Properly close the WebSocket connection
      console.log('WebSocket disconnected.');
      this.isConnected = false;
    }
  }

  connectNotifications(id: number): Promise<void> {
    return new Promise((resolve, reject) => {
      const socket = new SockJS('http://localhost:8080/ws');
      
  
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
  
        // Subscribe to receive all notifications
        this.client.subscribe('/topic/notifications', (message: Message) => {
          this.handleNotificationsMessage(message);
        });
  
        // Subscribe to status updates
        this.client.subscribe('/topic/notificationStatusUpdate', (message: Message) => {
          const updatedNotificationIds: number[] = JSON.parse(message.body);
          this.statusUpdateSubject.next(updatedNotificationIds);
        });
  
        // Subscribe to individual notification updates
        this.client.subscribe('/topic/notification', (message: Message) => {
          try {
            const notifications: Notification[] = JSON.parse(message.body);
            console.log('Received new notification:', message.body);
            this.notificationsSubject.next(notifications);
          } catch (error) {
            console.error('Error parsing new notification message:', error);
          }
        });
  
        this.requestAllNotifications();
      };
  
      this.client.onStompError = (frame) => {
        console.error('WebSocket error:', frame);
      };
  
      this.client.activate(); 
  });
}
  private handleNotificationsMessage(message: Message): void {
    try {
      const notification: Notification[] = JSON.parse(message.body);
      console.log('Received notifications:', notification);
      this.notificationsSubject.next(notification);
    } catch (error) {
      console.error('Error parsing notifications message:', error);
    }
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
  getStatusUpdates(): Observable<number[]> {
    return this.statusUpdateSubject.asObservable();
  }
  
}