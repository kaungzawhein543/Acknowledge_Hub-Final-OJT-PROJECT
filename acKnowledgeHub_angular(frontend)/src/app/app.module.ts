import { NgModule } from '@angular/core';
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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MychartComponent } from './chart/mychart/mychart.component';
import { ChangepasswordComponent } from './user/changepassword/changepassword.component';
import { ExcelImportComponent } from './excel-import/excel-import.component';
import { LoginComponent } from './user/login/login.component';
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
import { OtpInputComponent } from './user/otp-input/otp-input.component';
import { OtpRequestComponent } from './user/otp-request/otp-request.component';
import { HideEmailPipe } from './pipe/hide-email.pipe';
import { UserChartComponent } from './chart/user-chart/user-chart.component';
import { ProfileComponent } from './user/profile/profile.component';

@NgModule({
  declarations: [
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
    LoginComponent,
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
    OtpInputComponent,
    OtpRequestComponent,
    HideEmailPipe,
    UserChartComponent,
    ProfileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
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
    MatListOption
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    AuthGuard,
    RoleGuard,
    provideAnimationsAsync(),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
