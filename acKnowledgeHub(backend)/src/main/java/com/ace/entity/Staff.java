package com.ace.entity;

import com.ace.enums.DefaultPassword;
import com.ace.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff")
public class Staff implements UserDetails {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "companyStaffId")
    private String companyStaffId;
    @Column(name = "email")
    private String email;
    @Column(name ="password")
    private String password;
    @Temporal(TemporalType.DATE)
    @Column(name="created_at")
    private Date createdAt;
    @Column(name = "status")
    private String status = "active";
    @Column(name = "chat_id")
    private String chatId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "position_id")
    private Position position;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToMany(mappedBy = "staff")
    @JsonIgnore
    private List<Group> groups;
    @ManyToMany(mappedBy = "staff")
    @JsonIgnore
    private List<Announcement> announcement = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date(System.currentTimeMillis());
        }
        if (this.password == null) {
            switch (this.role) {
                case ADMIN:
                    this.password = passwordEncoder.encode(DefaultPassword.ADMIN_PASSWORD.getPassword());
                    break;
                case USER:
                default:
                    this.password = passwordEncoder.encode(DefaultPassword.USER_PASSWORD.getPassword());
                    break;
            }
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    @Override
    public String getUsername() {
        return this.companyStaffId != null ? this.getEmail() : this.email; // Ensure email is used as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Staff{id=" + id + ", name='" + name + "', companyStaffId='" + companyStaffId + "', email='" + email + "', createdAt=" + createdAt + "}";
    }
}
