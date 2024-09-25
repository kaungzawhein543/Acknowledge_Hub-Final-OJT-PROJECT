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
import { AddAnnouncementComponent } from './announcement/add-announcement/add-announcement.component';
import { RequestAnnouncementComponent } from './announcement/request-announcement/request-announcement.component';
import { ListAnnouncementComponent } from './announcement/list-announcement/list-announcement.component';
import { ProfileComponent } from './user/profile/profile.component';
import { roleBaseRedirectGuard } from './guard/role-base-redirect.guard';
import { DetailAnnouncementComponent } from './announcement/detail-announcement/detail-announcement.component';
import { NotedComponent } from './user/noted/noted.component';
import { UserAnnouncementListComponent } from './user/user-announcement-list/user-announcement-list.component';
import { RequestListByUserComponent } from './announcement/request-list-by-user/request-list-by-user.component';
import { RequestListComponent } from './announcement/request-list/request-list.component';
import { AddHRMainComponent } from './user/add-hr-main/add-hr-main.component';
import { AddPositionComponent } from './position/add-position/add-position.component';
import { PositionListComponent } from './position/position-list/position-list.component';

const routes: Routes = [
  { path: 'acknowledgeHub/login', component: LoginComponent },
  { path: 'acknowledgeHub/change-password/:staffId', component: ChangepasswordComponent },
  { path: 'acknowledgeHub/import-excel', component: ExcelImportComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], position: ['Human Resource(Main)'] } },
  { path: 'acknowledgeHub/404', component: Page404Component },
  { path: 'acknowledgeHub/profile', component: ProfileComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER', 'ADMIN'] } },
  { path: 'acknowledgeHub/otp-input', component: OtpInputComponent },
  { path: 'acknowledgeHub/noted', component: NotedComponent },
  { path: 'acknowledgeHub/otp-request', component: OtpRequestComponent },
  { path: 'acknowledgeHub/add-password', component: AddPasswordComponent },
  // { path: 'admindashboard', component: AdminDashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'acknowledgeHub/system-dashboard', component: HRdashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER', 'ADMIN'], positions: ['Human Resource(Main)'] } },
  { path: 'acknowledgeHub/staff-dashboard', component: DashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['Human Resource(Main)'] } },
  { path: 'acknowledgeHub/add-category', component: AddCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'acknowledgeHub/list-category', component: ListCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'acknowledgeHub/update-category/:id', component: UpdateCategoryComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: 'acknowledgeHub/add-hr-main', component: AddHRMainComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } },
  { path: '', canActivate: [roleBaseRedirectGuard], children: [] },

  {
    path: 'acknowledgeHub/company', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] }, children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'add', component: AddCompanyComponent },
      { path: 'list', component: ListCompaniesComponent },
    ]
  },
  {
    path: 'acknowledgeHub/department', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] }, children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'add', component: AddDepartmentComponent },
      { path: 'list', component: ListDepartmentsComponent }
    ]
  },
  {
    path: 'acknowledgeHub/position', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] }, children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'add', component: AddPositionComponent },
      { path: 'list', component: PositionListComponent }
    ]
  },
  {
    path: 'acknowledgeHub/group', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] }, children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'add', component: AddGroupComponent },
      { path: 'list', component: ListGroupComponent }
    ]
  },
  {
    path: 'acknowledgeHub/announcement', canActivate: [AuthGuard], children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'detail/:id', component: DetailAnnouncementComponent, canActivate: [AuthGuard] },
      { path: 'list', component: ListAnnouncementComponent, canActivate: [AuthGuard] },
      { path: 'request', component: RequestAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], positions: ['Human Resource'] } },
      { path: 'add', component: AddAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },
      { path: 'request-list', component: RequestListComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },
      { path: 'update/:id', component: UpdateAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },
      { path: 'notNoted-announceemnt/:id/:status/:name/:file', component: NotNotedAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },
      { path: 'noted-announcement/:id/:name/:file', component: NotedAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },
      { path: 'staff-noted', component: UserNotedComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'] } },
      { path: 'staff-unnoted', component: UserUnnotedComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['Human Resource(Main)'] } },
      { path: 'list-by-staff', component: UserAnnouncementListComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['USER'], excludedRoles: ['ADMIN'], excludedPositions: ['Human Resource(Main)'] } },
      { path: 'pending-announcement', component: PendingAnnouncementComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] } },

    ]
  },
  // { 
  //   path: '', 
  //   canActivate: [roleBaseRedirectGuard], 
  //   children: [] 
  // },
  {
    path: 'acknowledgeHub/users', canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN', 'USER'], positions: ['Human Resource(Main)'] }, children: [
      { path: '', redirectTo: '/acknowledgeHub/404', pathMatch: 'full' },
      { path: 'list', component: ListUserComponent },
      { path: 'add', component: AddUserComponent },
      { path: 'requested', component: RequestListByUserComponent },
    ]
  },

  { path: '**', redirectTo: '/acknowledgeHub/404' }

];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})



export class AppRoutingModule { }
