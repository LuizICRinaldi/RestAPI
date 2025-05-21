package com.RestAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RestAPI.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByAssignedUsers_Id(Long id);
}
