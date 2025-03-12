import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { LoadingService } from '../../services/loading.service';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent implements OnInit, OnDestroy {
  loading = false;
  private loadingSubscription !: Subscription;

  constructor(private loadingService: LoadingService) {}

  ngOnInit() {
    this.loadingSubscription = this.loadingService.loading$.subscribe(isLoading => {
      this.loading = isLoading;
    });
  }

  ngOnDestroy() {
    this.loadingSubscription.unsubscribe(); // Clean up subscription
  }
}