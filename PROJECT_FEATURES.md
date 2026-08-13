# Acknowledge Hub — Project Features and Setup

## 1. Project overview

Acknowledge Hub is an internal company announcement and acknowledgement platform. It allows administrators and Human Resource staff to manage organizational data, publish announcements, track who has acknowledged them, collect feedback, and notify staff in real time.

The repository contains two applications:

- `acKnowledgeHub_angular(frontend)` — Angular 18 web application
- `acKnowledgeHub(backend)` — Spring Boot 3 REST API

## 2. Technology stack

### Frontend

- Angular 18 and TypeScript
- Angular Router, Forms, Reactive Forms, CDK, and Material
- Tailwind CSS, Bootstrap, Font Awesome, PrimeNG, and NG Matero
- RxJS
- STOMP and SockJS for real-time communication
- Chart.js, ng2-charts, and D3 for dashboards
- jsPDF and jsPDF AutoTable for PDF export
- XLSX, ExcelJS, and FileSaver for spreadsheet import/export
- Angular service worker support
- Optional Angular SSR support; SSR and prerendering are disabled for development builds

### Backend

- Java 17
- Spring Boot 3.3.1
- Spring Web, Data JPA, Security, Mail, WebSocket, HATEOAS, and Async
- JWT authentication with BCrypt password hashing
- H2 embedded database for local development
- MySQL connector retained for production integration
- Cloudinary for uploaded files and profile photos
- Apache POI for Excel processing
- JasperReports and DynamicReports for reports
- Telegram Bots integration
- MapStruct and ModelMapper

## 3. User types and access

Authorization combines a role with an optional job position.

### Roles

- `ADMIN` — system administrator
- `USER` — standard authenticated account

### Special positions

- `Human Resource(Main)` — main HR user with system-management permissions
- `Human Resource` — HR user who can manage staff and submit or create announcements
- Other positions — regular staff access

### Access summary

- Administrators manage categories, companies, departments, positions, groups, users, announcements, and HR Main assignments.
- HR Main users access the system dashboard and most organizational and announcement-management functions.
- HR users manage staff in their permitted scope and submit or create announcements.
- Regular staff view assigned announcements, acknowledge them, submit feedback, and manage their profile.
- Frontend route guards and backend endpoint authorization both enforce access.

## 4. Authentication and account features

- Login using company staff ID and password
- JWT authentication
- JWT accepted from an HTTP-only-style cookie flow or `Authorization: Bearer` header
- Stateless backend sessions
- Current-user and profile lookup
- Logout and local-session cleanup
- Forced or direct password change
- Change old password from the profile
- Forgot-password flow using email OTP
- OTP verification and password reset
- Remember-me option
- Role- and position-based redirect after login
- Authentication and role guards for protected pages
- Staff activation and inactivation

### Demo accounts

The development data loader creates:

- Administrator staff ID: `SEED_ADMIN_STAFF_ID` (defaults to `ADMIN001`)
- Staff user ID: `SEED_USER_STAFF_ID` (defaults to `EMP001`)
- Passwords: `DEFAULT_ADMIN_PASSWORD` and `DEFAULT_USER_PASSWORD`

Passwords are stored as BCrypt hashes.

When an account still uses its generated default password, login returns a
change-password instruction instead of issuing a JWT. The frontend redirects
the user to `/acknowledgeHub/change-password/:staffId` before normal access.

## 5. Dashboard features

### System dashboard

- Intended for administrators and HR Main
- Staff summary cards
- Announcement statistics
- Published and pending announcement counts
- Monthly announcement charts
- Staff acknowledgement statistics
- Recent announcement information

### Staff dashboard

- Assigned announcements
- Acknowledged and unacknowledged announcement views
- Monthly acknowledgement chart
- Personal announcement history
- Quick navigation to announcement details

## 6. Announcement management

- Create announcements
- Create announcements as HR
- Submit announcements for approval
- Approve or reject announcement requests
- Cancel pending requests
- Publish immediately
- Schedule publication using date and time
- Update announcements
- Soft-status handling
- Assign announcements directly to selected staff
- Assign announcements through groups
- Select an announcement category
- Attach files
- Upload and retain file versions
- Retrieve the latest file version
- View version history
- Download announcement files
- Generate announcement reports
- List published announcements
- List pending announcements
- List requests submitted by the current HR user
- View detailed announcement information
- Mark an announcement as acknowledged/noted
- Check whether a staff member already acknowledged an announcement
- View acknowledged staff
- View unacknowledged staff
- Count acknowledgements per announcement
- Filter staff results based on direct or group assignment
- Email or Telegram delivery support for announcement files

