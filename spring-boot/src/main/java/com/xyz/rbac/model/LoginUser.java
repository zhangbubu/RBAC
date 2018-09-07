package com.xyz.rbac.model;

import com.xyz.rbac.validation.IsLoginName;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Setter
@Getter
public class LoginUser {

    @NotEmpty
    @IsLoginName
    private String name;

    @NotEmpty
    private String password;


    private String code;
}
