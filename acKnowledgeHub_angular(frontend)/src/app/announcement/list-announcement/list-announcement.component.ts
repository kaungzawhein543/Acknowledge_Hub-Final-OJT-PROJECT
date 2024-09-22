import { Component, HostListener, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { announcement } from '../../models/announcement';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import jsPDF from 'jspdf';
import * as XLSX from 'xlsx';
import saveAs from 'file-saver';
import autoTable from 'jspdf-autotable';
import { AnnouncementService } from '../../services/announcement.service';
import { listAnnouncement } from '../../models/announcement-list';

@Component({
  selector: 'app-list-announcement',
  templateUrl: './list-announcement.component.html',
  styleUrl: './list-announcement.component.css'
})
export class ListAnnouncementComponent {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  announcements: listAnnouncement[] = [];
  filteredAnnouncements: listAnnouncement[] = [];
  dataSource = new MatTableDataSource<listAnnouncement>([]);
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
    { field: 'file', header: 'Versions' },
    { field: 'scheduleAt', header: 'Created At' },
    { field: 'note', header: 'Noted/UnNoted' },
    { field: 'detail', header: 'Details' },
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

  getVersionNumber(title: string): string | null {
    const match = title.match(/V(\d+)/);
    return match ? match[1] : null;
  }

  fetchAnnouncements() {
    this.announcementService.getPublishAnnouncements().subscribe(
      (data) => {
        this.announcements = data.map((item, index) => ({
          ...item,
          autoNumber: this.generateAutoNumber(index + 1) // Assign sequential number
        })); this.filteredAnnouncements = data;
        this.dataSource.data = this.filteredAnnouncements;
        this.dataSource.paginator = this.paginator;
        this.filterAnnouncements();
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
        a.category?.toLowerCase() || '',
        a.createStaff?.toLowerCase() || '',
        new Date(a.created_at).toLocaleString().toLowerCase(),
        new Date(a.scheduleAt).toLocaleString().toLowerCase()
      ];
      return fieldsToSearch.some(field => field.includes(query));
    });
    this.dataSource.data = this.filteredAnnouncements;
  }

  onActiveCheckboxChange(event: any) {
    this.activeChecked = event.target.checked;
    this.filterAnnouncements();
  }

  onInactiveCheckboxChange(event: any) {
    this.inactiveChecked = event.target.checked;
    this.filterAnnouncements();
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

  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isReportDropdownOpen = false;
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
    // Exclude 'note' and 'detail' columns from the report
    const visibleColumns = this.columns
      .filter(col => this.columnVisibility[col.field] && col.field !== 'note' && col.field !== 'detail');

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


  generateExcel(announcements: listAnnouncement[], fileName: string) {
    // Exclude 'note' and 'detail' columns from the report
    const visibleColumns = this.columns
      .filter(col => this.columnVisibility[col.field] && col.field !== 'note' && col.field !== 'detail');

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

  filterAnnouncements() {
    this.filteredAnnouncements = this.announcements.filter(a => {
      const isActive = this.activeChecked && a.status.trim().toLowerCase() === 'active';
      const isInactive = this.inactiveChecked && a.status.trim().toLowerCase() === 'inactive';
      return (isActive || isInactive || (!this.activeChecked && !this.inactiveChecked));
    }).filter(a => {
      if (this.startDateTime && this.endDateTime) {
        const scheduleAt = new Date(a.scheduleAt);
        return scheduleAt >= new Date(this.startDateTime) && scheduleAt <= new Date(this.endDateTime);
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

  onNotedButtonClick(id: number, name: string, file: string) {
    const encodedId = btoa(id.toString());
    const encodedName = btoa(name);
    const encodedFile = btoa(file);
    this.router.navigate(['/acknowledgeHub/announcement/noted-announcement/' + encodedId + '/' + encodedName + '/' + encodedFile]);
  }

  onUnNotedButtonClick(id: number, groupStatus: number, name: string, file: string) {
    const encodedId = btoa(id.toString());
    const encodedName = btoa(name);
    const encodedStatus = btoa(groupStatus.toString());
    const encodedFile = btoa(file);
    this.router.navigate(['announcement/notNoted-announceemnt/' + encodedId + '/' + encodedStatus + '/' + encodedName + '/' + encodedFile])
  }

  onDetailButtonClick(id: number) {
    if(id){
      this.router.navigate(['/acknowledgeHub/announcement/detail/' + btoa(id.toString())]);
    }
  }
}
