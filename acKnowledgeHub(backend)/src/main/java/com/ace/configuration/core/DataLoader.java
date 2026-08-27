package com.ace.configuration.core;

import com.ace.entity.*;
import com.ace.enums.Role;
import com.ace.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Configuration
public class DataLoader {

    private final StaffRepository staffRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final AnnouncementRepository announcementRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackReplyRepository feedbackReplyRepository;
    private final NotificationRepository notificationRepository;
    private final NotedRepository notedRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.photo.path}")
    private String DEFAULT_PHOTO_PATH;

    @Value("${app.default-password.user}")
    private String defaultUserPassword;

    @Value("${app.default-password.admin}")
    private String defaultAdminPassword;

    @Value("${app.seed.admin.staff-id}")
    private String seedAdminStaffId;

    @Value("${app.seed.admin.email}")
    private String seedAdminEmail;

    @Value("${app.seed.user.staff-id}")
    private String seedUserStaffId;

    @Value("${app.seed.user.email}")
    private String seedUserEmail;

    public DataLoader(StaffRepository staffRepository,
                      CompanyRepository companyRepository,
                      PositionRepository positionRepository,
                      DepartmentRepository departmentRepository,
                      GroupRepository groupRepository,
                      CategoryRepository categoryRepository,
                      AnnouncementRepository announcementRepository,
                      FeedbackRepository feedbackRepository,
                      FeedbackReplyRepository feedbackReplyRepository,
                      NotificationRepository notificationRepository,
                      NotedRepository notedRepository,
                      PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.announcementRepository = announcementRepository;
        this.feedbackRepository = feedbackRepository;
        this.feedbackReplyRepository = feedbackReplyRepository;
        this.notificationRepository = notificationRepository;
        this.notedRepository = notedRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner loadData(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return args -> transactionTemplate.executeWithoutResult(status -> seedData());
    }

    private void seedData() {
        List<Company> companies = seedCompanies();
        List<Department> departments = seedDepartments(companies);
        Map<String, Position> positions = seedPositions();
        List<Category> categories = seedCategories();
        List<Staff> staff = seedStaff(companies, departments, positions);
        List<Group> groups = seedGroups(companies, staff);
        List<Announcement> announcements = seedAnnouncements(staff, groups, categories);
        seedActivity(staff, announcements);
    }

    private List<Company> seedCompanies() {
        return List.of(
                findOrCreateCompany("ACE Data Systems Ltd. (ACE)"),
                findOrCreateCompany("Apex Finance Group"),
                findOrCreateCompany("Nova Logistics"),
                findOrCreateCompany("Bright Health Services")
        );
    }

    private Company findOrCreateCompany(String name) {
        return companyRepository.findByName(name).stream().findFirst()
                .orElseGet(() -> {
                    Company company = new Company();
                    company.setName(name);
                    return companyRepository.save(company);
                });
    }

    private List<Department> seedDepartments(List<Company> companies) {
        return List.of(
                findOrCreateDepartment("ERP", companies.get(0)),
                findOrCreateDepartment("Software Engineering", companies.get(0)),
                findOrCreateDepartment("Banking Operations", companies.get(1)),
                findOrCreateDepartment("Risk and Compliance", companies.get(1)),
                findOrCreateDepartment("Fleet Operations", companies.get(2)),
                findOrCreateDepartment("Customer Logistics", companies.get(2)),
                findOrCreateDepartment("Clinical Operations", companies.get(3)),
                findOrCreateDepartment("Patient Support", companies.get(3))
        );
    }

    private Department findOrCreateDepartment(String name, Company company) {
        Department existing = departmentRepository.findByNameAndCompany(name, company.getName());
        if (existing != null) {
            return existing;
        }
        Department department = new Department();
        department.setName(name);
        department.setCompany(company);
        return departmentRepository.save(department);
    }

    private Map<String, Position> seedPositions() {
        Map<String, Position> positions = new LinkedHashMap<>();
        List.of("Manager", "Human Resource(Main)", "Human Resource", "Software Engineer",
                "Business Analyst", "Accountant", "Customer Support", "Operations Officer")
                .forEach(name -> positions.put(name, findOrCreatePosition(name)));
        return positions;
    }

    private Position findOrCreatePosition(String name) {
        return positionRepository.findByName(name).stream().findFirst()
                .orElseGet(() -> {
                    Position position = new Position();
                    position.setName(name);
                    return positionRepository.save(position);
                });
    }

    private List<Category> seedCategories() {
        return List.of(
                findOrCreateCategory("General", "General company announcements", "active"),
                findOrCreateCategory("Human Resources", "Policies, benefits and people updates", "active"),
                findOrCreateCategory("Information Security", "Security alerts and required actions", "active"),
                findOrCreateCategory("Training", "Courses, workshops and learning material", "active"),
                findOrCreateCategory("Events", "Company events and staff activities", "active"),
                findOrCreateCategory("Operations", "Operational notices and service updates", "active"),
                findOrCreateCategory("Archived", "Inactive category for status testing", "inactive")
        );
    }

    private Category findOrCreateCategory(String name, String description, String status) {
        Category existing = categoryRepository.findByLowerName(name);
        if (existing != null) {
            return existing;
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setCreatedAt(LocalDate.now());
        category.setStatus(status);
        return categoryRepository.save(category);
    }

    private List<Staff> seedStaff(List<Company> companies, List<Department> departments,
                                  Map<String, Position> positions) {
        List<Staff> staff = new ArrayList<>();
        staff.add(findOrCreateStaff(seedAdminEmail, seedAdminStaffId, "System Administrator",
                Role.ADMIN, positions.get("Manager"), companies.get(0), departments.get(0), "active"));
        staff.add(findOrCreateStaff(seedUserEmail, seedUserStaffId, "Demo HR User",
                Role.USER, positions.get("Human Resource"), companies.get(0), departments.get(0), "active"));
        staff.add(findOrCreateStaff("hr.main@example.test", "HRMAIN001", "Maya HR Main",
                Role.USER, positions.get("Human Resource(Main)"), companies.get(0), departments.get(0), "active"));

        String[] firstNames = {"Aung", "Mya", "Thiri", "Min", "Su", "Kyaw", "Nandar", "Htet",
                "Ei", "Zaw", "May", "Linn", "Nyein", "Soe", "Khin", "Wai", "Phyo", "Yoon",
                "Han", "Chit", "Nan", "Ko", "Cherry", "Sai"};
        String[] positionNames = {"Software Engineer", "Business Analyst", "Accountant",
                "Customer Support", "Operations Officer", "Manager"};

        for (int i = 0; i < firstNames.length; i++) {
            int companyIndex = i % companies.size();
            int departmentIndex = companyIndex * 2 + (i % 2);
            String staffId = String.format("DEMO%03d", i + 1);
            String status = i == firstNames.length - 1 || i == firstNames.length - 2
                    ? "inactive" : "active";
            staff.add(findOrCreateStaff(
                    "demo" + (i + 1) + "@example.test",
                    staffId,
                    firstNames[i] + " Demo",
                    Role.USER,
                    positions.get(positionNames[i % positionNames.length]),
                    companies.get(companyIndex),
                    departments.get(departmentIndex),
                    status
            ));
        }
        return staff;
    }

    private Staff findOrCreateStaff(String email, String companyStaffId, String name,
                                    Role role, Position position, Company company,
                                    Department department, String status) {
        String defaultPassword = role == Role.ADMIN ? defaultAdminPassword : defaultUserPassword;
        Staff existing = staffRepository.findByEmail(email);
        if (existing == null) {
            existing = staffRepository.findByCompanyStaffId(companyStaffId);
        }
        if (existing != null) {
            if (usesKnownDefaultPassword(existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(defaultPassword));
                return staffRepository.save(existing);
            }
            return existing;
        }

        Staff staff = new Staff();
        staff.setName(name);
        staff.setCompanyStaffId(companyStaffId);
        staff.setEmail(email);
        staff.setCreatedAt(new Date());
        staff.setStatus(status);
        staff.setCompany(company);
        staff.setDepartment(department);
        staff.setPosition(position);
        staff.setRole(role);
        staff.setPassword(passwordEncoder.encode(defaultPassword));
        staff.setPhotoPath(DEFAULT_PHOTO_PATH);
        return staffRepository.save(staff);
    }

    private boolean usesKnownDefaultPassword(String encodedPassword) {
        return passwordEncoder.matches(defaultUserPassword, encodedPassword)
                || passwordEncoder.matches(defaultAdminPassword, encodedPassword)
                || passwordEncoder.matches("acknowledgeHub", encodedPassword)
                || passwordEncoder.matches("adminPassword", encodedPassword)
                || passwordEncoder.matches("replace-with-a-temporary-user-password", encodedPassword)
                || passwordEncoder.matches("replace-with-a-temporary-admin-password", encodedPassword);
    }

    private List<Group> seedGroups(List<Company> companies, List<Staff> staff) {
        List<Staff> activeUsers = staff.stream()
                .filter(user -> user.getRole() == Role.USER && "active".equals(user.getStatus()))
                .toList();
        List<Group> groups = new ArrayList<>();
        groups.add(findOrCreateGroup("Global Group", "active", activeUsers));
        for (Company company : companies) {
            List<Staff> companyStaff = activeUsers.stream()
                    .filter(user -> user.getCompany().getId() == company.getId())
                    .toList();
            groups.add(findOrCreateGroup(company.getName() + " Group", "active", companyStaff));
        }
        groups.add(findOrCreateGroup("Engineering Community", "active",
                activeUsers.stream().filter(user -> "Software Engineer".equals(user.getPosition().getName())).toList()));
        groups.add(findOrCreateGroup("People Leaders", "active",
                activeUsers.stream().filter(user -> user.getPosition().getName().contains("Human Resource")
                        || "Manager".equals(user.getPosition().getName())).toList()));
        groups.add(findOrCreateGroup("Legacy Project Group", "inactive", activeUsers.subList(0, 3)));
        return groups;
    }

    private Group findOrCreateGroup(String name, String status, List<Staff> members) {
        Group group = groupRepository.findByName(name);
        if (group == null) {
            group = new Group();
            group.setName(name);
            group.setStatus(status);
            group = groupRepository.save(group);
        }

        if (group.getStaff() == null) {
            group.setStaff(new ArrayList<>());
        }
        Set<Integer> currentMemberIds = new HashSet<>(
                group.getStaff().stream().map(Staff::getId).toList());
        boolean changed = false;
        for (Staff member : members) {
            if (currentMemberIds.add(member.getId())) {
                group.getStaff().add(member);
                changed = true;
            }
        }
        if (changed) {
            group = groupRepository.save(group);
        }
        return group;
    }

    private List<Announcement> seedAnnouncements(List<Staff> staff, List<Group> groups,
                                                  List<Category> categories) {
        String[] topics = {
                "Welcome to Acknowledge Hub", "Annual Leave Policy", "Password Security Reminder",
                "Quarterly Town Hall", "Workplace Safety Training", "System Maintenance Window",
                "Employee Wellness Program", "Data Privacy Guidelines", "New Benefits Package",
                "Fire Drill Schedule", "Customer Service Workshop", "Holiday Calendar",
                "Expense Claim Procedure", "Remote Work Guidelines", "Upcoming Leadership Forum",
                "Scheduled Network Upgrade", "HR Policy Review Request", "Training Budget Request",
                "New Office Proposal", "Team Building Request", "Rejected Vendor Notice",
                "Rejected Schedule Change", "Archived Operations Notice", "Emergency Contact Update"
        };

        Staff admin = staff.get(0);
        List<Announcement> announcements = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < topics.length; i++) {
            boolean published = i < 14 || i == 23;
            String permission = i < 17 || i == 22 || i == 23
                    ? "approved" : i < 21 ? "pending" : "reject";
            String status = i == 22 ? "inactive" : "active";
            LocalDateTime scheduleAt = i < 14
                    ? now.minusMonths(i % 12).minusDays(i + 1L)
                    : now.plusDays(i - 12L);
            boolean groupDelivery = i % 2 == 0;
            List<Group> targetGroups = groupDelivery
                    ? List.of(groups.get(i % (groups.size() - 1)))
                    : List.of();
            List<Staff> targetStaff = groupDelivery
                    ? List.of()
                    : rotatingStaff(staff, i, 6);

            announcements.add(findOrCreateAnnouncement(
                    "Demo " + String.format("%02d", i + 1) + " - " + topics[i],
                    "Sample content for " + topics[i] + ". This record demonstrates announcement workflows, reporting and dashboard statistics.",
                    (byte) (groupDelivery ? 1 : 0),
                    admin,
                    categories.get(i % (categories.size() - 1)),
                    targetGroups,
                    targetStaff,
                    permission,
                    published,
                    status,
                    scheduleAt
            ));
        }
        return announcements;
    }

    private List<Staff> rotatingStaff(List<Staff> staff, int offset, int count) {
        List<Staff> activeUsers = staff.stream()
                .filter(user -> user.getRole() == Role.USER && "active".equals(user.getStatus()))
                .toList();
        List<Staff> selected = new ArrayList<>();
        for (int i = 0; i < Math.min(count, activeUsers.size()); i++) {
            selected.add(activeUsers.get((offset + i) % activeUsers.size()));
        }
        return selected;
    }

    private Announcement findOrCreateAnnouncement(String title, String description,
                                                  byte groupStatus, Staff creator,
                                                  Category category, List<Group> groups,
                                                  List<Staff> staff, String permission,
                                                  boolean published, String status,
                                                  LocalDateTime scheduleAt) {
        Announcement existing = announcementRepository.findAll().stream()
                .filter(announcement -> title.equals(announcement.getTitle()))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            if (isMissingAttachment(existing.getFile())) {
                existing.setFile(localDemoAttachment());
                return announcementRepository.save(existing);
            }
            return existing;
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setDescription(description);
        announcement.setFile(localDemoAttachment());
        announcement.setPublished(published);
        announcement.setStatus(status);
        announcement.setPermission(permission);
        announcement.setGroupStatus(groupStatus);
        announcement.setScheduleAt(scheduleAt);
        announcement.setCreated_at(Date.from(scheduleAt.atZone(ZoneId.systemDefault()).toInstant()));
        announcement.setCreateStaff(creator);
        announcement.setCategory(category);
        announcement.setGroup(new ArrayList<>(groups));
        announcement.setStaff(new ArrayList<>(staff));
        return announcementRepository.save(announcement);
    }

    private String localDemoAttachment() {
        return "local:demo/announcement-sample.txt";
    }

    private boolean isMissingAttachment(String file) {
        return file == null || file.isBlank() || "N/A".equalsIgnoreCase(file.trim());
    }

    private void seedActivity(List<Staff> staff, List<Announcement> announcements) {
        Staff admin = staff.get(0);
        int activityIndex = 0;
        for (Announcement announcement : announcements) {
            if (!announcement.isPublished() || !"approved".equals(announcement.getPermission())
                    || !"active".equals(announcement.getStatus())) {
                continue;
            }

            List<Staff> recipients = recipientsFor(announcement);
            for (Staff recipient : recipients) {
                findOrCreateNotification(recipient, announcement, activityIndex % 4 == 0);
                if ((recipient.getId() + announcement.getId()) % 3 != 0) {
                    findOrCreateNoted(recipient, announcement);
                }
                activityIndex++;
            }

            for (int i = 0; i < Math.min(3, recipients.size()); i++) {
                Staff author = recipients.get(i);
                String content = "Demo feedback " + (i + 1) + " for announcement "
                        + announcement.getId() + ": please provide more details.";
                Feedback feedback = findOrCreateFeedback(author, announcement, content);
                if (i % 2 == 0) {
                    findOrCreateFeedbackReply(admin, feedback,
                            "Demo response: additional information has been provided.");
                }
            }
        }
    }

    private List<Staff> recipientsFor(Announcement announcement) {
        Map<Integer, Staff> recipients = new LinkedHashMap<>();
        if (announcement.getGroupStatus() == 1 && announcement.getGroup() != null) {
            announcement.getGroup().stream()
                    .filter(group -> group.getStaff() != null)
                    .flatMap(group -> group.getStaff().stream())
                    .forEach(staff -> recipients.put(staff.getId(), staff));
        } else if (announcement.getStaff() != null) {
            announcement.getStaff().forEach(staff -> recipients.put(staff.getId(), staff));
        }
        return new ArrayList<>(recipients.values());
    }

    private Feedback findOrCreateFeedback(Staff staff, Announcement announcement, String content) {
        Feedback existing = feedbackRepository.findAll().stream()
                .filter(feedback -> content.equals(feedback.getContent()))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            return existing;
        }

        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setStaff(staff);
        feedback.setAnnouncement(announcement);
        return feedbackRepository.save(feedback);
    }

    private void findOrCreateFeedbackReply(Staff staff, Feedback feedback, String content) {
        boolean exists = feedbackReplyRepository.findAll().stream()
                .anyMatch(reply -> reply.getFeedback() != null
                        && reply.getFeedback().getId().equals(feedback.getId()));
        if (exists) {
            return;
        }

        FeedbackReply reply = new FeedbackReply();
        reply.setContent(content);
        reply.setStaff(staff);
        reply.setFeedback(feedback);
        feedbackReplyRepository.save(reply);
    }

    private void findOrCreateNotification(Staff staff, Announcement announcement, boolean checked) {
        boolean exists = notificationRepository.findByStaffId(staff.getId()).stream()
                .anyMatch(notification -> notification.getAnnouncement().getId()
                        .equals(announcement.getId()));
        if (exists) {
            return;
        }

        Notification notification = new Notification();
        notification.setDescription("New demo announcement: " + announcement.getTitle());
        notification.setUrl("/acknowledgeHub/announcement/detail/"
                + Base64.getEncoder().encodeToString(announcement.getId().toString().getBytes()));
        notification.setChecked(checked);
        notification.setStatus(checked ? "inactive" : "active");
        notification.setStaff(staff);
        notification.setAnnouncement(announcement);
        notificationRepository.save(notification);
    }

    private void findOrCreateNoted(Staff staff, Announcement announcement) {
        if (notedRepository.findByStaffAndAnnouncement(staff, announcement).isPresent()) {
            return;
        }
        StaffNotedAnnouncement noted = new StaffNotedAnnouncement();
        noted.setStaff(staff);
        noted.setAnnouncement(announcement);
        notedRepository.save(noted);
    }
}




