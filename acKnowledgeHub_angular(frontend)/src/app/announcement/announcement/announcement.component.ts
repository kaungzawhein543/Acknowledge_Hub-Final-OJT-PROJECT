import { Component } from '@angular/core';
import { AnnouncementService } from '../../services/announcement.service';
import { response } from 'express';
import { announcement } from '../../models/announcement';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrl: './announcement.component.css'
})
export class AnnouncementComponent {
  announcement: any = {
    id: 0,
    title: '',
    description: '', 
  };

  constructor(private announcementService: AnnouncementService) {}

 

  onSubmit(form: any): void {
    if (!form.valid) {
      return;
    }

    this.announcement.title = form.value.title;
    this.announcement.description = form.value.description;

    // Send form data to the service
    // this.announcementService.createAnnouncement(this.announcement).subscribe(
    //   response => {
    //     console.log('Announcement created:', response);
    //     this.announcement = { title: '', description: '' }; // Reset announcement object
    //     form.reset(); // Reset form
    //   },
    //   error => {
    //     console.error('Error creating announcement:', error);
    //   }
    // );
  }


  getAnnouncement(id:number):void{
    this.announcementService.getAnnouncementById(id).subscribe(
      (response : announcement) =>{
        console.log(response);
      },
      error => {
        console.error('Error getting announcement', error);
      }
    )
  }

  deleteAnnouncement(id : number){
    this.announcementService.deleteAnnouncement(id);
  }
}
