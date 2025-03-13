package com.ace.staff.dtos;

import com.ace.utility.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserInfo {
    private int id;
    private String staffId;
    private String name;
    private Role role;
    private String Position;

    public LoginUserInfo() {

    }
}
