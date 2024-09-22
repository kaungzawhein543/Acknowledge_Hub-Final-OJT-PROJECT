import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Group } from '../../models/Group';
import { Staff } from '../../models/staff';
import { GroupService } from '../../services/group.service';
import { CategoryService } from '../../services/category.service';
import { StaffService } from '../../services/staff.service';
import { AnnouncementService } from '../../services/announcement.service';
import { announcement } from '../../models/announcement';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { Category } from '../../models/category';
import { toArray } from 'rxjs';

@Component({
  selector: 'app-update-announcement',
  templateUrl: './update-announcement.component.html',
  styleUrl: './update-announcement.component.css'
})
export class UpdateAnnouncementComponent implements OnInit {
  @ViewChild('staffContainer') staffContainer!: ElementRef; // Reference to the scrollable container

  groups: Group[] = [];
  staffs: Staff[] = [];
  selectedOption: string = 'group'; // Default to group
  selectedGroups: Group[] = [];
  selectedStaffs: Staff[] = [];
  announcementTitle: string = '';
  announcementDescription: string = '';
  scheduleDate: Date | null = null;
  minDateTime: string = '';
  dateError: string = '';
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
  filteredGroups: Group[] = [];
  showPreviousVersion: boolean = false;
  announcementVersions: string[] = [];
  public downloadUrl: string = 'https://res.cloudinary.com/djqxznfjm/raw/upload/v1725614114/';

  private page = 0;
  private pageSize = 20;
  public isLoading = false;
  private hasMore = true;
  searchTerm: string = ''; // Search term for filtering
  announcementId: string | null = '';
  intervalId: any;

  constructor(
    private groupService: GroupService,
    private categoryService: CategoryService,
    private staffService: StaffService,
    public announcementService: AnnouncementService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.announcementId = this.route.snapshot.paramMap.get('id');
    this.loadGroups();
    this.loadCategories();
    this.loadStaffs();
    this.setMinDateTime();
    this.intervalId = setInterval(() => {
      this.setMinDateTime();
    }, 60000);
    this.authService.getUserInfo().subscribe(
      data => {
        this.createStaffId = data.user.id;
        this.announcementService.getLatestAnnouncementById(Number(this.announcementId)).subscribe(
          data => {
            this.announcementService.getAnnouncementVersion("Announce" + this.announcementId).subscribe(
              versions => {
                console.log(versions)
                this.announcementVersions = versions;
              }
            )
            this.announcementTitle = data.title;
            this.announcementDescription = data.description;
            const selectedCategory = this.categories.find(category => category.id === data.category.id);
            if (selectedCategory) {
              this.selectedCategory = selectedCategory;
            } else {
              console.log('Category not found');
            }

            if (data['group'].length && data['group'].length > 0) {
              data['group'].forEach((groupId: number) => {
                // Find the matching group in filterGroups
                const matchedGroup = this.filteredGroups.find((group: Group) => group.id === groupId);

                if (matchedGroup) {
                  // Add to selectedGroups if not already added
                  this.selectedGroups.push(matchedGroup);

                  // Set the 'selected' field to true
                  matchedGroup.selected = true;
                }
              });


              this.onOptionChange('group')
            } else {
              this.optionStaffOfGroup = "Staffs";
              this.selectedStaffs = data['staff'];
              this.onOptionChange('staff');
            }
          }
        )
      }
    )
  }
  onStaffSelectionChange(staff: any, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const isChecked = inputElement.checked;

    if (isChecked) {
      if (!this.selectedStaffs.some(s => s.id === staff.id)) {
        this.selectedStaffs.push(staff);
      }
    } else {
      this.selectedStaffs = this.selectedStaffs.filter(s => s.id !== staff.id);
    }
  }


  loadGroups() {
    this.groupService.getAllGroups().subscribe(
      (groups: Group[]) => {
        this.groups = Array.isArray(groups) ? groups : JSON.parse(groups);
        this.filteredGroups = [...this.groups];
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
    if (this.isLoading || !this.hasMore) return;

    this.isLoading = true;

    this.staffService.getStaffs(this.page, this.pageSize, this.searchTerm).subscribe(
      response => {
        this.isLoading = false;

        if (response && response.data && response.data.content && Array.isArray(response.data.content)) {
          const processedStaffs = response.data.content.map((staff: { position: string; }) => ({
            ...staff,
            position: this.extractPositionName(staff.position) // Extract only the name
          }));

          this.staffs = [...this.staffs, ...processedStaffs];
          this.page++;
          this.hasMore = this.page < response.data.page.totalPages;
          this.staffs.forEach(staff => {
            staff.selected = this.selectedStaffs.some(selected => selected.id === staff.id);
          });
        } else {
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

    if (this.scheduleDate && this.minDateTime && new Date(this.scheduleDate).getTime() < new Date(this.minDateTime).getTime()) {
      this.dateError = 'The schedule date cannot be earlier than the current date & time.';
      return;
    }
    // Create the announcement object
    const announcement = {
      id: this.announcementId,
      title: this.announcementTitle,
      description: this.announcementDescription,
      groupStatus: this.selectedOption === "staff" ? 0 : 1,
      scheduleAt: this.scheduleDate,
      category: this.selectedCategory
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
        console.log(response);
      },
      error => {
        console.error(error);
      }
    );
  }
  resetStaffList(): void {
    this.page = 0; // Reset pagination
    this.hasMore = true;
    this.staffs = []; // Clear current staff list
    this.searchTerm = ''; // Clear search term
    this.loadStaffs(); // Load all staff without any filtering
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
    console.log("Selected staff ID:", selectedStaffId); // Log the selected staffId

    const selectedStaff = this.staffs.find(staff => staff.staffId === selectedStaffId);

    if (selectedStaff) {
      if (target.checked) {
        if (!this.selectedStaffs.some(staff => staff.staffId === selectedStaffId)) {
          this.selectedStaffs.push(selectedStaff);
        }
      } else {
        this.selectedStaffs = this.selectedStaffs.filter(staff => staff.staffId !== selectedStaffId);
      }
      console.log("Updated selected staffs:", this.selectedStaffs); // Log the updated selected staff array
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
    } else {
      this.resetStaffList();
    }
  }

  groupInputChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm = inputElement.value.trim();
    this.filterGroups();
  }

  filterStaffs(): void {
    this.page = 0; // Reset pagination
    this.hasMore = true;
    this.staffs = []; // Clear current staff list
    console.log("This is A")
    this.loadStaffs(); // Reload staff with the search term
  }

  showSelectedOptionBox(): void {
    if (this.selectedOptionsBox === false) {
      this.selectedOptionsBox = true;
    } else {
      this.selectedOptionsBox = false;
    }
  }
  showPreviousVersionBox(): void {
    if (this.showPreviousVersion === false) {
      this.showPreviousVersion = true;
    } else {
      this.showPreviousVersion = false;
    }
  }

  closeModal(): void {
    this.showPreviousVersion = false;
  }

  downloadFile(version: string): void {
    this.announcementService.downloadFile(version);
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
  }

}