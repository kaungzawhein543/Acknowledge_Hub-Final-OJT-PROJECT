import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { AnnouncementService } from '../../services/announcement.service';
import { requestAnnouncement } from '../../models/announcement-list';
import { ActivatedRoute, Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import saveAs from 'file-saver';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
@Component({
  selector: 'app-request-list',
  templateUrl: './request-list.component.html',
  styleUrl: './request-list.component.css'
})
export class RequestListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  requestAnnouncements: requestAnnouncement[] = [];
  filteredAnnouncements: requestAnnouncement[] = [];
  dataSource = new MatTableDataSource<requestAnnouncement>([]);
  searchQuery: string = '';
  startDateTime: string | null = null;
  endDateTime: string | null = null;
  todayDate: string | undefined;
  activeChecked = false;
  inactiveChecked = false;
  isFilterDropdownOpen = false;
  isReportDropdownOpen = false;
  rejectAnnouncementId !: number;
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;

  columns = [
    { field: 'autoNumber', header: 'No.' },
    { field: 'title', header: 'Title' },
    { field: 'description', header: 'Description' },
    { field: 'createdAt', header: 'Created At' },
    { field: 'scheduleAt', header: 'Schedule At' },
    { field: 'createStaff', header: 'Request Staff' },
    { field: 'action', header: 'Action' },
    { field: 'detail', header: 'Detail' },
  ];

  columnVisibility: { [key: string]: boolean } = {};
  selectedColumns = this.columns.map(col => col.field);
  constructor(private announcementService: AnnouncementService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.todayDate = new Date().toISOString().split('T')[0];
    this.fetchAnnouncements();
    this.columns.forEach(col => (this.columnVisibility[col.field] = true));
  }

  fetchAnnouncements() {
    this.announcementService.getRequestAnnouncementList().subscribe({
      next: (data) => {
        this.requestAnnouncements = data.map((item, index) => ({
          ...item,
          autoNumber: this.generateAutoNumber(index + 1) // Assign sequential number
        }));
        console.log(data)
        this.filteredAnnouncements = this.requestAnnouncements;
        this.dataSource.data = this.filteredAnnouncements;
        this.dataSource.paginator = this.paginator;
        this.filterAnnouncements();
      },
      error: (e) => console.log(e)
    });
  }

  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isReportDropdownOpen = false;
    }
  }

  onApprovedButtonClick(id: number) {
    this.announcementService.approvedRequestAnnouncement(id).subscribe({
      next: (data: boolean) => {
        this.fetchAnnouncements();
      },
      error: (e) => console.log(e)
    });
  }

  onRejectButtonClick() {
    this.announcementService.rejectRequestAnnouncement(this.rejectAnnouncementId).subscribe({
      next: (data: boolean) => {
        this.fetchAnnouncements();
      },
      error: (e) => console.log(e)
    });
  }

  getRequestStaff(announcement: any): string {
    const staffName = announcement.createStaff || 'N/A';
    const companyName = announcement.staffCompany || 'N/A';
    return `${staffName} (${companyName})`;
  }

  generateAutoNumber(index: number): string {
    return index.toString(); // Adjust 6 to the desired length
  }

  toggleReportDropdown() {
    this.isReportDropdownOpen = !this.isReportDropdownOpen;
    if (this.isFilterDropdownOpen) this.isFilterDropdownOpen = false; // Close filter dropdown if open
  }
  onSearchChange() {
    const query = this.searchQuery.toLowerCase();
    this.filteredAnnouncements = this.requestAnnouncements.filter(a => {
      const fieldsToSearch = [
        a.title?.toLowerCase() || '',
        a.description?.toLowerCase() || '',
        a.category?.toLowerCase() || '',
        a.createStaff?.toLowerCase() || '',
        new Date(a.createdAt).toLocaleString().toLowerCase(),
        new Date(a.scheduleAt).toLocaleString().toLowerCase()
      ];
      return fieldsToSearch.some(field => field.includes(query));
    });
    this.dataSource.data = this.filteredAnnouncements;
  }

  onStartDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.startDateTime = input.value || null;
    this.validateDateRange();
    this.filterAnnouncements();
  }

  onEndDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const today = new Date().toISOString().split('T')[0];
    this.endDateTime = input.value <= today ? input.value : today;
    this.validateDateRange();
    this.filterAnnouncements();
  }

  validateDateRange() {
    if (this.startDateTime && this.endDateTime && this.startDateTime > this.endDateTime) {
      this.startDateTime = null;
    }
  }

  generateReport(format: 'pdf' | 'excel') {
    if (format === 'pdf') {
      this.generatePDF(this.filteredAnnouncements, 'report.pdf');
    } else if (format === 'excel') {
      this.generateExcel(this.filteredAnnouncements, 'report.xlsx');
    }
  }

  generatePDF(announcements: any[], filename: string) {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field]);
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
        // Add more column styles as needed
      },
      tableWidth: 'auto', // Auto width adjustment for table
    });

    // Save the PDF file
    doc.save(filename);
  }


  generateExcel(announcements: requestAnnouncement[], fileName: string) {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field]);
    const headers = visibleColumns.map(col => col.header);
    const data = [headers, ...announcements.map(a => visibleColumns.map(col => col.field.split('.').reduce((o, k) => o?.[k], a) || ''))];
    const worksheet = XLSX.utils.aoa_to_sheet(data);
    const workbook = { Sheets: { 'Report': worksheet }, SheetNames: ['Report'] };
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, fileName);
  }

  private saveAsExcelFile(buffer: any, fileName: string) {
    const data = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    saveAs(data, fileName);
  }

  filterAnnouncements() {
    this.filteredAnnouncements = this.requestAnnouncements.filter(a => {
      if (this.startDateTime && this.endDateTime) {
        const createdAt = new Date(a.createdAt);
        return createdAt >= new Date(this.startDateTime) && createdAt <= new Date(this.endDateTime);
      }
      return true;
    });
    this.dataSource.data = this.filteredAnnouncements;
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

  onDetailButtonClick(id: number) {
    this.router.navigate(['/acknowledgeHub/announcement/detail/' + btoa(id.toString())]);
  }
  openCorfirmModal(id: number) {
    this.rejectAnnouncementId = id;
    this.modal.showReasonInput = true;
    this.modal.open();
  }

  onModalConfirm(event: { reason: string }) {
    const reason = event.reason;
    this.onRejectButtonClick();
  }
}

