# ooad-project
## capstone management system

### Problem Statement: 
The Capstone Project Management System is a digital platform designed to streamline the end-to-end management of capstone projects. It enables students to register their projects, submit phase-wise documentation, and track progress, while allowing faculty to review submissions, provide feedback, and schedule evaluations. With built-in version control, scheduled review slots, and structured grading, the system ensures an organized and efficient project workflow.
Each student team consists of exactly four members, ensuring balanced workload distribution and accountability. Students first form a team of four and then approach a faculty guide of their choice. Each team is assigned one faculty guide, who can be a Professor, Associate Professor, or Assistant Professor. The faculty guide is responsible for reviewing progress, providing feedback, and evaluating submissions. The number of teams a faculty guide can mentor depends on their designation:

Professor: Can mentor up to 7 teams
Associate Professor: Can mentor up to 5 teams
Assistant Professor: Can mentor up to 3 teams
To maintain project diversity and encourage innovation, each team must select a unique problem statement—no two teams can work on the same topic within the same academic session. Additionally, the university provides predefined domains for project selection, including: Cybersecurity, Machine Learning, Cloud Computing, IoT and Digital Twin.
Once the problem statement is approved, the system allows teams to register their project, manage phase-wise submissions, ensuring all necessary details are recorded. Faculty mentors have access to all submitted documents, allowing them to provide timely feedback and assign phase-wise grades. Each submission undergoes plagiarism and AI content checks to ensure originality and authenticity.
A key aspect of capstone projects is structured evaluations and reviews. Admins are responsible for scheduling review slots, ensuring an organised and conflict-free process. Automated reminders notify both students and faculty about upcoming reviews.
During evaluations, in addition to the faculty guide, a panel of members assesses the team’s performance. The final grade is awarded jointly by the guide and the panel, ensuring a fair and comprehensive assessment. However, the finalised grade is uploaded solely by the faculty.
In the final phase, students must submit a research paper to a Scopus-indexed or Web of Science-indexed journal or conference, which is a critical requirement for project completion.
To maintain security, transparency, and accountability, the system enforces strict role-based access control. Students can only manage their project-related submissions, faculty members oversee only their assigned teams, and administrators handle system-wide management. Additionally, students cannot switch teams once a project is registered, ensuring commitment to their assigned project. Faculty members cannot evaluate projects they are not mentoring, preventing conflicts of interest.
The Capstone Project Management System provides a structured approach to capstone project management by integrating project tracking, document submissions, faculty evaluations, and research paper submissions. By ensuring clear communication and streamlined processes, the system enhances collaboration between students and faculty, making the entire capstone experience more efficient and well-organised.

### before running:
- update applications.properties with your own mongodb atlas uri
- create the appropriate db and collections (users) on mongodb
- update credentials.json with your own api key from google cloud for storing files in gdrive