### Announcement workflow

1. Admin or authorized HR creates or submits an announcement.
2. The announcement can be assigned to individual staff or groups.
3. If approval is required, Admin or HR Main approves or rejects it.
4. The announcement is published immediately or at its scheduled time.
5. Assigned staff receive notifications and view the announcement.
6. Staff acknowledge the announcement.
7. Authorized users review acknowledged and unacknowledged staff lists.

## 7. Feedback features

- Staff can submit feedback/questions on announcements.
- Authorized users can reply to feedback.
- Feedback and replies update in real time through WebSocket topics.
- Typing status can be broadcast over WebSocket.
- Feedback lists include staff identity and profile images.
- Feedback reports can be generated and downloaded.
- JasperReports supports report generation in backend services.
- Frontend detail views can export data to PDF and Excel.

## 8. Real-time notifications

- SockJS endpoint: `/ws`
- STOMP application prefix: `/app`
- Simple broker destinations: `/topic` and `/queue`
- Staff-specific notification topics
- Notification list retrieval
- Mark notifications as checked
- Broadcast notification status updates
- Real-time feedback and reply updates
- Real-time typing status

Important destinations include:

- `/topic/notification/{staffId}`
- `/topic/notifications/{staffId}`
- `/topic/notificationStatusUpdate`
- `/topic/feedback/`
- `/topic/typing`

## 9. Staff management

- Add individual staff accounts
- Import staff from an Excel workbook
- Paginated and searchable staff list
- Staff detail data with company, department, position, role, and status
- Activate and inactivate staff
- Assign positions and departments
- Assign staff to groups
- Promote an eligible staff member to HR Main
- List HR staff
- List staff available for group assignment
- Upload profile photos
- View profile information
- Change account password
- Staff summary statistics
- Announcement list by staff
- Acknowledged and unacknowledged staff reports

### Excel import behavior

- Upload an Excel staff file
- Create or update staff data
- Associate staff with organizational groups
- Update statuses for staff missing from a later import
- Send account-related emails asynchronously

Expected spreadsheet columns are:

1. Staff ID
2. Name
3. Email
4. Position
5. Department
6. Company
7. Telegram username

Import supports add-only and override modes. Override mode can deactivate staff
IDs that are absent from the imported file.

## 10. Organization management

### Companies

- Add companies
- List companies
- View a company
- Rename/update a company
- Delete a company
- Update associated group names when a company name changes

### Departments

- Add departments
- List departments by company
- View a department
- Update departments
- Create departments in an HR workflow

### Positions

- Add positions
- List positions
- View a position
- Update positions

### Categories

- Add announcement categories
- List active categories
- View a category
- Update categories
- Delete categories
- Soft-delete categories

### Groups

- Create groups
- List groups
- View group details
- Assign staff to groups
- List groups by HR/company scope
- Activate groups
- Soft-delete groups
- List groups assigned to an announcement
- Automatically maintain organization-based groups during imports

## 11. Profile features

- View the authenticated staff profile
- Display company, department, position, role, and account information
- Upload/change profile photo
- Cache-busted profile image loading
- Change password after validating the old password
- View personal monthly acknowledgement statistics

## 12. Reporting and export

- Excel staff import
- Excel export from frontend views
- PDF export using jsPDF
- PDF tables using jsPDF AutoTable
- Announcement report generation
- Feedback report generation
- Asynchronous report processing
- Downloadable announcement files
- JasperReports templates:
  - `reports/announcement.jrxml`
  - `reports/feedbackReport.jrxml`

## 13. Email features

- Send password-reset OTP codes
- Verify OTP codes
- Update a password after OTP verification
- HTML email support
- Asynchronous email sending
- File email support in the backend service
- SMTP configuration through application properties

External SMTP credentials are required for real email delivery.

## 14. Telegram integration

- Telegram bot registration
- Associate Telegram-related details with staff
- Send announcement files through Telegram
- Asynchronous bot delivery

A valid bot name and token are required. Only one long-polling instance of the same Telegram bot token should run at a time, otherwise Telegram returns a `409 Conflict`.

