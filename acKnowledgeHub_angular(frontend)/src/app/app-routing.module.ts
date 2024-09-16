import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddCategoryComponent } from './category/add-category/add-category.component';
import { ListCategoryComponent } from './category/list-category/list-category.component';
import { UpdateCategoryComponent } from './category/update-category/update-category.component';
import { UpdateAnnouncementComponent } from './announcement/update-announcement/update-announcement.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './user/login/login.component';
import { ChangepasswordComponent } from './user/changepassword/changepassword.component';
import { ExcelImportComponent } from './excel-import/excel-import.component';
import { AuthGuard } from './guard/auth.guard';
import { RoleGuard } from './guard/role.guard';
import { AddCompanyComponent } from './company/add-company/add-company.component';
import { ListCompaniesComponent } from './company/list-companies/list-companies.component';
import { AddDepartmentComponent } from './department/add-department/add-department.component';
import { ListDepartmentsComponent } from './department/list-departments/list-departments.component';
import { AddGroupComponent } from './group/add-group/add-group.component';
import { ListGroupComponent } from './group/list-group/list-group.component';
import { HRdashboardComponent } from './hrdashboard/hrdashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { Page404Component } from './page404/page404.component';
import { NotNotedAnnouncementComponent } from './announcement/not-noted-announcement/not-noted-announcement.component';
import { NotedAnnouncementComponent } from './announcement/noted-announcement/noted-announcement.component';
import { PendingAnnouncementComponent } from './announcement/pending-announcement/pending-announcement.component';
import { ListUserComponent } from './user/list-user/list-user.component';
import { OtpInputComponent } from './user/otp-input/otp-input.component';
import { OtpRequestComponent } from './user/otp-request/otp-request.component';
import { AddPasswordComponent } from './user/add-password/add-password.component';
import { AddUserComponent } from './user/add-user/add-user.component';
import { UserNotedComponent } from './user/user-noted/user-noted.component';
import { UserUnnotedComponent } from './user/user-unnoted/user-unnoted.component';
import { AnnouncementComponent } from './announcement/announcement/announcement.component';
import { AddPositionComponent } from './position/add-position/add-position.component';
import { FeedbackListComponent } from './announcement/feedback-list/feedback-list.component';
import { AddAnnouncementComponent } from './announcement/add-announcement/add-announcement.component';
import { RequestAnnouncementComponent } from './announcement/request-announcement/request-announcement.component';
import { ListAnnouncementComponent } from './announcement/list-announcement/list-announcement.component';
import { ProfileComponent } from './user/profile/profile.component';
import { roleBaseRedirectGuard } from './guard/role-base-redirect.guard';
import { DetailAnnouncementComponent } from './announcement/detail-announcement/detail-announcement.component';
import { NotedComponent } from './user/noted/noted.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'change-password/:staffId', component: ChangepasswordComponent },
  { path: 'import-excel', component: ExcelImportComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], position: ['HR_MAIN'] } },
  { path: '404', component: Page404Component },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER', 'ADMIN'] } },
  { path: 'otp-input', component: OtpInputComponent },
  { path: 'noted', component: NotedComponent },
  { path: 'otp-request', component: OtpRequestComponent },
  { path: 'add-password', component: AddPasswordComponent },
  // { path: 'admindashboard', component: AdminDashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'dashboard', component: HRdashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER', 'ADMIN'], positions: ['HR_MAIN'] } },
  { path: 'staff-dashboard', component: DashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['HR_MAIN'] } },
  { path: 'add-category', component: AddCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'list-category', component: ListCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'update-category/:id', component: UpdateCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },

  { path: '', canActivate: [roleBaseRedirectGuard], children: [] },

  {
    path: 'company', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] }, children: [
      { path: 'add', component: AddCompanyComponent },
      { path: 'list', component: ListCompaniesComponent },
    ]
  },
  {
    path: 'department', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] }, children: [
      { path: 'add', component: AddDepartmentComponent },
      { path: 'list', component: ListDepartmentsComponent }
    ]
  },
  {
    path: 'group', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] }, children: [
      { path: 'add', component: AddGroupComponent },
      { path: 'list', component: ListGroupComponent }
    ]
  },
  {
    path: 'announcement', canActivate: [AuthGuard], children: [
      { path: 'detail/:id', component: DetailAnnouncementComponent, canActivate: [AuthGuard] },
      { path: 'list', component: ListAnnouncementComponent, canActivate: [AuthGuard] },
      { path: 'request', component: RequestAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], positions: ['HR'] } },
      { path: 'add', component: AddAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] } },
      { path: 'update/:id', component: UpdateAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] } },
      { path: 'notNoted-announceemnt/:id/:status/:name/:file', component: NotNotedAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'noted-announcement/:id/:name/:file', component: NotedAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'staff-noted/:staffId', component: UserNotedComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'] } },
      { path: 'staff-unnoted', component: UserUnnotedComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['HR_MAIN'] } },
      { path: 'list-by-staff/:staffId', component: UserUnnotedComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['HR_MAIN'] } },
      { path: 'pending-announcement', component: PendingAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] } }
    ]
  },
  {
    path: '',
    canActivate: [roleBaseRedirectGuard],
    children: []
  },
  {
    path: 'users', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['HR_MAIN'] }, children: [
      { path: 'list', component: ListUserComponent },
      { path: 'add', component: AddUserComponent }
    ]
  },

  { path: '**', redirectTo: '/404' }

];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})



export class AppRoutingModule { }
