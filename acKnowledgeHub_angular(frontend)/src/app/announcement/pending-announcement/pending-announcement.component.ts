import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { announcement } from '../../models/announcement';
import { MatTableDataSource } from '@angular/material/table';
import { AnnouncementService } from '../../services/announcement.service';
import { ActivatedRoute, Router } from '@angular/router';
import { announcementList } from '../../models/announcement-list';

@Component({
  selector: 'app-pending-announcement',
  templateUrl: './pending-announcement.component.html',
  styleUrl: './pending-announcement.component.css'
})
export class PendingAnnouncementComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  announcements: announcementList[] = [];
  filteredAnnouncements: announcementList[] = [];
  dataSource = new MatTableDataSource<announcementList>([]);
  searchQuery: string = '';
  startDateTime: string | null = null;
  endDateTime: string | null = null;
  todayDate: string | undefined;
  activeChecked = false;
  inactiveChecked = false;
  isFilterDropdownOpen = false;
  isReportDropdownOpen = false;

  columns = [
    { field: 'autoNumber', header: 'No.' },
    { field: 'title', header: 'Title' },
    { field: 'description', header: 'Description' },
    { field: 'createStaff', header: 'Create/Request Staff' },
    { field: 'createdAt', header: 'Create At' },
    { field: 'category', header: 'Category' },
    { field: 'file', header: 'View' },
  ];

  columnVisibility: { [key: string]: boolean } = {};
  selectedColumns = this.columns.map(col => col.field);

  constructor(
    private announcementService: AnnouncementService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.todayDate = new Date().toISOString().split('T')[0];
    this.fetchAnnouncements();
    this.columns.forEach(col => (this.columnVisibility[col.field] = true));
  }

  generateAutoNumber(index: number): string {
    return index.toString(); // Adjust 6 to the desired length
  }

  fetchAnnouncements() {
    this.announcementService.pendingAnnouncementBySchedule().subscribe(
      (data) => {
        this.announcements = data.map((item, index) => ({
          ...item,
          autoNumber: this.generateAutoNumber(index + 1) // Assign sequential number
        }));
        this.filteredAnnouncements = this.announcements;
        this.dataSource.data = this.filteredAnnouncements;
        this.dataSource.paginator = this.paginator;
      },
      (error) => console.error('Error fetching announcements:', error)
    );
  }

  toggleFilterDropdown() {
    this.isFilterDropdownOpen = !this.isFilterDropdownOpen;
    if (this.isReportDropdownOpen) this.isReportDropdownOpen = false; // Close report dropdown if open
  }

  toggleReportDropdown() {
    this.isReportDropdownOpen = !this.isReportDropdownOpen;
    if (this.isFilterDropdownOpen) this.isFilterDropdownOpen = false; // Close filter dropdown if open
  }
  onSearchChange() {
    const query = this.searchQuery.toLowerCase();
    this.filteredAnnouncements = this.announcements.filter(a => {
      const fieldsToSearch = [
        a.title?.toLowerCase() || '',
        a.description?.toLowerCase() || '',
        a.file?.toLowerCase() || '',
        a.createStaff?.toLowerCase() || '',
        a.category?.toLowerCase() || '',
        new Date(a.createdAt).toLocaleString().toLowerCase(),
      ];
      return fieldsToSearch.some(field => field.includes(query));
    });
    this.dataSource.data = this.filteredAnnouncements;
  }

  onActiveCheckboxChange(event: any) {
    this.activeChecked = event.target.checked;

  }

  onInactiveCheckboxChange(event: any) {
    this.inactiveChecked = event.target.checked;
  }

  onStartDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.startDateTime = input.value || null;
    this.validateDateRange();

  }

  onEndDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const today = new Date().toISOString().split('T')[0];
    this.endDateTime = input.value <= today ? input.value : today;
    this.validateDateRange();
  }

  validateDateRange() {
    if (this.startDateTime && this.endDateTime && this.startDateTime > this.endDateTime) {
      this.startDateTime = null;
    }
  }


  getNestedProperty(obj: any, path: string): any {
    if (!obj || !path) return null;
    return path.split('.').reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : null), obj);
  }

  onColumnVisibilityChange() {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field]);
    this.selectedColumns = visibleColumns.map(col => col.field);
    this.dataSource.data = [...this.filteredAnnouncements];
  }

  formatDateTime(datetime: string): string {
    const date = new Date(datetime);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  }

  onFileButtonClick(file: string) {
    if (file) {
      window.open(file, '_blank'); // Open the file in a new tab
    } else {
      console.log('No file available for this announcement');
    }
  }


}