## 15. File and media management

- Multipart uploads up to 10 MB
- Cloudinary upload integration
- Profile photo uploads
- Announcement file uploads
- File version lookup and download
- Default profile image served from classpath resources
- Uploaded/static image resource mapping

Cloudinary credentials are required for Cloudinary-backed uploads.

## 16. Frontend routes

### Public/account routes

- `/acknowledgeHub/login`
- `/acknowledgeHub/change-password/:staffId`
- `/acknowledgeHub/otp-request`
- `/acknowledgeHub/otp-input`
- `/acknowledgeHub/add-password`
- `/acknowledgeHub/noted`
- `/acknowledgeHub/404`

### Dashboard and profile routes

- `/acknowledgeHub/system-dashboard`
- `/acknowledgeHub/staff-dashboard`
- `/acknowledgeHub/profile`

### Company routes

- `/acknowledgeHub/company/add`
- `/acknowledgeHub/company/list`
- `/acknowledgeHub/company/update/:id`

### Department routes

- `/acknowledgeHub/department/add`
- `/acknowledgeHub/department/list`
- `/acknowledgeHub/department/update/:id`

### Position routes

- `/acknowledgeHub/position/add`
- `/acknowledgeHub/position/list`
- `/acknowledgeHub/position/update/:id`

### Group routes

- `/acknowledgeHub/group/add`
- `/acknowledgeHub/group/list`

### Category routes

- `/acknowledgeHub/add-category`
- `/acknowledgeHub/list-category`
- `/acknowledgeHub/update-category/:id`

### User routes

- `/acknowledgeHub/users/list`
- `/acknowledgeHub/users/add`
- `/acknowledgeHub/users/requested`
- `/acknowledgeHub/add-hr-main`
- `/acknowledgeHub/import-excel`

### Announcement routes

- `/acknowledgeHub/announcement/detail/:id`
- `/acknowledgeHub/announcement/list`
- `/acknowledgeHub/announcement/add`
- `/acknowledgeHub/announcement/create`
- `/acknowledgeHub/announcement/update/:id`
- `/acknowledgeHub/announcement/request`
- `/acknowledgeHub/announcement/requested-list`
- `/acknowledgeHub/announcement/request-list`
- `/acknowledgeHub/announcement/pending-announcement`
- `/acknowledgeHub/announcement/notNoted-announceemnt/:id/:status/:name/:file`
- `/acknowledgeHub/announcement/noted-announcement/:id/:name/:file`
- `/acknowledgeHub/announcement/staff-noted`
- `/acknowledgeHub/announcement/staff-unnoted`
- `/acknowledgeHub/announcement/list-by-staff`

Unknown routes redirect to `/acknowledgeHub/404`.

## 17. Backend API areas

The main API prefix is `/api/v1`.

### Authentication — `/auth`

- `POST /auth/login`
- `POST /auth/changePassword`
- `GET /auth/me`
- `POST /auth/logout`
- `GET /auth/profile`

### Announcements — `/api/v1/announcement`

- Create/update multipart announcements
- Get announcement details and latest versions
- List published, pending, requested, staff, and HR announcements
- Approve, reject, cancel, and publish announcements
- Download and report endpoints
- Acknowledgement and statistics endpoints

### Staff — `/api/v1/staff`

- Add/list/search staff
- Group staff lookup
- HR listing and HR Main promotion
- Profile photo upload
- Staff announcement and chart data
- Acknowledgement operations
- Password validation/change
- Activate/inactivate staff

### Categories — `/api/v1/category`

- Save, list, view, update, delete, and soft-delete categories

### Companies — `/api/v1/company/sys`

- List, view, add, update, and delete companies

### Departments — `/api/v1/department`

- Company list lookup
- Department lookup by company
- Create and update departments

### Positions — `/api/v1/position`

- List, add, view, and update positions

### Groups — `/api/v1/group`

- List, create, view, activate, soft-delete, and announcement-group lookup

### Feedback — `/api/v1/feedback`

- Send feedback
- List feedback by announcement
- Generate feedback reports

### Feedback replies — `/api/v1/feedback-reply`

- Save replies
- WebSocket typing updates

### Excel — `/api/v1/excel`

- Upload staff spreadsheets

### Email — `/api/v1/email`

