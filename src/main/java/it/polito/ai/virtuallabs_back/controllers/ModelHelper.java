package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO courseDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel();
        Link enrolledLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CourseController.class)
                .enrolledStudents(courseDTO.getName())).withRel("enrolled");
        return courseDTO.add(selfLink, enrolledLink);
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        Link link = WebMvcLinkBuilder.linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel();
        return studentDTO.add(link);
    }

    public static TeamDTO enrich(TeamDTO teamDTO) {
        Link membersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                .getTeamMembers(teamDTO.getId())).withRel("members");
        return teamDTO.add(membersLink);
    }

    public static TeacherDTO enrich(TeacherDTO teacherDTO) {
        Link selfLink = WebMvcLinkBuilder.linkTo(TeacherController.class).slash(teacherDTO.getId()).withSelfRel();
        return teacherDTO.add(selfLink);
    }

}

