import { Component, OnInit } from '@angular/core';
import { announcement } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement.service';
import { ActivatedRoute } from '@angular/router';
import { response } from 'express';
import { error } from 'console';

@Component({
  selector: 'app-update-announcement',
  templateUrl: './update-announcement.component.html',
  styleUrl: './update-announcement.component.css'
})
export class UpdateAnnouncementComponent implements OnInit{

  announcement: any = {
    id:0,
    title: '',
    description: ''
  };
  announcementId!: number;
  deleteAnnounceId!: number;
  constructor(private announcementService: AnnouncementService,private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.announcementId = +this.route.snapshot.paramMap.get('id')!;

        this.announcementService.getAnnouncementById(this.announcementId).subscribe(
          (response : announcement) =>{
        this.announcement = response;
      },
      error => {
        console.error('Error getting announcement', error);
      }
    )
  
  }
  deleteAnnouncementById():void{
    this.announcementService.deleteAnnouncement(this.deleteAnnounceId).subscribe(
      (response : string)=>{
        console.log(response);
      },
      (error : any)=>{
        console.log(error);
      }
    )
  }
  onSubmit(form: any): void {
    if (!form.valid) {
      return;
    }

    this.announcement.title = form.value.title;
    this.announcement.description = form.value.description;

    // Send form data to the service
    this.announcementService.editAnnouncement(this.announcement).subscribe(
      response => {
        console.log('Announcement Edited:', response);
        this.announcement = { title: '', description: '' }; // Reset announcement object
        form.reset(); // Reset form
      },
      error => {
        console.error('Error creating announcement:', error);
      }
    );
  }
  downloadPdf() {
    this.announcementService.downloadPdf('Announce1').subscribe(response => {
      const blob = new Blob([response], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'your-file.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

}
