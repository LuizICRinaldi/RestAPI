package com.RestAPI.entity;

import java.util.List;

import com.RestAPI.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Status status = Status.IN_PROGRESS;
    @ManyToMany
    private List<User> assignedUsers;

    public Task() {}

    public Task(String title, String description, List<User> assignedUsers) {
        this.title = title;
        this.description = description;
        this.assignedUsers = assignedUsers;
    }

    public Task(Long id, String title, String description, List<User> assignedUsers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedUsers = assignedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
