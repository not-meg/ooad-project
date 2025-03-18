# ooad-project
capstone project management
*Refactored Capstone Project Management System - Class Diagram Specification*

### 1. *User (Abstract)*
- Attributes:
  - +userID: String
  - +name: String
  - +email: String
  - +password: String
  - #role: String
- Methods:
  - +login(): Boolean
  - +logout(): void
  - #viewDashboard(): void
- *Relationships:*
  - Generalization → (User is the parent of Student, Faculty, Admin)

### 2. *Student (Inherits from User)*
- Attributes:
  - +teamID: String
  - +semester: Integer
- Methods:
  - +registerProject(problemStatement: String): Boolean
  - +submitPhaseDocument(phase: Integer, document: File): Boolean
  - +submitResearchPaper(title: String, document: File): Boolean
  - +viewFeedback(): String
  - +viewProjectTimeline(): void
- *Relationships:*
  - Association → (Student belongs to 1 Team)

### 3. *Faculty (Inherits from User)*
- Attributes:
  - +designation: String
  - +maxTeamsAllowed: Integer
- Methods:
  - +trackProjectProgress(teamID: String): void
  - +reviewResearchPaper(paperID: String): Boolean
- *Relationships:*
  - Association → (Faculty mentors 0..* Teams, constrained by designation)
  - *Delegates grading and scheduling to services*

### 4. *Admin (Inherits from User)*
- Methods:
  - +manageUsers(): void
  - +configureSystemSettings(): void
  - +generateReports(): void

### 5. *Team*
- Attributes:
  - +teamID: String
  - +problemStatement: String
  - +facultyID: String
- Methods:
  - +addMember(studentID: String): Boolean
  - +removeMember(studentID: String): Boolean
  - +updateProblemStatement(newStatement: String): Boolean
- *Relationships:*
  - Composition → (Team consists of 4 Students)
  - Association → (1 Team has 1 Faculty mentor)

### 6. *Project*
- Attributes:
  - +projectID: String
  - +teamID: String
  - +domain: String
  - +startSemester: Integer
- Methods:
  - +registerProject(teamID: String, problemStatement: String): Boolean
  - +trackProjectProgress(): String
- *Relationships:*
  - Aggregation → (1 Project is associated with 1 Team)

### 7. *PhaseSubmission*
- Attributes:
  - +submissionID: String
  - +teamID: String
  - +phase: Integer
  - +document: File
  - +submissionDate: DateTime
  - +grade: Float
- Methods:
  - +submitDocument(document: File): Boolean
  - +getGrade(): Float
- *Relationships:*
  - Association → (1 Team can have 0..* PhaseSubmissions)

### 8. *PlagiarismServiceInterface (New - Interface)*
- Methods:
  - +runCheck(submissionID: String): Boolean
  - +generateReport(submissionID: String): File
  - +getSimilarityScore(submissionID: String): Float
  - +getAIGeneratedScore(submissionID: String): Float

### 9. *PlagiarismService (Implements PlagiarismServiceInterface)*
- Methods:
  - Implements all methods from PlagiarismServiceInterface
- *Relationships:*
  - Implemented by PlagiarismChecker

### 10. *PlagiarismChecker*
- Attributes:
  - +checkID: String
  - +submissionID: String
- Methods:
  - +runCheck(submissionID: String): Boolean
  - +generateReport(submissionID: String): File
- *Relationships:*
  - Uses PlagiarismServiceInterface for flexibility

### 11. *ResearchPaper*
- Attributes:
  - +paperID: String
  - +teamID: String
  - +title: String
  - +abstract: String
  - +document: File
  - +submissionDate: DateTime
  - +journalOrConference: String
  - +scopusIndexed: Boolean
  - +webOfScienceIndexed: Boolean
  - +acceptanceStatus: String
- Methods:
  - +submitPaper(teamID: String, title: String, document: File): Boolean
  - +checkIndexingStatus(): String
  - +updateAcceptanceStatus(status: String): void
- *Relationships:*
  - Dependency → (ResearchPaper submission is required for project completion)
  - Association → (PlagiarismService verifies ResearchPaper)

### 12. *ReviewSchedulingService (New)*
- Methods:
  - +scheduleReview(teamID: String, dateTime: DateTime): Boolean
  - +notifyParticipants(): void
- *Relationships:*
  - Used by Faculty instead of handling scheduling directly

### 13. *GradingService (New)*
- Methods:
  - +assignGrade(teamID: String, phase: Integer, grade: Float): void
  - +provideFeedback(teamID: String, phase: Integer, feedback: String): void
- *Relationships:*
  - Used by Faculty instead of assigning grades directly

### 14. *EvaluationStrategy (New - Interface)*
- Methods:
  - +evaluateProject(teamID: String): void
- *Relationships:*
  - Implemented by EvaluationPanel

### 15. *EvaluationPanel (Implements EvaluationStrategy)*
- Attributes:
  - +panelID: String
  - +teamID: String
  - +facultyMembers: List<Faculty>
  - +finalGrade: Float
- Methods:
  - +evaluateProject(teamID: String): void
- *Relationships:*
  - Uses EvaluationStrategy for flexible grading policies

### 16. *VersionControl*
- Attributes:
  - +versionID: String
  - +submissionID: String
  - +timestamp: DateTime
- Methods:
  - +trackChanges(submissionID: String): void
- *Relationships:*
  - Association → (1 VersionControl entry corresponds to 1 PhaseSubmission)

### 17. *GanttChart*
- Attributes:
  - +chartID: String
  - +teamID: String
- Methods:
  - +generateChart(teamID: String): Image
- *Relationships:*
  - Association → (1 GanttChart belongs to 1 Team)