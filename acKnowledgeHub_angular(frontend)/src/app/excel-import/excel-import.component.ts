import { Component, ViewChild } from '@angular/core';
import { ExcelServiceService } from '../services/excel.service';
import * as XLSX from 'xlsx';
import { ConfirmationModalComponent } from '../confirmation-modal/confirmation-modal.component';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-excel-import',
  templateUrl: './excel-import.component.html',
  styleUrls: ['./excel-import.component.css']
})
export class ExcelImportComponent {
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  file: File | null = null;
  fileError: string | null = null;
  staffs: { id: number, name: string, email: string, position: string, department: string, company: string }[] = [];
  filteredStaffs: { id: number, name: string, email: string, position: string, department: string, company: string }[] = [];
  currentPageStaffs: any[] = [];
  fileUploaded = false;
  currentPage = 1;
  itemsPerPage = 5;  // Number of staff to display per page
  searchTerm: string = '';

  constructor(private excelImportService: ExcelServiceService, private toastService: ToastService) { }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const selectedFile = input.files[0];

      if (this.validateFile(selectedFile)) {
        this.file = selectedFile;
        this.fileError = null;

        const reader = new FileReader();

        reader.onload = (e: ProgressEvent<FileReader>) => {
          const binaryData = e.target?.result as string;
          const workbook = XLSX.read(binaryData, { type: 'binary' });
        
          // Assuming the staff data is in the first sheet
          const sheetName = workbook.SheetNames[0];
          const sheetData = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
        
          // Update the mapping to use the correct column headers
          this.staffs = sheetData.map((row: any) => ({
            id: row['ID'],  // Assuming the ID column is present in the Excel file
            name: row['Name'],
            email: row['Email'],
            position: row['Position'],
            department: row['Department'],
            company: row['Company']
          }));
        
          this.filteredStaffs = [...this.staffs];  // Initialize filtered staff
          this.fileUploaded = true;
          this.updateCurrentPageStaffs();
        };

        reader.readAsBinaryString(selectedFile);
      } else {
        this.fileError = 'Only Excel files are allowed.';
        this.file = null;
      }
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const selectedFile = event.dataTransfer.files[0];

      if (this.validateFile(selectedFile)) {
        this.file = selectedFile;
        this.fileError = null;

        const reader = new FileReader();

        reader.onload = (e: ProgressEvent<FileReader>) => {
          const binaryData = e.target?.result as string;
          const workbook = XLSX.read(binaryData, { type: 'binary' });
        
          // Assuming the staff data is in the first sheet
          const sheetName = workbook.SheetNames[0];
          const sheetData = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
        
          // Update the mapping to use the correct column headers
          this.staffs = sheetData.map((row: any) => ({
            id: row['ID'],  // Assuming the ID column is present in the Excel file
            name: row['Name'],
            email: row['Email'],
            position: row['Position'],
            department: row['Department'],
            company: row['Company']
          }));
        
          this.filteredStaffs = [...this.staffs];  // Initialize filtered staff
          this.fileUploaded = true;
          this.updateCurrentPageStaffs();
        };

        reader.readAsBinaryString(selectedFile);
      } else {
        this.fileError = 'Only Excel files are allowed.';
        this.file = null;
      }
    }
  }

  validateFile(file: File): boolean {
    const allowedTypes = [
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.ms-excel'
    ];
    return allowedTypes.includes(file.type);
  }
  updateCurrentPageStaffs(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.currentPageStaffs = this.filteredStaffs.slice(startIndex, endIndex);
    console.log('currentPageStaffs:', this.currentPageStaffs);
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.updateCurrentPageStaffs();
  }

  filterStaff(): void {
    const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
    this.filteredStaffs = this.staffs.filter(staff =>
      staff.name.toLowerCase().includes(lowerCaseSearchTerm) ||
      staff.email.toLowerCase().includes(lowerCaseSearchTerm) ||
      staff.position.toLowerCase().includes(lowerCaseSearchTerm) ||
      staff.department.toLowerCase().includes(lowerCaseSearchTerm) ||
      staff.company.toLowerCase().includes(lowerCaseSearchTerm)
    );
    this.currentPage = 1; // Reset to the first page
    this.updateCurrentPageStaffs();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const label = event.target as HTMLElement;
    label.classList.remove('bg-gray-300'); // Remove the feedback when dragging ends
  }

  onSubmit(): void {
    if (this.file) {
      this.excelImportService.uploadExcelFile(this.file).subscribe(
        () => {
          this.showSuccessToast();
        },
        error => {
          this.handleError(error);
        }
      );
    }
  }

  private handleError(error: any): void {
    let errorMessage = 'An unknown error occurred.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      errorMessage = `Server-side error: ${error.status} ${error.message}`;
    }
    console.error(errorMessage);
    this.fileError = errorMessage;
  }

  reset(): void {
    this.fileUploaded = false;
    // Reset file input element
    const element = document.getElementById('file') as HTMLInputElement;
    if (element) {
      element.value = ''; // Clear the file input
    }
    // Optionally clear other states
    this.file = null;
    this.fileError = null;
    this.staffs = [];
    this.filteredStaffs = [];
    this.currentPage = 1;
    this.updateCurrentPageStaffs();
  }

  onDeleteConfirmed() {
    if (this.file) {
      this.reset();
    }
  }

  openDeleteModal() {
    this.modal.open();
  }

  showSuccessToast() {
    this.toastService.showToast('Add Group successful!', 'success');
  }

  showErrorToast() {
    this.toastService.showToast('An error occurred!', 'error');
  }

  showInfoToast() {
    this.toastService.showToast('Here is some information.', 'info');
  }
}
