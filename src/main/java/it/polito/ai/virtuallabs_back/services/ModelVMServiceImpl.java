package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.ModelVM;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.AssignmentChangeNotValid;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.ModelVMChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.ModelVMNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.ModelVMRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Transactional
public class ModelVMServiceImpl implements ModelVMService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelVMRepository modelVMRepository;

    @Override
    public ModelVMDTO getModelVm(String courseName) {
        ModelVM modelVM = utilityService.getCourse(courseName).getModelVM();
        if (modelVM == null)
            throw new ModelVMNotFoundException("Model not found");
        return modelMapper.map(modelVM, ModelVMDTO.class);
    }

    @Override
    public byte[] getModelVmContent(@PathVariable Long modelVmId) {
        return utilityService.getModelVm(modelVmId).getContent();
    }

    @Override
    public ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, String courseName) {
        Course course = utilityService.getCourse(courseName);

        utilityService.courseOwnerValid(courseName);

        if (course.getModelVM() != null || course.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is not possible to add a new modelVM");

        if (!course.isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        ModelVM modelVM = ModelVM.builder()
                .name(modelVMDTO.getName())
                .type(modelVMDTO.getType())
                .course(course)
                .build();

        return modelMapper.map(modelVMRepository.save(modelVM), ModelVMDTO.class);
    }

    @Override
    public ModelVMDTO addContent(Long modelVmId, MultipartFile file) {
        Teacher teacher = utilityService.getTeacher();
        ModelVM modelVM = utilityService.getModelVm(modelVmId);
        if (!modelVM.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (!teacher.getCourses().contains(modelVM.getCourse()))
            throw new AssignmentChangeNotValid("You have no permission to modify an assignment to this course");
        try {
            modelVM.setContent(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return modelMapper.map(modelVM, ModelVMDTO.class);
    }

    @Override
    public ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO) {
        ModelVM modelVM = utilityService.getModelVm(modelVMDTO.getId());
        Course course = utilityService.getCourse(modelVM.getCourse().getName());

        utilityService.courseOwnerValid(modelVM.getCourse().getName());

        if (course.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is not possible to add a new modelVM");

        if (!course.isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        modelVM.setName(modelVMDTO.getName());
        modelVM.setType(modelVMDTO.getType());

        return modelMapper.map(modelVM, ModelVMDTO.class);
    }

    @Override
    public void deleteModelVm(Long modelVmId) {
        Teacher teacher = utilityService.getTeacher();
        ModelVM modelVM = utilityService.getModelVm(modelVmId);

        if (!teacher.getCourses().contains(modelVM.getCourse()))
            throw new AssignmentChangeNotValid("You have no permission to modify an assignment to this course");

        if (!modelVM.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (modelVM.getCourse().getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is not possible to delete a new modelVM");

        modelVMRepository.delete(modelVM);
    }
}
