import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-countdown',
  templateUrl: './countdown.component.html',
  styleUrl: './countdown.component.css'
})
export class CountdownComponent implements OnInit, OnDestroy{
  @Input() targetDate: string | null = null; // Input for the target date and time
  timeLeft: string | null = null;
  private countdownSubscription: Subscription = new Subscription();

  ngOnInit() {
    console.log('Target Date Received:', this.targetDate); // Debugging line to ensure targetDate is received
    if (this.targetDate) {
      this.startCountdown();
    }
  }

  ngOnDestroy() {
    this.countdownSubscription.unsubscribe(); // Clean up the subscription
  }

  private startCountdown() {
    this.countdownSubscription = interval(1000).subscribe(() => {
      this.updateCountdown();
    });
  }

  private updateCountdown() {
    if (!this.targetDate) return; // Exit if no target date
    const targetDate = new Date(this.targetDate); // Convert to Date object
    const now = new Date();
    const remainingTime = targetDate.getTime() - now.getTime();

    if (remainingTime <= 0) {
      this.timeLeft = 'Countdown finished!';
      this.countdownSubscription.unsubscribe(); // Stop the countdown
    } else {
      const seconds = Math.floor((remainingTime / 1000) % 60);
      const minutes = Math.floor((remainingTime / 1000 / 60) % 60);
      const hours = Math.floor((remainingTime / 1000 / 3600) % 24);
      const days = Math.floor(remainingTime / (1000 * 60 * 60 * 24)); // Added days calculation

      this.timeLeft = `${this.pad(days)} days ${this.pad(hours)}:${this.pad(minutes)}:${this.pad(seconds)}`;
    }
  }

  private pad(num: number): string {
    return num < 10 ? '0' + num : '' + num;
  }
}
