import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { StaffService } from '../../services/staff.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { UnNotedUser } from '../../models/un-noted-user';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import saveAs from 'file-saver';
import { AnnouncementService } from '../../services/announcement.service';
import { announcementVersion } from '../../models/announcement-version';

@Component({
  selector: 'app-not-noted-announcement',
  templateUrl: './not-noted-announcement.component.html',
  styleUrl: './not-noted-announcement.component.css'
})
export class NotNotedAnnouncementComponent {

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  staffs: UnNotedUser[] = [];
  filteredStaffs: UnNotedUser[] = [];
  dataSource = new MatTableDataSource<UnNotedUser>([]);
  searchQuery: string = '';
  startDateTime: string | null = null;
  endDateTime: string | null = null;
  todayDate: string | undefined;
  activeChecked = false;
  inactiveChecked = false;
  announcementId !: number;
  isFilterDropdownOpen = false;
  isReportDropdownOpen = false;
  groupStatus !: number;
  versions: announcementVersion[] = [];
  selectedVersionId!: number;
  announcementName !: string;
  announcementFile !: string;
  originId !: number;
  columns = [
    { field: 'autoNumber', header: 'No.' },
    { field: 'staffId', header: 'Staff Id' },
    { field: 'name', header: 'Name' },
    { field: 'email', header: 'Email' },
    { field: 'positionName', header: 'Position' },
    { field: 'departmentName', header: 'Department' },
    { field: 'companyName', header: 'Company' },
  ];

  columnVisibility: { [key: string]: boolean } = {};
  selectedColumns = this.columns.map(col => col.field);

  constructor(
    private staffService: StaffService,
    private router: Router,
    private route: ActivatedRoute,
    private announcementService: AnnouncementService
  ) { }

  ngOnInit() {
    this.todayDate = new Date().toISOString().split('T')[0];
    this.route.params.subscribe((params: Params) => {
      const decodedStringId = atob(params['id']);
      const decodedStringStatus = atob(params['status']);
      const decodedStringName = atob(params['name']);
      const decodedStringFile = atob(params['file']);
      this.announcementId = parseInt(decodedStringId, 10);
      this.groupStatus = parseInt(decodedStringStatus, 10);
      this.announcementName = decodedStringName;
      this.announcementFile = decodedStringFile;
    });
    this.getUnNotedStaffList(this.announcementId, this.groupStatus);
    this.getVersions();
    //  this.columns.forEach(col => (this.columnVisibility[col.field] = true));
  }
  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isReportDropdownOpen = false;
    }
  }
  generateAutoNumber(index: number): string {
    return index.toString(); // Adjust 6 to the desired length
  }

  getVersions() {
    let match = this.announcementFile.match(/\/Announce(\d+)\//);
    if (match) {
      this.originId = parseInt(match[1]);
    }
    this.announcementService.getAnnouncementVersions(this.originId).subscribe({
      next: (data) => {
        this.versions = data;
        if (this.versions.length > 0) {
          this.selectedVersionId = this.versions[this.versions.length - 1].id;
        }

      },
      error: (e) => console.log(e)
    });
    this.columns.forEach(col => (this.columnVisibility[col.field] = true));
  }

  onVersionChange(newVersionId: number) {
    this.selectedVersionId = newVersionId;
    if (this.selectedVersionId !== null) {
      this.getUnNotedStaffList(this.selectedVersionId, this.groupStatus);
    }
  }

  getVersionNumber(title: string): string | null {
    const match = title.match(/V(\d+)/);
    return match ? match[1] : null;
  }

  getUnNotedStaffList(id: number, groupStatus: number) {
    this.staffService.getUnNotedStaffByAnnouncementList(id, groupStatus).subscribe(
      (data) => {
        this.staffs = data.map((item, index) => ({
          ...item,
          autoNumber: this.generateAutoNumber(index + 1) // Assign sequential number
        })); this.filteredStaffs = data;
        this.dataSource.data = this.filteredStaffs;
        this.dataSource.paginator = this.paginator;
        //  this.filterAnnouncements();
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
    this.filteredStaffs = this.staffs.filter(a => {
      const fieldsToSearch = [
        a.staffId?.toLowerCase() || '',
        a.name?.toLowerCase() || '',
        a.email?.toLowerCase() || '',
        a.positionName?.toLowerCase() || '',
        a.departmentName?.toLowerCase() || '',
        a.companyName?.toLowerCase() || '',
      ];
      return fieldsToSearch.some(field => field.includes(query));
    });
    this.dataSource.data = this.filteredStaffs;
  }



  onInactiveCheckboxChange(event: any) {
    this.inactiveChecked = event.target.checked;
    //  this.filterAnnouncements();
  }

  onStartDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.startDateTime = input.value || null;
    this.validateDateRange();
    //  this.filterAnnouncements();
  }

  onEndDateChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const today = new Date().toISOString().split('T')[0];
    this.endDateTime = input.value <= today ? input.value : today;
    this.validateDateRange();
    //  this.filterAnnouncements();
  }

  validateDateRange() {
    if (this.startDateTime && this.endDateTime && this.startDateTime > this.endDateTime) {
      this.startDateTime = null;
    }
  }

  generateReport(format: 'pdf' | 'excel') {
    if (format === 'pdf') {
      this.generatePDF(this.filteredStaffs, 'report.pdf');
    } else if (format === 'excel') {
      this.generateExcel(this.filteredStaffs, 'report.xlsx');
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


  generateExcel(announcements: UnNotedUser[], fileName: string) {
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



  getNestedProperty(obj: any, path: string): any {
    if (!obj || !path) return null;
    return path.split('.').reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : null), obj);
  }

  onColumnVisibilityChange() {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field]);
    this.selectedColumns = visibleColumns.map(col => col.field);
    //  this.dataSource.data = [...this.filteredAnnouncements];
  }

  formatDateTime(datetime: string): string {
    const date = new Date(datetime);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  }
}
