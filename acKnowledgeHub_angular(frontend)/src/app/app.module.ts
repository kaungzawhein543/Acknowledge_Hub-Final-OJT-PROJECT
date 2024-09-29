import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { ListCategoryComponent } from './category/list-category/list-category.component';
import { UpdateCategoryComponent } from './category/update-category/update-category.component';
import { AnnouncementComponent } from './announcement/announcement/announcement.component';
import { UpdateAnnouncementComponent } from './announcement/update-announcement/update-announcement.component';
import { AddCategoryComponent } from './category/add-category/add-category.component';
import { SidebarComponent } from './sidebar/sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MychartComponent } from './chart/mychart/mychart.component';
import { ChangepasswordComponent } from './user/changepassword/changepassword.component';
import { ExcelImportComponent } from './excel-import/excel-import.component';
import { AuthGuard } from './guard/auth.guard';
import { RoleGuard } from './guard/role.guard';
import { AddAnnouncementComponent } from './announcement/add-announcement/add-announcement.component';
import { ListAnnouncementComponent } from './announcement/list-announcement/list-announcement.component';
import { AddGroupComponent } from './group/add-group/add-group.component';
import { ListGroupComponent } from './group/list-group/list-group.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { HRdashboardComponent } from './hrdashboard/hrdashboard.component';
import { Page404Component } from './page404/page404.component';
import { NotedAnnouncementComponent } from './announcement/noted-announcement/noted-announcement.component';
import { NotNotedAnnouncementComponent } from './announcement/not-noted-announcement/not-noted-announcement.component';
import { PendingAnnouncementComponent } from './announcement/pending-announcement/pending-announcement.component';
import { ConfirmationModalComponent } from './confirmation-modal/confirmation-modal.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MatButtonModule } from '@angular/material/button';
import { MatOption, MatPseudoCheckboxModule } from '@angular/material/core';
import { RouterModule } from '@angular/router';
import { MtxGridModule } from '@ng-matero/extensions/grid';
import { MatRadioModule } from '@angular/material/radio';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatListModule, MatListOption } from '@angular/material/list';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { AddPasswordComponent } from './user/add-password/add-password.component';
import { HideEmailPipe } from './pipe/hide-email.pipe';
import { AddUserComponent } from './user/add-user/add-user.component';
import { UserNotedComponent } from './user/user-noted/user-noted.component';
import { UserUnnotedComponent } from './user/user-unnoted/user-unnoted.component';
import { AddCompanyComponent } from './company/add-company/add-company.component';
import { AddDepartmentComponent } from './department/add-department/add-department.component';
import { AddPositionComponent } from './position/add-position/add-position.component';
import { FeedbackListComponent } from './announcement/feedback-list/feedback-list.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RequestAnnouncementComponent } from './announcement/request-announcement/request-announcement.component';
import { ListUserComponent } from './user/list-user/list-user.component';
import { ListCompaniesComponent } from './company/list-companies/list-companies.component';
import { ListDepartmentsComponent } from './department/list-departments/list-departments.component';
import { OtpInputComponent } from './user/otp-input/otp-input.component';
import { OtpRequestComponent } from './user/otp-request/otp-request.component';
import { LoginComponent } from './user/login/login.component';
import { AddHRMainComponent } from './user/add-hr-main/add-hr-main.component';
import { RequestListComponent } from './announcement/request-list/request-list.component';
import { NotedComponent } from './user/noted/noted.component';

import { UserChartComponent } from './chart/user-chart/user-chart.component';
import { ProfileComponent } from './user/profile/profile.component';
import { StaffChartComponent } from './chart/staff-chart/staff-chart.component';


import { LoaderComponent } from './loader/loader/loader.component';
import { DetailAnnouncementComponent } from './announcement/detail-announcement/detail-announcement.component';
import { CommonModule } from '@angular/common';
import { LoadingService } from './services/loading.service';
import { TimeAgoPipe } from './time-ago.pipe';
import { UserAnnouncementListComponent } from './user/user-announcement-list/user-announcement-list.component';
import { RequestListByUserComponent } from './announcement/request-list-by-user/request-list-by-user.component';
import { PositionListComponent } from './position/position-list/position-list.component';
import { CountdownComponent } from './countdown/countdown.component';
import { UpdateCompanyComponent } from './company/update-company/update-company.component';
import { UpdateDepartmentComponent } from './department/update-department/update-department.component';
import { UpdatePositionComponent } from './position/update-position/update-position.component';

@NgModule({
  declarations: [
    LoginComponent,
    AppComponent,
    AnnouncementComponent,
    UpdateAnnouncementComponent,
    AddCategoryComponent,
    UpdateCategoryComponent,
    ListCategoryComponent,
    SidebarComponent,
    NavbarComponent,
    DashboardComponent,
    MychartComponent,
    ChangepasswordComponent,
    ExcelImportComponent,
    AddAnnouncementComponent,
    ListAnnouncementComponent,
    AddGroupComponent,
    ListGroupComponent,
    AdminDashboardComponent,
    HRdashboardComponent,
    Page404Component,
    NotedAnnouncementComponent,
    NotNotedAnnouncementComponent,
    PendingAnnouncementComponent,
    ConfirmationModalComponent,
    AddPasswordComponent,
    HideEmailPipe,
    AddUserComponent,
    UserNotedComponent,
    UserUnnotedComponent,
    AddCompanyComponent,
    AddDepartmentComponent,
    AddPositionComponent,
    FeedbackListComponent,
    RequestAnnouncementComponent,
    ListUserComponent,
    ListCompaniesComponent,
    ListDepartmentsComponent,
    OtpInputComponent,
    OtpRequestComponent,
    AddHRMainComponent,
    RequestListComponent,
    NotedComponent,
    UserChartComponent,
    ProfileComponent,
    StaffChartComponent,
    LoaderComponent,
    DetailAnnouncementComponent,
    TimeAgoPipe,
    UserAnnouncementListComponent,
    RequestListByUserComponent,
    PositionListComponent,
    CountdownComponent,
    UpdateCompanyComponent,
    UpdateDepartmentComponent,
    UpdatePositionComponent
  ],
  imports: [
    BrowserAnimationsModule, // required for toastr
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatPseudoCheckboxModule,
    MatRadioModule,
    MtxGridModule,
    RouterModule,
    MatCheckboxModule,
    MatListModule,
    MatOption,
    MatButtonToggleModule,
    ScrollingModule,
    MatListOption,
    MatButtonModule,
    MatRadioModule,
    MtxGridModule,
    RouterModule,
    MatMenuModule,
    ScrollingModule,
    MatTableModule,
    MatPaginatorModule,
    BrowserAnimationsModule,
    BrowserModule,
    CommonModule,
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    AuthGuard,
    RoleGuard,
    provideAnimationsAsync(),
    LoadingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
