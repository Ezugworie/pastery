package com.ezez.pastery.DTO;

import lombok.Data;

@Data
public class UserDTO {

    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

}
