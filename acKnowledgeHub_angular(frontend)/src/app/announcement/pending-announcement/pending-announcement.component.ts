import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { announcement } from '../../models/announcement';
import { MatTableDataSource } from '@angular/material/table';
import { AnnouncementService } from '../../services/announcement.service';
import { ActivatedRoute, Router } from '@angular/router';
import { announcementList } from '../../models/announcement-list';
import autoTable from 'jspdf-autotable';
import jsPDF from 'jspdf';
import * as XLSX from 'xlsx';
import saveAs from 'file-saver';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { AuthService } from '../../services/auth.service';
import { StaffProfileDTO } from '../../models/staff';
import { ToastService } from '../../services/toast.service';


@Component({
  selector: 'app-pending-announcement',
  templateUrl: './pending-announcement.component.html',
  styleUrl: './pending-announcement.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ])
        ]),
      ]),
    ]),
  ],
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
  loginRole !: string;
  isaHrMain: boolean = false; // Initialize the isaHrMain variable
  profile: StaffProfileDTO | null = null;


  columns = [
    { field: 'autoNumber', header: 'No.' },
    { field: 'title', header: 'Title' },
    { field: 'description', header: 'Description' },
    { field: 'category', header: 'Category' },
    { field: 'createStaff', header: 'Create/Request Staff' },
    { field: 'createdAt', header: 'Create At' },
    { field: 'detail', header: 'View' },
    { field: 'cancel', header: 'Action' }
  ];

  columnVisibility: { [key: string]: boolean } = {};
  selectedColumns = this.columns.map(col => col.field);

  constructor(
    private authService :AuthService,
    private announcementService: AnnouncementService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService : ToastService
  ) { }

  ngOnInit() {
    this.todayDate = new Date().toISOString().split('T')[0];
    this.fetchAnnouncements();
    this.columns.forEach(col => (this.columnVisibility[col.field] = true));
    this.authService.getProfile().subscribe(
      (data) => {
        this.profile = data;
        this.loginRole = data.role;

        // Check if the user position is "Human Resource(Main)"
        if (this.profile.position === "Human Resource(Main)") {
          this.isaHrMain = true;
        } else {
          this.isaHrMain = false;
        }
      },
      (error) => {
        console.error('Error fetching profile:', error);
      }
    );
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
        console.log(data);
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

  generateReport(format: 'pdf' | 'excel') {
    if (format === 'pdf') {
      this.generatePDF(this.filteredAnnouncements, 'report.pdf');
    } else if (format === 'excel') {
      this.generateExcel(this.filteredAnnouncements, 'report.xlsx');
    }
  }

  generatePDF(announcements: any[], filename: string) {
    // Exclude 'note' and 'detail' columns from the report
    const visibleColumns = this.columns
      .filter(col => this.columnVisibility[col.field] && col.field !== 'detail');

    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });

    // Define column headers and data rows
    const headers = visibleColumns.map(col => col.header);
    const rows = announcements.map(announcement =>
      visibleColumns.map(col => col.field.split('.').reduce((o, k) => o?.[k], announcement) || '')
    );

    // Calculate column widths based on content length or set manually
    const columnWidths = visibleColumns.map(col => {
      return col.field === 'description' ? 60 : 30; // Adjust widths as needed
    });

    // Use autoTable to generate the table in PDF
    autoTable(doc, {
      head: [headers],
      body: rows,
      startY: 20,
      margin: { top: 20 },
      styles: { fontSize: 10, cellPadding: 4 }, // Adjust fontSize and cellPadding
      headStyles: { fillColor: [79, 129, 189], textColor: [255, 255, 255] },
      columnStyles: {
        0: { cellWidth: columnWidths[0] }, // Adjust width for specific columns
        1: { cellWidth: columnWidths[1] }, // Adjust width for specific columns
      },
      tableWidth: 'auto', // Auto width adjustment for table
    });

    // Save the PDF file
    doc.save(filename);
  }


  generateExcel(announcements: announcementList[], fileName: string) {
    // Exclude 'note' and 'detail' columns from the report
    const visibleColumns = this.columns
      .filter(col => this.columnVisibility[col.field] && col.field !== 'detail');

    const headers = visibleColumns.map(col => col.header);
    const data = [headers, ...announcements.map(a =>
      visibleColumns.map(col => col.field.split('.').reduce((o, k) => o?.[k], a) || '')
    )];

    const worksheet = XLSX.utils.aoa_to_sheet(data);
    const workbook = { Sheets: { 'Report': worksheet }, SheetNames: ['Report'] };
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, fileName);
  }
  private saveAsExcelFile(buffer: any, fileName: string) {
    const data = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    saveAs(data, fileName);
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

  onFileButtonClick(id: number) {
    this.router.navigate(['/acknowledgeHub/announcement/detail/'+btoa(id.toString())]);
  }

  onCancelButtonClick(id: number) {
    this.announcementService.cancelPendingAnnouncement(id).subscribe({
      next: (data: string) => {
        this.ngOnInit();
        this.showCancelSuccessToast();
      },
      error: (e) => console.log(e)
    })
  }

  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isReportDropdownOpen = false;
    }
  }

  onPublishButtonClick(id: number) {
    this.announcementService.postPublishNow(id).subscribe({
      next: (data: string) => {
        this.fetchAnnouncements();
      },
      error: (e) => console.log(e)
    })
  }

  setColumnsBasedOnRole() {
    if (this.loginRole == 'ADMIN') {
      this.columns = this.columns.filter(col => col.field !== 'action');
    }
    this.selectedColumns = this.columns.map(col => col.field);
  }
  showCancelSuccessToast() {
    this.toastService.showToast('Add Group  successful!', 'success');
  }
}