import { Component, HostListener, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { staffList } from '../../models/staff';
import { MatTableDataSource } from '@angular/material/table';
import { StaffService } from '../../services/staff.service';
import { ActivatedRoute, Router } from '@angular/router';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import saveAs from 'file-saver';
import { AuthService } from '../../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { MatSelectionList } from '@angular/material/list';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';


@Component({
  selector: 'app-list-user',
  templateUrl: './list-user.component.html',
  styleUrl: './list-user.component.css',
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
export class ListUserComponent {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  announcements: staffList[] = [];
  filteredStaffs: staffList[] = [];
  dataSource = new MatTableDataSource<staffList>([]);
  searchQuery: string = '';
  startDateTime: string | null = null;
  endDateTime: string | null = null;
  todayDate: string | undefined;
  activeChecked = false;
  inactiveChecked = false;
  loginRole !: string;
  isFilterDropdownOpen = false;
  idnumbertoInactive : number = 0;
  isReportDropdownOpen = false;
  @ViewChild('staff') staff!: MatSelectionList;
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  columns = [
    { field: 'autoNumber', header: 'No.' },
    { field: 'companyStaffId', header: 'Staff Id' },
    { field: 'name', header: 'Name' },
    { field: 'email', header: 'Email' },
    { field: 'position', header: 'Position' },
    { field: 'department', header: 'Department' },
    { field: 'company', header: 'Company' },
    { field: 'status', header: 'Action' }
  ];

  columnVisibility: { [key: string]: boolean } = {};
  selectedColumns = this.columns.map(col => col.field);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private staffService: StaffService,
    private authService: AuthService,
  ) { }

  ngOnInit() {
    this.todayDate = new Date().toISOString().split('T')[0];
    this.fetchStaffs();
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        this.loginRole = data.user.role;
      }
    })
    this.columns.forEach(col => (this.columnVisibility[col.field] = true));
  }

  generateAutoNumber(index: number): string {
    return index.toString(); // Adjust 6 to the desired length
  }

  fetchStaffs() {
    this.staffService.getList().subscribe(
      (data) => {
        this.announcements = data.map((item, index) => ({
          ...item,
          autoNumber: this.generateAutoNumber(index + 1) // Assign sequential number
        }));
        this.filteredStaffs = data;
        //  this.dataSource.data = this.filteredStaffs;
        this.dataSource.paginator = this.paginator;
        this.filterAnnouncements();
        this.activeChecked = true;
        this.inactiveChecked = true;
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
    const query = this.searchQuery.toLowerCase().trim();
    this.filteredStaffs = this.announcements.filter(a => {
      const fieldsToSearch = [
        a.companyStaffId?.toLowerCase() || '',
        a.name?.toLowerCase() || '',
        a.email?.toLowerCase() || '',
        a.position?.toLowerCase() || '',
        a.department?.toLowerCase() || '',
        a.company?.toLowerCase() || ''
      ];
      return fieldsToSearch.some(field => field.includes(query));
    });
    this.dataSource.data = this.filteredStaffs;
  }

  onActiveCheckboxChange(event: any) {
    this.activeChecked = event.target.checked;
    this.filterAnnouncements();
  }

  onInactiveCheckboxChange(event: any) {
    this.inactiveChecked = event.target.checked;
    this.filterAnnouncements();
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
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field] && col.field !== 'status');
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


  generateExcel(announcements: staffList[], fileName: string) {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field] && col.field !== 'status');
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
    this.filteredStaffs = this.announcements.filter(a => {
      const isActive = this.activeChecked && a.status.trim().toLowerCase() === 'active';
      const isInactive = this.inactiveChecked && a.status.trim().toLowerCase() === 'inactive';
      return (isActive || isInactive || (!this.activeChecked && !this.inactiveChecked));
    }).filter(a => {
      // if (this.startDateTime && this.endDateTime) {
      //   const scheduleAt = new Date(a.scheduleAt);
      //   return scheduleAt >= new Date(this.startDateTime) && scheduleAt <= new Date(this.endDateTime);
      // }
      return true;
    });
    this.dataSource.data = this.filteredStaffs;
  }

  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isReportDropdownOpen = false;
    }
  }
  getNestedProperty(obj: any, path: string): any {
    if (!obj || !path) return null;
    return path.split('.').reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : null), obj);
  }

  onColumnVisibilityChange() {
    const visibleColumns = this.columns.filter(col => this.columnVisibility[col.field]);
    this.selectedColumns = visibleColumns.map(col => col.field);
    this.dataSource.data = [...this.filteredStaffs];
  }

  formatDateTime(datetime: string): string {
    const date = new Date(datetime);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  }

  onActiveButtonClick(id: number) {
    this.staffService.activateStaff(id).subscribe({
      next: (data: string) => {
        console.log(data);
        this.ngOnInit();
      },
      error: (e) => console.log(e)
    })
  }

  inActiveButtonClick(id: number) {
    this.staffService.InactivateStaff(id).subscribe({
      next: (data: string) => {
        console.log(data);
        this.ngOnInit();
      },
      error: (e) => console.log(e)
    })
  }
  openInactiveModal(id : number) {
    this.idnumbertoInactive = id;
    this.modal.open();
  }
}
