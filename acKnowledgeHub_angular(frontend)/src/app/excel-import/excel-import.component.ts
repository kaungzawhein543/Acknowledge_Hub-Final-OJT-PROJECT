import { Component } from '@angular/core';

import { ExcelServiceService } from '../services/excel.service';

@Component({
  selector: 'app-excel-import',
  templateUrl: './excel-import.component.html',
  styleUrl: './excel-import.component.css'
})
export class ExcelImportComponent {
  file: File | null = null;
  fileError: string | null = null;

  constructor(private excelImportService: ExcelServiceService) { }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const selectedFile = input.files[0];
      if (this.validateFile(selectedFile)) {
        this.file = selectedFile;
        this.fileError = null;
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

  onSubmit(): void {
    if (this.file) {
      this.excelImportService.uploadExcelFile(this.file).subscribe(
        response => {
          console.log('File uploaded successfully:', response);
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
}
