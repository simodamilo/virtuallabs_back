package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class ModelHelper {

    public static AssignmentDTO enrich(AssignmentDTO assignmentDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(VMController.class).slash(assignmentDTO.getId()).withSelfRel();
        Link solutionsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SolutionController.class)
                .getAssignmentSolutions(assignmentDTO.getId())).withRel("solutions");
        return assignmentDTO.add(selfLink, solutionsLink);
    }

    public static CourseDTO enrich(CourseDTO courseDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel();
        Link enrolledLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                .getEnrolledStudents(courseDTO.getName())).withRel("enrolled");
        return courseDTO.add(selfLink, enrolledLink);
    }

    public static ModelVMDTO enrich(ModelVMDTO modelVMDTO) {
        Link link = WebMvcLinkBuilder.linkTo(StudentController.class).slash(modelVMDTO.getId()).withSelfRel();
        return modelVMDTO.add(link);
    }

    public static SolutionDTO enrich(SolutionDTO solutionDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(VMController.class).slash(solutionDTO.getId()).withSelfRel();
        return solutionDTO.add(selfLink);
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        Link link = WebMvcLinkBuilder.linkTo(StudentController.class).slash(studentDTO.getSerial()).withSelfRel();
        return studentDTO.add(link);
    }

    public static TeacherDTO enrich(TeacherDTO teacherDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(TeacherController.class).slash(teacherDTO.getSerial()).withSelfRel();
        return teacherDTO.add(selfLink);
    }

    public static TeamDTO enrich(TeamDTO teamDTO) {
        Link membersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                .getTeamStudents(teamDTO.getId())).withRel("members");
        return teamDTO.add(membersLink);
    }

    public static VMDTO enrich(VMDTO vmDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(VMController.class).slash(vmDTO.getId()).withSelfRel();
        return vmDTO.add(selfLink);
    }
}

