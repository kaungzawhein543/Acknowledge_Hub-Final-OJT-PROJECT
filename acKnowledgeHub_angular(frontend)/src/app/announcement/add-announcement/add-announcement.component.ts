import { Component, OnInit } from '@angular/core';
import { Group } from '../../models/Group';
import { Staff } from '../../models/Staff';
import { ExampleDataServiceService } from '../../services/example-data-service.service';

@Component({
  selector: 'app-add-announcement',
  templateUrl: './add-announcement.component.html',
  styleUrl: './add-announcement.component.css'
})
export class AddAnnouncementComponent implements OnInit{
  groups: Group[] = [];
  staffs: Staff[] = [];
  selectedOption: string = 'group'; // Default to group
  selectedGroups: Group[] = [];
  selectedStaffs: Staff[] = [];
  announcementTitle: string = '';
  announcementDescription: string = '';
  scheduleDate: Date | null = null;
  categories: { id: number, name: string }[] = [
    { id: 1, name: 'General' },
    { id: 2, name: 'Urgent' },
    { id: 3, name: 'Reminder' }
  ];
  selectedCategory: number | null = null;

  constructor(private exampleDataService: ExampleDataServiceService) {}

  ngOnInit(): void {
    this.exampleDataService.getGroups().subscribe(groups => this.groups = groups);
    this.exampleDataService.getStaffs().subscribe(staffs => this.staffs = staffs);
  }

  onSubmit(): void {
    // Handle form submission
    console.log({
      title: this.announcementTitle,
      description: this.announcementDescription,
      option: this.selectedOption,
      groups: this.selectedGroups,
      staff: this.selectedStaffs,
      scheduleDate: this.scheduleDate,
      category: this.selectedCategory
    });
  }

  onOptionChange(option: string): void {
    this.selectedOption = option;
    if (option === 'group') {
      this.selectedStaffs = [];
    } else {
      this.selectedGroups = [];
    }
  }

  onGroupChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    if (target) {
      const selectedOptions = Array.from(target.selectedOptions);
      this.selectedGroups = selectedOptions.map(option => {
        const id = +option.value;
        return this.groups.find(group => group.id === id)!;
      });
    }
  }

  onStaffChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    if (target) {
      const selectedOptions = Array.from(target.selectedOptions);
      this.selectedStaffs = selectedOptions.map(option => {
        const id = +option.value;
        return this.staffs.find(staff => staff.id === id)!;
      });
    }
  }
}
