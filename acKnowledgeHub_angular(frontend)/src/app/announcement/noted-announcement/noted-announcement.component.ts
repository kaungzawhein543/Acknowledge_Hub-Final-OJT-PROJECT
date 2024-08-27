import { Component, OnInit, inject } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { StaffService } from '../../services/staff.service';
import { NotedUser } from '../../models/noted-user';

@Component({
  selector: 'app-noted-announcement',
  templateUrl: './noted-announcement.component.html',
  styleUrl: './noted-announcement.component.css'
})
export class NotedAnnouncementComponent implements OnInit{
  searchTerm: string = '';
  filteredData: NotedUser[] = [];
  list: NotedUser[] = [];
  announcementId!: number;
  constructor(private service: StaffService, private router: Router, private route: ActivatedRoute) { };

  ngOnInit() {
    this.announcementId = +this.route.snapshot.params['id'];
    this.getNotedList(this.announcementId);
  }
  getNotedList(id: number) {
    this.service.getNotedUserByAnnouncementList(this.announcementId).subscribe({
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
      header: ('Staff ID'),
      field: 'staffId',
      sortable: true,
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Name'),
      field: 'name',
      sortable: true,
      disabled: true,
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Position'),
      field: 'positionName',
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Department'),
      field: 'departmentName',
      minWidth: 140,
      width: '140px',
    },
    {
      header: ('Company'),
      field: 'companyName',
      minWidth: 120,
      width: '140px',
    },

  ];

  showToolbar = true;
  columnHideable = true;
  columnSortable = true;
  rowHover = true;
  rowStriped = true;
  showPaginator = true;
  //announcementId : number | undefined ;

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
