--liquibase formatted sql


-- Changeset vengatesh:1 Create E_TEXTBOOK Table
CREATE TABLE E_Textbook (
    uid SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(60) NOT NULL,
    PRIMARY KEY (uid),
    UNIQUE (title)
);

-- Changeset vengatesh:2 Create Chapter Table
CREATE TABLE Chapter (
    cno SMALLINT UNSIGNED NOT NULL,
    chapter_code VARCHAR(10) NOT NULL,
    title VARCHAR(60) NOT NULL,
    isHidden TINYINT(1) NOT NULL DEFAULT 0,
    tbook_id SMALLINT UNSIGNED NOT NULL,
    UNIQUE(title, tbook_id),
    PRIMARY KEY(cno, tbook_id),
    FOREIGN KEY (tbook_id) REFERENCES E_Textbook(uid)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Changeset vengatesh:3 Create Section Table
CREATE TABLE Section (
     sno SMALLINT UNSIGNED NOT NULL,
     title VARCHAR(30) NOT NULL,
     isHidden TINYINT(1) NOT NULL DEFAULT 0,
     tbook_id SMALLINT UNSIGNED NOT NULL,
     chapter_no SMALLINT UNSIGNED NOT NULL,
     PRIMARY KEY(tbook_id, chapter_no, sno),
     FOREIGN KEY (chapter_no, tbook_id) REFERENCES Chapter(cno, tbook_id)
         ON DELETE CASCADE
         ON UPDATE CASCADE
);
-- Changeset vengatesh:4 Create User Table
CREATE TABLE User(
                     user_id INT UNSIGNED AUTO_INCREMENT,
                     fname VARCHAR(50) NOT NULL,
                     lname VARCHAR(50) NOT NULL,
                     email VARCHAR(50) UNIQUE NOT NULL,
                     password VARCHAR(200),
                     role_name VARCHAR(10) CHECK (role_name IN ('admin', 'student', 'faculty', 'ta')),
                     PRIMARY KEY (user_id)
);

-- Changeset vengatesh:5 Create Course Table
CREATE TABLE Course (
    course_id VARCHAR(50),
    title VARCHAR(100) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    course_type VARCHAR(15) NOT NULL DEFAULT 'Evaluation',
    textbook_id SMALLINT UNSIGNED NOT NULL UNIQUE,
    professor_id INT UNSIGNED,
    PRIMARY KEY (course_id),
    FOREIGN KEY (textbook_id) REFERENCES E_Textbook(uid)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (professor_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (course_type IN ('Active', 'Evaluation'))
);



-- Changeset vengatesh:6 Create ActiveCourse Table
CREATE TABLE ActiveCourse(
     course_token CHAR(7) NOT NULL UNIQUE,
     course_capacity SMALLINT NOT NULL,
     course_id VARCHAR(50),
     PRIMARY KEY (course_id),
     FOREIGN KEY (course_id) REFERENCES Course(course_id)
         ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Changeset vengatesh:7 Create Notification Table
CREATE TABLE Notification(
     user_id INT UNSIGNED,
     CourseID VARCHAR(50),
     message TEXT,
     PRIMARY KEY (user_id, CourseID),
     FOREIGN KEY (user_id) REFERENCES User(user_id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (CourseID) REFERENCES Course(course_id)
         ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset vengatesh:8 Create UserRegistersCourse Table
CREATE TABLE UserRegistersCourse(
    user_id INT UNSIGNED,
    CourseID VARCHAR(50),
    enrollment_date TIMESTAMP,
    approval_status VARCHAR(50)
        CHECK (approval_status IN ('Approved', 'Rejected', 'Waiting')),
    PRIMARY KEY (user_id, CourseID),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (CourseID) REFERENCES Course(course_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset vengatesh:9 Create Permission Table
CREATE TABLE Permission(
   permission_id SMALLINT UNSIGNED AUTO_INCREMENT,
   permission CHAR(10) NOT NULL
       CHECK (permission IN ('CREATE', 'READ', 'UPDATE', 'DELETE')),
   PRIMARY KEY (permission_id)
);

-- Changeset vengatesh:10 Create Resource Table
CREATE TABLE Resource(
     resource_id SMALLINT UNSIGNED AUTO_INCREMENT,
     resource CHAR(20) CHECK (resource IN ('Course', 'Textbook', 'Chapter', 'Section', 'Content', 'Activity')),
     PRIMARY KEY (resource_id)
);

-- Changeset vengatesh:11 Create User_Resource_Permission Table
CREATE TABLE User_Resource_Permission(
     user_id INT UNSIGNED NOT NULL,
     permission_id SMALLINT UNSIGNED NOT NULL,
     resource_id SMALLINT UNSIGNED NOT NULL,
     PRIMARY KEY (user_id, permission_id, resource_id),
     FOREIGN KEY (user_id) REFERENCES User(user_id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (permission_id) REFERENCES Permission(permission_id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (resource_id) REFERENCES Resource(resource_id)
         ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset vengatesh:12 Create Assigned Table
CREATE TABLE Assigned(
     course_id VARCHAR(50) NOT NULL,
     user_id INT UNSIGNED NOT NULL,
     PRIMARY KEY (course_id, user_id),
     FOREIGN KEY (course_id) REFERENCES Course(course_id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (user_id) REFERENCES User(user_id)
         ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset vengatesh:13 Create Trigger before_insert_chapter
CREATE TRIGGER before_insert_chapter
    BEFORE INSERT ON Chapter
    FOR EACH ROW
BEGIN
    SET NEW.chapter_code = CONCAT('chap', NEW.cno);
END;

-- Changeset vengatesh:14 Create Content Table
    CREATE TABLE Content (
     content_id SMALLINT UNSIGNED NOT NULL,
     s_id SMALLINT UNSIGNED NOT NULL,
     c_id SMALLINT UNSIGNED NOT NULL,
     t_id SMALLINT UNSIGNED NOT NULL,
     is_hidden TINYINT DEFAULT 0,
     content_type CHAR(10) CHECK (content_type IN ('text','image','activity')),
     owned_by VARCHAR(15) DEFAULT 'admin'
         CHECK (owned_by IN ('faculty', 'ta','admin')),
     PRIMARY KEY (content_id, s_id, c_id, t_id),
     FOREIGN KEY (t_id, c_id, s_id) REFERENCES Section(tbook_id, chapter_no, sno)
         ON DELETE CASCADE ON UPDATE CASCADE
    );

-- Changeset vengatesh:15 Create TextContent Table
CREATE TABLE TextContent (
     content_id SMALLINT UNSIGNED NOT NULL,
     s_id SMALLINT UNSIGNED NOT NULL,
     c_id SMALLINT UNSIGNED NOT NULL,
     t_id SMALLINT UNSIGNED NOT NULL,
     data TEXT NOT NULL,
     PRIMARY KEY (content_id, s_id, c_id, t_id),
     FOREIGN KEY (content_id, s_id, c_id, t_id)
         REFERENCES Content(content_id, s_id, c_id, t_id)
         ON DELETE CASCADE ON UPDATE CASCADE
    );

-- Changeset vengatesh:16 Create ImageContent Table
CREATE TABLE ImageContent (
  content_id SMALLINT UNSIGNED NOT NULL,
  s_id SMALLINT UNSIGNED NOT NULL,
  c_id SMALLINT UNSIGNED NOT NULL,
  t_id SMALLINT UNSIGNED NOT NULL,
  data MEDIUMBLOB NOT NULL,
  PRIMARY KEY (content_id, s_id, c_id, t_id),
  FOREIGN KEY (content_id, s_id, c_id, t_id)
      REFERENCES Content(content_id, s_id, c_id, t_id)
      ON DELETE CASCADE ON UPDATE CASCADE
    );

-- Changeset vengatesh:17 Create Activity Table
CREATE TABLE Activity (
  activity_id SMALLINT UNSIGNED NOT NULL,
  content_id SMALLINT UNSIGNED NOT NULL,
  s_id SMALLINT UNSIGNED NOT NULL,
  c_id SMALLINT UNSIGNED NOT NULL,
  t_id SMALLINT UNSIGNED NOT NULL,
  isHidden TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (activity_id, content_id, s_id, c_id, t_id),
  FOREIGN KEY (content_id, s_id, c_id, t_id)
      REFERENCES Content(content_id, s_id, c_id, t_id)
      ON DELETE CASCADE ON UPDATE CASCADE
    );



-- Changeset vengatesh:18 Creating Question Table
CREATE TABLE Question(
 activity_id SMALLINT UNSIGNED NOT NULL,
 content_id SMALLINT UNSIGNED NOT NULL,
 s_id SMALLINT UNSIGNED NOT NULL,
 c_id SMALLINT UNSIGNED NOT NULL,
 t_id SMALLINT UNSIGNED NOT NULL,
 q_id SMALLINT UNSIGNED NOT NULL,
 ans_id SMALLINT UNSIGNED,
 question TEXT NOT NULL,
 PRIMARY KEY (activity_id, content_id, s_id, c_id, t_id,q_id),
 FOREIGN KEY (activity_id,content_id, s_id, c_id, t_id)
     REFERENCES Activity(activity_id,content_id, s_id, c_id, t_id)
     ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset vengatesh:19 Create Answer Table
CREATE TABLE Answer (
    question_id SMALLINT UNSIGNED NOT NULL,
    answer_id SMALLINT UNSIGNED NOT NULL,
    activity_id SMALLINT UNSIGNED NOT NULL,
    content_id SMALLINT UNSIGNED NOT NULL,
    s_id SMALLINT UNSIGNED NOT NULL,
    c_id SMALLINT UNSIGNED NOT NULL,
    t_id SMALLINT UNSIGNED NOT NULL,
    answer_text TEXT NOT NULL,
    justification TEXT NOT NULL,
    PRIMARY KEY (question_id, answer_id, activity_id, content_id, s_id, c_id, t_id),
    FOREIGN KEY (activity_id, content_id, s_id, c_id, t_id, question_id)
        REFERENCES Question(activity_id, content_id, s_id, c_id, t_id, q_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

--Changeset vengatesh:20 Create Participation Table
CREATE Table User_Participation(
    user_id INT UNSIGNED NOT NULL,
    activity_id SMALLINT UNSIGNED NOT NULL,
    content_id SMALLINT UNSIGNED NOT NULL,
    s_id SMALLINT UNSIGNED NOT NULL,
    c_id SMALLINT UNSIGNED NOT NULL,
    t_id SMALLINT UNSIGNED NOT NULL,
    q_id SMALLINT UNSIGNED NOT NULL,
    ans_id SMALLINT UNSIGNED,
    score SMALLINT UNSIGNED,
    PRIMARY KEY (user_id,activity_id,content_id,s_id,c_id,t_id,q_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (activity_id,content_id,s_id,c_id,t_id,q_id) REFERENCES Question(activity_id,content_id,s_id,c_id,t_id,q_id) ON UPDATE CASCADE ON DELETE CASCADE
);

