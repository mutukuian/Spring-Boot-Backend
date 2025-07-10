package com.projects.learningspringboot.model.authmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class Role {
    @Id
    private int roleID;

    private String roleName;
}
