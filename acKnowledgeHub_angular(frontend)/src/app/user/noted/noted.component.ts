import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-noted',
  templateUrl: './noted.component.html',
  styleUrl: './noted.component.css'
})
export class NotedComponent {
  publicId: string | null = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {

    this.publicId = this.route.snapshot.queryParamMap.get('announcementId');
    console.log("here is announcement id : " + this.publicId)
  }
}