- Send OTP
- Verify OTP
- Update password

### Notifications

- Update notification status
- Check individual notifications
- WebSocket notification send/list messages

## 18. Security design

- CSRF is disabled for the stateless API.
- JWT authorization runs before Spring Security username/password filtering.
- BCrypt hashes all generated/default passwords.
- Route groups use combinations of:
  - `ROLE_ADMIN`
  - `Human Resource(Main)`
  - `Human Resource`
  - authenticated staff
- CORS allows the Angular development origin `http://localhost:4200`.
- WebSocket CORS also allows `http://localhost:4200`.
- Production deployments must configure the real frontend origin.

## 19. Database model

### Entity tables

- `company`
- `department`
- `position`
- `staff`
- `Category`
- `group`
- `announcement`
- `feedback`
- `feedback_reply`
- `notification`
- `staff_noted_announcement`

### Relationship tables

- `staff_has_group`
- `group_has_announcement`
- `staff_has_announcement`

### Main relationships

- A company has departments and staff.
- A department belongs to a company.
- Staff belong to a company, department, and position.
- Groups contain multiple staff.
- Announcements belong to a creator and category.
- Announcements target multiple groups or individual staff.
- Feedback belongs to a staff member and announcement.
- A feedback reply belongs to feedback and its replying staff member.
- Notifications belong to a staff member and announcement.
- Staff acknowledgement records connect staff and announcements.

## 20. Development test data

The startup data loader safely creates a large demonstration dataset without
duplicating it on later restarts. Seeding runs in a single transaction.

### Seeded record counts

The verified clean-database dataset contains:

- 4 companies
- 8 departments
- 8 positions
- 27 staff accounts
- 7 categories
- 8 groups
- 24 announcements
- 45 feedback messages
- 30 feedback replies
- 105 notifications
- 71 staff acknowledgement records
- 60 staff/group memberships
- 12 group/announcement assignments
- 72 direct staff/announcement assignments

### Feature coverage

- Multiple companies and two departments per company
- Admin, HR Main, HR, manager, engineer, analyst, accountant, support and
  operations personas
- Active and inactive staff
- Active and inactive categories
- Global, company, engineering, leadership and inactive groups
- Group-targeted and directly targeted announcements
- Published announcements distributed across the previous 12 months
- Future scheduled announcements
- Pending approval requests
- Rejected requests
- Active and inactive announcement records
- A mixture of acknowledged and unacknowledged recipients
- Read and unread notifications
- Multiple feedback questions and replies
- Sufficient historical data for dashboard cards, monthly charts, reports,
  filters, pagination and export features

Seed IDs and passwords are controlled by `.env`. Other generated staff use IDs
from `DEMO001` through `DEMO024` and the configured default user password.

## 21. Local database

Development uses an embedded file-based H2 database, so MySQL is not required.

- JDBC URL: `jdbc:h2:file:./data/acknowledge_hub;AUTO_SERVER=TRUE`
- Username: `sa`
- Password: empty
- H2 console: `http://localhost:8080/h2-console`
- Database files: `acKnowledgeHub(backend)/data/`

MySQL support remains in the Maven dependencies for a future production configuration.

## 22. Configuration

Copy the environment template and enter your own values:

```bash
cd "acKnowledgeHub(backend)"
cp .env.example .env
```

Export the variables before running the backend:

```bash
set -a
source .env
set +a
mvn spring-boot:run
```

Required environment variables include:

- `JWT_SECRET` (64+ characters recommended; shorter values are hashed to 512 bits)
- `MAIL_USERNAME` and `MAIL_PASSWORD`
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, and `CLOUDINARY_API_SECRET`
- `TELEGRAM_BOT_NAME` and `TELEGRAM_BOT_TOKEN`
- `DEFAULT_USER_PASSWORD` and `DEFAULT_ADMIN_PASSWORD`

Deployment URLs, CORS origins, database credentials, and seed account IDs can
also be overridden with the variables documented in `.env.example`.

Do not commit `.env`. Spring's `application.properties` contains only
environment-variable references and safe local defaults.

Frontend environments:

- Development API: `http://localhost:8080`
- Production API: same-origin requests through an empty API base URL

## 23. Running the project

### Prerequisites

- Java 17 or newer
- Maven
- Node.js and npm

MySQL is not required for local development.

### Start the backend

