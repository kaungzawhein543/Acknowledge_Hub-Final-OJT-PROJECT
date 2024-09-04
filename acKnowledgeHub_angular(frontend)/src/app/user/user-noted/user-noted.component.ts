import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { announcement } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement.service';
import { AuthService } from '../../services/auth.service';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { staffNotedAnnouncement } from '../../models/staff-noted-announcement';

@Component({
  selector: 'app-user-noted',
  templateUrl: './user-noted.component.html',
  styleUrl: './user-noted.component.css'
})
export class UserNotedComponent implements OnInit {
  @ViewChild('fileTemplate', { static: true }) fileTemplate!: TemplateRef<any>;

  searchTerm: string = '';
  filteredData: staffNotedAnnouncement[] = [];
  list: staffNotedAnnouncement[] = [];
  announcementId!: number;
  staffId !: number;
  columns: MtxGridColumn[] = [];
  constructor(private service: AnnouncementService, private authService: AuthService) { };

  ngOnInit() {
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        this.staffId = data.user.id;
        this.getNotedList(this.staffId)
      },
      error: (e) => console.log(e)
    });
    this.initializeColumns();
  }

  getNotedList(userId: number) {
    this.service.userNotedAnnouncement(userId).subscribe({
      next: (data) => {
        this.list = data.map((item, index) => ({ ...item, autoNumber: index + 1 }));
        this.filteredData = data.map((item, index) => ({ ...item, autoNumber: index + 1 }));
      },
      error: (e) => console.log(e)
    });
  }

  initializeColumns() {
    this.columns = [
      {
        header: ('No.'),
        field: 'autoNumber',
        type: 'number',
        sortable: false,
        minWidth: 80,
        width: '80px',
      },
      {
        header: ('Title'),
        field: 'title',
        sortable: true,
        disabled: true,
        minWidth: 140,
        width: '140px',
      },
      {
        header: ('Description'),
        field: 'description',
        minWidth: 140,
        width: '140px',
      },
      {
        header: ('Create At'),
        field: 'createdAt',
        minWidth: 140,
        width: '140px',
      },
      {
        header: ('Noted At'),
        field: 'notedAt',
        minWidth: 140,
        width: '140px',
      },
      {
        header: ('Announcement'),
        field: 'file',
        minWidth: 140,
        width: '140px',
        cellTemplate: this.fileTemplate,
      },
    ];
  }
  showToolbar = true;
  columnHideable = true;
  columnSortable = true;
  rowHover = true;
  rowStriped = true;
  showPaginator = true;

  searchData() {
    if (this.searchTerm) {
      this.filteredData = this.list.filter(item =>
        Object.values(item).some(val =>
          val.toString().toLowerCase().includes(this.searchTerm.toLowerCase())
        )
      )
        .map((item, index) => ({ ...item, autoNumber: index + 1 }));
    } else {
      this.filteredData = this.list.map((item, index) => ({ ...item, autoNumber: index + 1 }));
    }
  }

  changeSelect(e: any) {
    console.log(e);
  }

  changeSort(e: any) {
    console.log(e);
  }
}
