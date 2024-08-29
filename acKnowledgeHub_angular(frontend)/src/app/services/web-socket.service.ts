import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notification } from '../models/Notification';
import { openDB } from 'idb';


interface ChatMessage {
  sender: string;
  content: string;
  userId: string; 
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private chatClient!: Client;
  private notificationsClient!: Client;
  private messagesSubject = new Subject<ChatMessage>();
  private notificationsSubject = new Subject<Notification>();
  private dbPromise = openDB('notifications-db', 1, {
    upgrade(db) {
      if (!db.objectStoreNames.contains('notifications')) {
        db.createObjectStore('notifications', { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains('unreadNotificationCount')) {
        db.createObjectStore('unreadNotificationCount', { keyPath: 'key' }); // Store for unread count
      }
    },
  });

  constructor() {
    this.connectChat();
    this.connectNotifications();
    this.loadNotifications(); 
  }

  connectChat() {
    const socket = new SockJS('http://localhost:8080/ws/chat'); 
    this.chatClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log('Chat:', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.chatClient.onConnect = () => {
      console.log('Connected to Chat WebSocket');

      this.chatClient.subscribe('/topic/messages', (message: Message) => {
        const chatMessage: ChatMessage = JSON.parse(message.body);
        this.messagesSubject.next(chatMessage);
      });
    };

    this.chatClient.activate();
  }

  connectNotifications() {
    const socket = new SockJS('http://localhost:8080/ws/notifications'); 
    this.notificationsClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log('Notifications:', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.notificationsClient.onConnect = () => {
      console.log('Connected to Notifications WebSocket');

      this.notificationsClient.subscribe('/topic/notifications', (message: Message) => {
        const notification: Notification = JSON.parse(message.body);
        this.addNotification(notification);
      });
    };

    this.notificationsClient.activate();
  }

  sendMessage(message: { sender: string; content: string }) {
    if (this.chatClient && this.chatClient.connected) {
      this.chatClient.publish({
        destination: '/app/sendMessage',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message. STOMP client is not connected.');
    }
  }

  getMessages() {
    return this.messagesSubject.asObservable();
  }
  

  async handleNewNotification(notification: Notification) {
    await this.addNotification(notification);
    const unreadCount = await this.loadUnreadNotificationCount();
    await this.saveUnreadNotificationCount(unreadCount + 1);
  }

  getNotifications(): Observable<Notification> {
    this.loadNotifications();
    return this.notificationsSubject.asObservable();
  }

  private async addNotification(notification: Notification) {
    const db = await this.dbPromise;
    const tx = db.transaction('notifications', 'readwrite');
    const store = tx.objectStore('notifications');

    try {
      const existingNotification = await store.get(notification.id);
      
      if (!existingNotification) {
        await store.add(notification);
        this.notificationsSubject.next(notification);
      }
    } catch (error: unknown) {
      if (error instanceof DOMException && error.name === 'ConstraintError') {
        console.error(`Key already exists: ${notification.id}`);
      } else {
        console.error('Error adding notification to IndexedDB:', error);
      }
    }

    await tx.done;
  }

  async saveUnreadNotificationCount(count: number) {
    const db = await this.dbPromise;
    const tx = db.transaction('unreadNotificationCount', 'readwrite');
    const store = tx.objectStore('unreadNotificationCount');
    await store.put({ key: 'count', value: count });
    await tx.done;
  }

  async loadUnreadNotificationCount(): Promise<number> {
    const db = await this.dbPromise;
    const tx = db.transaction('unreadNotificationCount', 'readonly');
    const store = tx.objectStore('unreadNotificationCount');
    const record = await store.get('count');
    return record ? record.value : 0;
  }

  private async loadNotificationsFromIndexedDB() {
    const db = await this.dbPromise;
    const tx = db.transaction('notifications', 'readonly');
    const notifications = await tx.objectStore('notifications').getAll();
    notifications.forEach(notification => this.notificationsSubject.next(notification));
  }

  private async loadNotifications() {
    await this.loadNotificationsFromIndexedDB();
  }

  async markAllAsRead() {
    await this.saveUnreadNotificationCount(0);
  }
}