
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Assignment;

public interface AssignmentService {

    Assignment saveAssignment(Assignment assignment);

    List<Assignment> getAllAssignments();

    Assignment getAssignmentById(Long id);

    void deleteAssignment(Long id);
}