package com.capstone.controller;

import com.capstone.model.StudentGrade;
import com.capstone.service.StudentGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

// Component Interface for Grade Display
interface GradeDisplay {
    String display();
}

// Concrete Component: Displays a placeholder
class PlaceholderGradeDisplay implements GradeDisplay {
    @Override
    public String display() {
        return "";
    }
}

// Concrete Component: Displays the actual grade
class RealGradeDisplay implements GradeDisplay {
    private final StudentGrade grade;

    public RealGradeDisplay(StudentGrade grade) {
        this.grade = grade;
    }

    @Override
    public String display() {
        return String.valueOf(grade.getScore()); // Assuming StudentGrade has a getScore() method
    }
}

// Base Decorator
abstract class GradeDisplayDecorator implements GradeDisplay {
    protected GradeDisplay decoratedDisplay;

    public GradeDisplayDecorator(GradeDisplay decoratedDisplay) {
        this.decoratedDisplay = decoratedDisplay;
    }

    @Override
    public String display() {
        return decoratedDisplay.display();
    }
}

// Concrete Decorator: Adds logging
class LoggingGradeDisplay extends GradeDisplayDecorator {
    private static final Logger logger = LoggerFactory.getLogger(LoggingGradeDisplay.class);

    public LoggingGradeDisplay(GradeDisplay decoratedDisplay) {
        super(decoratedDisplay);
    }

    @Override
    public String display() {
        String displayedGrade = super.display();
        logger.info("Displayed grade: {}", displayedGrade);
        return displayedGrade;
    }
}

// Concrete Decorator: Adds formatting
class FormattingGradeDisplay extends GradeDisplayDecorator {
    private final String format;

    public FormattingGradeDisplay(GradeDisplay decoratedDisplay, String format) {
        super(decoratedDisplay);
        this.format = format;
    }

    @Override
    public String display() {
        String displayedGrade = super.display();
        return String.format(format, displayedGrade);
    }
}

@RestController
@RequestMapping("/api/grades")
public class StudentGradeController {

    private final StudentGradeService studentGradeService;

    @Autowired
    public StudentGradeController(StudentGradeService studentGradeService) {
        this.studentGradeService = studentGradeService;
    }

    private GradeDisplay createGradeDisplay(StudentGrade grade) {
        GradeDisplay display;
        if (grade == null || grade.getScore() == null) {
            display = new PlaceholderGradeDisplay();
        } else {
            display = new RealGradeDisplay(grade);
            display = new LoggingGradeDisplay(display); // Add logging
            display = new FormattingGradeDisplay(display, "[Grade: %s]"); // Add formatting
        }
        return display;
    }

    @GetMapping("/display/{studentId}/team/{teamId}/phase/{phase}")
    public ResponseEntity<String> displayGrade(
            @PathVariable String studentId,
            @PathVariable String teamId,
            @PathVariable int phase) {
        Optional<StudentGrade> gradeOptional = studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, phase);
        GradeDisplay gradeDisplay = createGradeDisplay(gradeOptional.orElse(null));
        return ResponseEntity.ok(gradeDisplay.display());
    }

    // The save and update methods would still interact with the StudentGradeService
    // to persist the actual StudentGrade objects. The Decorator pattern here is focused
    // on how the grade is *displayed*.

    @PostMapping("/save")
    public ResponseEntity<StudentGrade> saveGrade(@RequestBody StudentGrade gradeRequest) {
        StudentGrade savedGrade = studentGradeService.saveGrade(gradeRequest);
        return ResponseEntity.ok(savedGrade);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StudentGrade> updateGrade(@PathVariable String id, @RequestBody StudentGrade updatedGrade) {
        StudentGrade result = studentGradeService.updateGrade(id, updatedGrade);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/team/{teamId}/phase/{phase}")
    public ResponseEntity<List<StudentGrade>> getGradesByTeamAndPhase(@PathVariable String teamId, @PathVariable int phase) {
        List<StudentGrade> grades = studentGradeService.getGradesByTeamIdAndPhase(teamId, phase);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/raw/{studentId}/team/{teamId}/phase/{phase}")
    public ResponseEntity<Optional<StudentGrade>> getRawGrade(
            @PathVariable String studentId,
            @PathVariable String teamId,
            @PathVariable int phase) {
        Optional<StudentGrade> grade = studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, phase);
        return ResponseEntity.ok(grade);
    }
}


// package com.capstone.controller;

// import com.capstone.model.StudentGrade;
// import com.capstone.service.StudentGradeService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// // Factory interface
// interface GradeFactory {
//     StudentGrade createGrade(StudentGrade gradeData);
// }

// // Concrete factory implementation
// class StandardGradeFactory implements GradeFactory {
//     @Override
//     public StudentGrade createGrade(StudentGrade gradeData) {
//         return gradeData;
//     }
// }

// @RestController
// @RequestMapping("/api/grades")
// public class StudentGradeController {
   
//     private final StudentGradeService studentGradeService;
//     private final GradeFactory gradeFactory;
   
//     // Constructor Injection
//     @Autowired
//     public StudentGradeController(StudentGradeService studentGradeService) {
//         this.studentGradeService = studentGradeService;
//         this.gradeFactory = new StandardGradeFactory(); // Default implementation
//     }
   
//     @PostMapping("/save")
//     public ResponseEntity<StudentGrade> saveGrade(@RequestBody StudentGrade gradeRequest) {
//         // Use factory to create appropriate grade type
//         StudentGrade grade = gradeFactory.createGrade(gradeRequest);
//         StudentGrade savedGrade = studentGradeService.saveGrade(grade);
//         return ResponseEntity.ok(savedGrade);
//     }
   
//     @PutMapping("/update/{id}")
//     public ResponseEntity<StudentGrade> updateGrade(@PathVariable String id, @RequestBody StudentGrade updatedGrade) {
//         StudentGrade grade = gradeFactory.createGrade(updatedGrade);
//         StudentGrade result = studentGradeService.updateGrade(id, grade);
//         return ResponseEntity.ok(result);
//     }
   
//     @GetMapping("/team/{teamId}/phase/{phase}")
//     public ResponseEntity<List<StudentGrade>> getGradesByTeamAndPhase(@PathVariable String teamId, @PathVariable int phase) {
//         List<StudentGrade> grades = studentGradeService.getGradesByTeamIdAndPhase(teamId, phase);
//         return ResponseEntity.ok(grades);
//     }
   
//     @GetMapping("/student/{studentId}/team/{teamId}/phase/{phase}")
//     public ResponseEntity<StudentGrade> getGradeByStudentTeamPhase(
//             @PathVariable String studentId,
//             @PathVariable String teamId,
//             @PathVariable int phase) {
//         Optional<StudentGrade> grade = studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, phase);
//         return grade.map(ResponseEntity::ok)
//                     .orElseGet(() -> ResponseEntity.notFound().build());
//     }
// }
