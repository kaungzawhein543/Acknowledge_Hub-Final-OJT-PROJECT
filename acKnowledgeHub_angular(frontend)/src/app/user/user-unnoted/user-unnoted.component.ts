import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { AnnouncementService } from '../../services/announcement.service';
import { AuthService } from '../../services/auth.service';
import { announcementList } from '../../models/announcement-list';

@Component({
  selector: 'app-user-unnoted',
  templateUrl: './user-unnoted.component.html',
  styleUrl: './user-unnoted.component.css'
})
export class UserUnnotedComponent {
  searchTerm: string = '';
  filteredData: announcementList[] = [];
  list: announcementList[] = [];
  announcementId!: number;
  staffId !: number;
  constructor(private service: AnnouncementService, private authService: AuthService) { };

  ngOnInit() {
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        this.staffId = data.user.id;
        this.getNotedList(this.staffId)
      },
      error: (e) => console.log(e)
    });

  }

  getNotedList(userId: number) {
    this.service.userUnNotedAnnouncement(userId).subscribe({
      next: (data) => {
        this.list = data.map((item, index) => ({ ...item, autoNumber: index + 1 }));
        this.filteredData = data.map((item, index) => ({ ...item, autoNumber: index + 1 }));
      },
      error: (e) => console.log(e)
    });
  }


  columns: MtxGridColumn[] = [
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
      maxWidth: 400,
    },
    {
      header: ('Create At'),
      field: 'createdAt',
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Create By'),
      field: 'createStaff',
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Announcement'),
      field: 'file',
      minWidth: 140,
      width: '140px',
    },
    {

      field: 'operation',
      minWidth: 140,
      width: '140px',
      pinned: 'right',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'check_circle',
          click: record => this.edit(record),
        },
        {
          type: 'icon',
          color: 'warn',
          icon: 'cancel',
          click: record => this.delete(record),
        },
      ],
    },
  ];

  delete(data: any) {

  }
  edit(data: any) {

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