```bash
cd "acKnowledgeHub(backend)"
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`

### Start the frontend

```bash
cd "acKnowledgeHub_angular(frontend)"
npm install
npm start
```

Frontend URL: `http://localhost:4200`

### Build

```bash
cd "acKnowledgeHub(backend)"
mvn package
```

```bash
cd "acKnowledgeHub_angular(frontend)"
npm run build
```

### Tests and checks

```bash
cd "acKnowledgeHub(backend)"
mvn test
```

```bash
cd "acKnowledgeHub_angular(frontend)"
npm test
npx tsc -p tsconfig.app.json --noEmit
```

## 24. Project structure

```text
Acknowledge_Hub-Final-OJT-PROJECT/
├── acKnowledgeHub(backend)/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/ace/
│       │   ├── configuration/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── security/
│       │   └── service/
│       └── main/resources/
│           ├── application.properties.example
│           └── reports/
├── acKnowledgeHub_angular(frontend)/
│   ├── angular.json
│   ├── package.json
│   └── src/
│       ├── app/
│       │   ├── announcement/
│       │   ├── category/
│       │   ├── chart/
│       │   ├── company/
│       │   ├── department/
│       │   ├── group/
│       │   ├── guard/
│       │   ├── services/
│       │   └── user/
│       └── environments/
└── PROJECT_FEATURES.md
```

## 25. Deployment notes

- Replace development CORS origins with the deployed frontend origin.
- Use strong secrets from environment variables or a secret manager.
- Configure a production database profile if moving from H2 to MySQL.
- Do not expose the H2 console in production.
- Disable demo data seeding for a real production environment if sample records are not wanted.
- Use HTTPS so authentication cookies and tokens are protected.
- Configure Cloudinary, SMTP, and Telegram only when those integrations are enabled.
- Run only one application instance using a long-polling Telegram bot token, or redesign the bot integration for multi-instance deployment.

## 26. Implementation notes and known caveats

These items describe the current implementation and should be reviewed before
production deployment or a larger refactor.

### Frontend

- The route
  `/acknowledgeHub/announcement/notNoted-announceemnt/:id/:status/:name/:file`
  contains the existing `announceemnt` spelling and must be used exactly unless
  both route definitions and navigation calls are renamed together.
- The Excel import route uses `position` while `RoleGuard` reads `positions`;
  its position restriction may not be enforced as intended.
- `AdminDashboardComponent` exists, but its separate route is commented out.
  Administrators currently use the system dashboard.
- The OTP back-navigation method uses `/login` instead of
  `/acknowledgeHub/login`.
- The sidebar contains duplicate announcement menu IDs.
- Plain Human Resource users are redirected to the staff dashboard while still
  receiving HR-specific menu actions.
- Group routes allow administrators, but the sidebar currently displays the
  group menu only for HR Main.
- Announcement IDs passed to the detail screen are Base64-encoded by frontend
  navigation.
- One feedback-report request disables credentials and may fail when cookie
  authentication is required.
- A feedback-reply delete URL contains a trailing comma in its current path.
- WebSocket startup depends on the staff ID being stored in `localStorage`.
- `PositionService.getPositionById` does not currently send credentials.
- The helper method `hasPostion` retains a spelling error in its API name.

### Backend

- HR authorization is represented by the staff position name, not by additional
  values in the `Role` enum.
- OTP values are held in an in-memory map and are lost on restart; this is not
  suitable for clustered deployment.
- Announcement scheduling uses an in-memory scheduler and scheduled tasks are
  not automatically restored after a process restart.
- Several email links currently contain localhost backend URLs.
- The JWT cookie is configured as secure; browser behavior must be checked when
  developing over plain HTTP.
- A token blacklist service exists, but logout primarily clears the cookie.
- `/api/v1/announcement/publish-now/{id}` and some other endpoints rely on the
  catch-all authenticated rule rather than a more restrictive path rule.
- The Excel endpoint name includes `allSys`, but its path does not match the
  `/sys/**` authorization matcher.
- Profile photos are stored under the local images resource path, while
  announcement attachments use Cloudinary.
- Announcement updates use versioned rows/files rather than replacing every
  previous artifact.
- Postmark and Web Push dependencies are present but are not active features.
- The bot REST controller and selection controller are commented out.
- The H2 console should be disabled outside local development.
