import { ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

import { AddAnnouncementComponent } from '../add-announcement/add-announcement.component';
import { AnnouncementService } from '../../services/announcement.service';
import { GroupService } from '../../services/group.service';
import { CategoryService } from '../../services/category.service';
import { StaffService } from '../../services/staff.service';
import { AuthService } from '../../services/auth.service';
import { Group } from '../../models/Group';
import { Staff } from '../../models/staff';
import { announcement } from '../../models/announcement';
import { Position } from '../../models/Position';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-request-announcement',
  templateUrl: './request-announcement.component.html',
  styleUrls: ['./request-announcement.component.css'],
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ]),
        ]),
      ]),
    ]),
  ],
})
export class RequestAnnouncementComponent {

  @ViewChild('staffContainer') staffContainer!: ElementRef; // Reference to the scrollable container

  groups: Group[] = [];
  staffs: Staff[] = [];
  selectedOption: string = 'group'; // Default to group
  selectedGroups: Group[] = [];
  selectedStaffs: Staff[] = [];
  announcementTitle: string = '';
  announcementDescription: string = '';
  scheduleDate: Date | null = null;
  minDateTime: string ='';
  dateError : string = '';
  categories: { id: number, name: string, description: string }[] = [];
  selectedCategory: { id: number, name: string, description: string } | null = null;
  fileSelected = false;
  fileName = '';
  groupotion: boolean = true;
  staffoption: boolean = false;
  selectedOptionsBox: boolean = false;
  optionStaffOfGroup: string = "Groups";
  isScrolledDown = false;
  announcement !: announcement;
  selectedFile: File | null = null;
  createStaffId !: number;
  currentHumanResourceCompany !: string;
  updateInterval: any;
  intervalId: any;
  filteredGroups : Group[] = [];
  fileErrorMessage !: boolean;
  fileErrorText : string = '';

  private page = 0;
  private pageSize = 20;
  public isLoading = false;
  private hasMore = true;
  searchTerm: string = ''; // Search term for filtering
  selectAllStaffs : boolean = false;
  titleError: boolean = false;
  descriptionError: boolean = false;
  formSubmitted: boolean = false;

  constructor(
    private groupService: GroupService,
    private categoryService: CategoryService,
    private staffService: StaffService,
    public announcementService: AnnouncementService,
    private authService: AuthService,
    private cdr : ChangeDetectorRef,
    private toastService : ToastService
  ) { }

  ngOnInit(): void {
    this.loadCategories();
    this.loadStaffs();
    this.authService.getUserInfo().subscribe(
      data => {
        this.currentHumanResourceCompany = data.company;
        this.createStaffId = data.user.id;
        this.loadGroups(this.createStaffId);
      }
    )
    this.setMinDateTime();
    this.intervalId = setInterval(() => {
        this.setMinDateTime();
      }, 60000);
  }

  loadGroups(HumanResourceId: number) {
    this.groupService.getGroupsByHR(HumanResourceId).subscribe(
      (groups: Group[]) => {
        this.groups = Array.isArray(groups) ? groups : JSON.parse(groups);
        this.filteredGroups = [...this.groups];
        console.log('Loaded groups:', this.groups);  // Check if groups are loaded
      },
      error => {
        console.error('Error fetching groups:', error);
      }
    );
  }

  loadCategories() {
    this.categoryService.getAll().subscribe(
      (categories: { id: number, name: string, description: string }[]) => {
        this.categories = Array.isArray(categories) ? categories : JSON.parse(categories);
        this.selectedCategory = this.categories[0] || null;
      },
      error => {
        console.error('Error fetching categories:', error);
      }
    );
  }

  loadStaffs(): void {
    if (this.isLoading || !this.hasMore) {
      return;
    }
    this.isLoading = true;
    var loggedInStaffId = 0;
    this.authService.getUserInfo().subscribe(
      data =>{
        loggedInStaffId = data.user.id;
      }
    ); // Replace with actual way to get logged-in staff ID
    const query = this.searchTerm.trim();
    this.staffService.getStaffs(this.page, this.pageSize, query).subscribe(
      response => {
        this.isLoading = false;
        console.log(response)
        if (response && response.data && response.data.content && Array.isArray(response.data.content)) {
          const processedStaffs = response.data.content
            .filter((staff: { company?: { name?: string }; }) => {
              const companyName = staff.company?.name;
              const matchesCompany = companyName === this.currentHumanResourceCompany;
              return matchesCompany;
            })
            .filter((staff: { id: number }) => {
              console.log(`Checking staff with ID ${staff.id}`); // Log each staff's ID
              return staff.id !== loggedInStaffId; // Filter out logged-in staff
            })
            .map((staff: { position: Position; }) => {
              return {
                ...staff,
                position:  staff.position.name
              };
            });

          this.staffs = [...this.staffs, ...processedStaffs];
          this.page++;
          this.hasMore = this.page < response.data.page.totalPages;
          this.staffs.forEach(staff => {
            staff.selected = this.selectedStaffs.some(selected => selected.id === staff.id);
          });
          console.log(this.staffs)
        } else {
          console.log('No valid content found.');
          this.hasMore = false;
        }
      },
      error => {
        this.isLoading = false;
        console.error('Error fetching staff:', error);
      }
    );
  }

  groupInputChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm = inputElement.value.trim();
    this.filterGroups();
  }

  filterGroups(): void {
    if (this.searchTerm) {
      this.filteredGroups = this.groups.filter(group =>
        group.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    } else {
      this.filteredGroups = [...this.groups];
    }
    this.filteredGroups.forEach(group => {
      group.selected = this.selectedGroups.some(selectedGroup => selectedGroup.id === group.id);
    });
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    const scrollPosition = target.scrollHeight - target.scrollTop;
    const threshold = target.clientHeight + 100; // Adjust as needed

    if (scrollPosition <= threshold) {
      this.loadStaffs();
    }
  }

  onSubmit(): void {
    const formData = new FormData();
    const trimmedTitle = this.announcementTitle ? this.announcementTitle.trim() : '';
    const trimmedDescription = this.announcementDescription ? this.announcementDescription.trim() : '';
    if (this.scheduleDate && this.minDateTime && new Date(this.scheduleDate).getTime() < new Date(this.minDateTime).getTime()) {
      this.dateError = 'The schedule date cannot be late than the current date & time.';
      return;
    }
    if (trimmedTitle === '' && trimmedDescription === '' && !this.selectedFile) {
      this.titleError = true;
      this.descriptionError = true;
      this.fileErrorMessage = true;
      return;
    } else if (trimmedTitle === '' && trimmedDescription === '') {
      this.titleError = true;
      this.descriptionError = true;
      return;
    } else if (trimmedTitle === '' && !this.selectedFile) {
      this.titleError = true;
      this.fileErrorMessage = true;
      return;
    } else if (trimmedDescription === '' && !this.selectedFile) {
      this.descriptionError = true;
      this.fileErrorMessage = true;
      return;
    }
    else if (trimmedDescription === '') {
      this.descriptionError = true;
      return;
    } else if (trimmedTitle === '') {
      this.titleError = true;
      return;
    } else if (!this.selectedFile) {
      this.fileErrorMessage = true;
      return;
    } else {
      // If all checks are passed, append the file
      formData.append('files', this.selectedFile);
    }
    // Create the announcement object
    const announcement = {
      title: this.announcementTitle,
      description: this.announcementDescription,
      groupStatus: this.selectedOption === "staff" ? 0 : 1,
      scheduleAt: this.scheduleDate,
      category : this.selectedCategory,
      forRequest : 1
    };

    // Append the announcement DTO as a JSON string with appropriate MIME type
    formData.append('request', new Blob([JSON.stringify(announcement)], { type: 'application/json' }));

    // Append user IDs if any
    if (this.selectedStaffs && this.selectedStaffs.length) {
      const userIds = this.selectedStaffs.map(staff => staff.id);
      formData.append('userIds', new Blob([JSON.stringify(userIds)], { type: 'application/json' }));
    }

    // Append group IDs if any
    if (this.selectedGroups && this.selectedGroups.length) {
      const groupIds = this.selectedGroups.map(group => group.id);
      formData.append('groupIds', new Blob([JSON.stringify(groupIds)], { type: 'application/json' }));
    }

    // Append the selected file if any
    if (this.selectedFile) {
      formData.append('files', this.selectedFile);
    }

    // Call the service to create the announcement
    this.announcementService.createAnnouncement(formData, this.createStaffId).subscribe(
      response => {
        this.showSuccessToast();
      },
      error => {
        console.error(error);
      }
    );
  }



  onOptionChange(option: string): void {
    this.selectedOption = option;
    if (option === 'group') {
      this.groupotion = true;
      this.staffoption = false;
      this.optionStaffOfGroup = "Groups";
      this.selectedStaffs = [];
    } else {
      this.staffoption = true;
      this.groupotion = false;
      this.optionStaffOfGroup = "Staffs";
      this.selectedGroups = [];
      this.searchTerm = '';
      this.filterStaffs();
    }
  }

  onGroupChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const selectedGroupId = Number(target.value);
    const selectedGroup = this.groups.find(group => group.id === selectedGroupId);
    if (selectedGroup) {
      if (target.checked) {
        if (!this.selectedGroups.some(group => group.id === selectedGroupId)) {
          this.selectedGroups.unshift(selectedGroup);
        }
      } else {
        this.selectedGroups = this.selectedGroups.filter(selected => selected.id !== selectedGroupId);
      }
    } else {
      console.warn(`group with ID ${selectedGroup} not found in the group list.`);
    }
  }

  onStaffChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const selectedStaffId = target.value; // Get the selected staffId

    const selectedStaff = this.staffs.find(staff => staff.staffId === selectedStaffId);

    if (selectedStaff) {
      if (target.checked) {
        if (!this.selectedStaffs.some(staff => staff.staffId === selectedStaffId)) {
          this.selectedStaffs.unshift(selectedStaff);
        }
      } else {
        this.selectedStaffs = this.selectedStaffs.filter(staff => staff.staffId !== selectedStaffId);
      }
    } else {
      console.warn(`Staff with ID ${selectedStaffId} not found in the staff list.`);
    }
  }


  onFileChange(event: any): void {
    const input = event.target as HTMLInputElement;
    const maxSize = 2 * 1024 * 1024;
  
    const allowedFormats = [
      'application/msword',                    // .doc
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
      'application/vnd.ms-excel',              // .xls
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
      'application/pdf',                       // .pdf
      'application/zip',                       // .zip
      'application/x-rar-compressed'           // .rar
    ];

    // Check file format (MIME type)
    if (!allowedFormats.includes(event.target.files[0].type)) {
      this.fileErrorText='Invalid file format. Only Word, Excel, PDF, ZIP, and RAR files are allowed.';
      this.fileErrorMessage = true;
      return;
    }

    const file: File = event.target.files[0];
    if (file.size > maxSize) {
      this.fileErrorText = 'File size exceeds 2MB';
      this.fileErrorMessage = true;
      return;
    }else{
      this.fileErrorMessage = false;
    }

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.fileName = this.selectedFile.name;
      this.fileSelected = true;
    } else {
      this.selectedFile = null;
      this.fileName = '';
      this.fileSelected = false;
    }
  }

  onInputChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm = inputElement.value.trim();

    if (this.searchTerm) {
      this.filterStaffs();
    }else{
      this.resetStaffList();
    }
  }

  filterStaffs(): void {
    this.page = 0; // Reset pagination
    this.hasMore = true;
    this.staffs = [];
    this.loadStaffs(); // Reload staff with the search term
  }

  resetStaffList(): void {
    this.page = 0; // Reset pagination
    this.hasMore = true;
    this.staffs = []; // Clear current staff list
    this.searchTerm = ''; // Clear search term
    this.loadStaffs(); // Load all staff without any filtering
  }

  showSelectedOptionBox(): void {
    if (this.selectedOptionsBox === false) {
      this.selectedOptionsBox = true;
    } else {
      this.selectedOptionsBox = false;
    }
  }
  
  setMinDateTime(): void {
    const now = new Date();
  
    // Adjust the time to 2 minutes before the current time
    now.setMinutes(now.getMinutes() - 2);
  
    const year = now.getFullYear();
    const month = (now.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
    const day = now.getDate().toString().padStart(2, '0');
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
  
    // Set the minimum datetime to 2 minutes before the current date and time
    this.minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
    console.log('MinDateTime set to:', this.minDateTime);
  }

  onSelectAllStaffs(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.selectAllStaffs = target.checked;

    this.staffs.forEach(staff => {
      staff.selected = this.selectAllStaffs;

      const mockEvent = {
        target: {
          value: staff.staffId,
          checked: this.selectAllStaffs
        }
      };

      this.onStaffChange(mockEvent as any); // Casting to 'any' to bypass TypeScript checks
    });
    this.cdr.detectChanges();
  }
  onDateChange() {
    if (this.scheduleDate) {
      const selectedDate = new Date(this.scheduleDate); // Convert the input to a Date object
      const now = new Date(); // Get the current time
      
      // Add 3 minutes to the current time
      const minDate = new Date(now.getTime() + 3 * 60 * 1000); // Current time + 3 minutes
      
      // Compare the schedule date with the current time + 3 minutes
      if (selectedDate < minDate) {
        this.dateError = 'The schedule date should be at least 3 minutes later than the current time!'; 
      } else {
        this.dateError = ""; // No error
      }
    } else {
      this.dateError = ""; // Reset error if no date is selected
    }
  }
  showSuccessToast() {
    this.toastService.showToast(' Announcement requested successful!', 'success');
  }
  onCreate(){
    this.formSubmitted = true;  
  }
}
