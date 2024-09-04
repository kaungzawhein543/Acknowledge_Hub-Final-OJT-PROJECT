import { Component, ElementRef, ViewChild } from '@angular/core';
import { AddAnnouncementComponent } from '../add-announcement/add-announcement.component';
import { AnnouncementService } from '../../services/announcement.service';
import { GroupService } from '../../services/group.service';
import { CategoryService } from '../../services/category.service';
import { StaffService } from '../../services/staff.service';
import { AuthService } from '../../services/auth.service';
import { Group } from '../../models/Group';
import { Staff } from '../../models/staff';
import { announcement } from '../../models/announcement';

@Component({
  selector: 'app-request-announcement',
  templateUrl: './request-announcement.component.html',
  styleUrls: ['./request-announcement.component.css']
})
export class RequestAnnouncementComponent{

  @ViewChild('staffContainer') staffContainer!: ElementRef; // Reference to the scrollable container
  
  groups: Group[] = [];
  staffs: Staff[] = [];
  selectedOption: string = 'group'; // Default to group
  selectedGroups: Group[] = [];
  selectedStaffs: Staff[] = [];
  announcementTitle: string = '';
  announcementDescription: string = '';
  scheduleDate: Date | null = null;
  categories: { id: number, name: string, description: string }[] = [];
  selectedCategory: { id: number, name: string, description: string } | null = null;
  fileSelected = false;
  fileName = '';
  groupotion : boolean = true;
  staffoption : boolean = false;
  selectedOptionsBox : boolean = false;
  optionStaffOfGroup : string = "Groups";
  isScrolledDown = false;
  announcement !: announcement;
  selectedFile: File | null = null;
  createStaffId !: number;
  currentHrCompany !: string;

  private page = 0;
  private pageSize = 20;
  public isLoading = false;
  private hasMore = true;
  searchTerm: string = ''; // Search term for filtering

  constructor(
    private groupService: GroupService, 
    private categoryService: CategoryService,
    private staffService: StaffService,
    public announcementService: AnnouncementService,
    private authService : AuthService
  ) {}

  ngOnInit(): void {
    this.loadGroups();
    this.loadCategories();
    this.loadStaffs();
    this.authService.getUserInfo().subscribe(
      data => {
        this.createStaffId = data.user.id;
        this.currentHrCompany = data.company;
      }
    )
  }

  loadGroups() {
    this.groupService.getAllGroups().subscribe(
      (groups: Group[]) => {
        this.groups = Array.isArray(groups) ? groups : JSON.parse(groups);
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
      console.log('Skipping loadStaffs: isLoading=', this.isLoading, ', hasMore=', this.hasMore);
      return;
    }
  
    this.isLoading = true;
  
    const query = this.searchTerm.trim();
    console.log('Searching for term:', query);
  
    this.staffService.getStaffs(this.page, this.pageSize, query).subscribe(
      response => {
        this.isLoading = false;
        console.log('Received response:', response);
  
        if (response && response.data && response.data.content && Array.isArray(response.data.content)) {
          const processedStaffs = response.data.content
            .filter((staff: { company: { name: string; }; }) => {
              const companyName = staff.company.name;
              const matchesCompany = companyName === this.currentHrCompany;
              console.log('Staff company name:', companyName, 'Matches:', matchesCompany);
              return matchesCompany;
            })
            .map((staff: { position: string; }) => {
              const positionName = this.extractPositionName(staff.position);
              console.log('Original position:', staff.position, 'Extracted position name:', positionName);
              return {
                ...staff,
                position: positionName
              };
            });
  
          console.log('Processed staffs:', processedStaffs);
          this.staffs = [...this.staffs, ...processedStaffs];
          this.page++;
          this.hasMore = this.page < response.data.page.totalPages;
          console.log('Updated page:', this.page, 'Has more:', this.hasMore);
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
  
  
  
  
  

  // Helper method to extract the position name
  extractPositionName(position: string): string {
    const match = position.match(/Position\(id=\d+, name=(.*?)\)/);
    return match ? match[1] : position;
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

    // Create the announcement object
    const announcement = {
      title: this.announcementTitle,
      description: this.announcementDescription,
      groupStatus: this.selectedOption === "staff" ? 0 : 1,
      scheduleAt  : this.scheduleDate,
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
    this.announcementService.createAnnouncement(formData,this.createStaffId).subscribe(
      response => {

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
  const target = event.target as HTMLInputElement;
  const selectedStaffId = target.value; // Get the selected staffId
  
  const selectedStaff = this.staffs.find(staff => staff.staffId === selectedStaffId);

  if (selectedStaff) {
    if (target.checked) {
      if (!this.selectedStaffs.some(staff => staff.staffId === selectedStaffId)) {
        this.selectedStaffs.push(selectedStaff);
      }
    } else {
      this.selectedStaffs = this.selectedStaffs.filter(staff => staff.staffId !== selectedStaffId);
    }
  } else {
    console.warn(`Staff with ID ${selectedStaffId} not found in the staff list.`);
  }
}


onFileChange(event: Event): void {
  const input = event.target as HTMLInputElement;
  
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
    }
  }
  
  filterStaffs(): void {
    this.page = 0; // Reset pagination
    this.hasMore = true;
    this.staffs = []; 
    this.loadStaffs(); // Reload staff with the search term
  }
  
  showSelectedOptionBox():void{
    if(this.selectedOptionsBox === false){
      this.selectedOptionsBox = true;
    }else{
      this.selectedOptionsBox = false;
    }
  }
}
