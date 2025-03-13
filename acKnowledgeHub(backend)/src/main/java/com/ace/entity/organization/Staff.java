package com.ace.entity.organization;

import com.ace.utility.core.coreEntity.BaseTimestampEntity;
import com.ace.entity.Announcement.Announcement;
import com.ace.utility.enums.DefaultPassword;
import com.ace.utility.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "staff")
public class Staff extends BaseTimestampEntity implements UserDetails{

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
    @Column(name = "chat_id")
    private String chatId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;
    @Column(name = "photo_path")  // Field to store the file path
    private String photoPath;
    @Column(name = "telegram_name")
    private String telegramName;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "position_id")
    private Position position;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToMany(mappedBy = "staff",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Group> groups;
    @ManyToMany(mappedBy = "staff",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Announcement> announcement;


    @PrePersist
    protected void onCreate() {
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
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        if (this.position != null && this.position.getName() != null) {
            authorities.add(new SimpleGrantedAuthority(this.position.getName()));

        }
        return authorities;
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
        return "Staff{id=" + id + ", name='" + name + "', companyStaffId='" + companyStaffId + "', email='" + email + "', createdAt='" + super.createdAt + "'}";}
}
