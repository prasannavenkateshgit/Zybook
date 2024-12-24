--liquibase formatted sql

-- Changeset mspawan:1 Create Textbook Table
CREATE TABLE Textbook (
    uid SMALLINT  NOT NULL IDENTITY,
    title VARCHAR(30) NOT NULL,
    PRIMARY KEY (uid),
    UNIQUE (title)
);

-- Changeset mspawan:2 Create Chapter Table
CREATE TABLE Chapter (
    cno SMALLINT NOT NULL,
    chapter_code VARCHAR(10) NOT NULL,
    title VARCHAR(30) NOT NULL,
    isHidden TINYINT NOT NULL DEFAULT 0,
    tbook_id SMALLINT  NOT NULL,
    UNIQUE(title, tbook_id),
    PRIMARY KEY(cno, tbook_id),
    FOREIGN KEY (tbook_id) REFERENCES Textbook(uid)
    ON DELETE NO ACTION
    ON UPDATE CASCADE
);

-- Changeset mspawan:3 Create Section Table
CREATE TABLE Section (
    sno SMALLINT  NOT NULL,
    title VARCHAR(30) NOT NULL,
    isHidden TINYINT NOT NULL DEFAULT 0,
    tbook_id SMALLINT  NOT NULL,
    chapter_no SMALLINT  NOT NULL,
    PRIMARY KEY(chapter_no, tbook_id, sno),
    FOREIGN KEY (chapter_no, tbook_id) REFERENCES Chapter(cno, tbook_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:4 Create Course Table
CREATE TABLE Course (
    course_id VARCHAR(50),
    title VARCHAR(100) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    course_type VARCHAR(15) NOT NULL,
    textbook_id SMALLINT NOT NULL UNIQUE,
    PRIMARY KEY (course_id),
    FOREIGN KEY (textbook_id) REFERENCES Textbook(uid)
    ON DELETE NO ACTION
    ON UPDATE CASCADE
);

-- Changeset mspawan:5 Create ActiveCourse Table
CREATE TABLE ActiveCourse (
    course_token CHAR(7) NOT NULL UNIQUE,
    course_capacity SMALLINT NOT NULL,
    course_id VARCHAR(50),
    PRIMARY KEY(course_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
    ON DELETE NO ACTION
    ON UPDATE CASCADE
);

-- Changeset mspawan:6 Create User Table
CREATE TABLE [User] (
    user_id  INT IDENTITY,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50),
    role_name VARCHAR(10) CHECK (role_name IN ('admin','student','faculty','ta')),
    PRIMARY KEY(user_id)
);

-- Changeset mspawan:7 Create Notification Table
CREATE TABLE Notification (
    user_id  INT,
    course_id VARCHAR(50),
    message TEXT,
    PRIMARY KEY (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES [User](user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset mspawan:8 Create UserRegistersCourse Table
CREATE TABLE UserRegistersCourse (
    user_id INT,
    course_id VARCHAR(50),
    enrollment_date TIMESTAMP,
    approval_status VARCHAR(50) CHECK(approval_status IN ('Approved','Rejected','Waiting')),
    PRIMARY KEY (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES [User](user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset mspawan:9 Create Permission Table
CREATE TABLE Permission (
    permission_id SMALLINT  IDENTITY,
    permission CHAR(10) NOT NULL CHECK(permission IN ('CREATE','READ','UPDATE','DELETE')),
    PRIMARY KEY(permission_id)
);

-- Changeset mspawan:10 Create Resource Table
CREATE TABLE Resource (
    resource_id SMALLINT  IDENTITY,
    resource CHAR(20) CHECK (resource IN ('Course','Textbook','Chapter','Section','Content','Activity')),
    PRIMARY KEY(resource_id)
);

-- Changeset mspawan:11 Create User_Resource_Permission Table
CREATE TABLE User_Resource_Permission (
    user_id INT  NOT NULL,
    permission_id SMALLINT  NOT NULL,
    resource_id SMALLINT  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES [User](user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permission(permission_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES Resource(resource_id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY(user_id, permission_id, resource_id)
);

-- Changeset mspawan:12 Create Assigned Table
CREATE TABLE Assigned (
    course_id VARCHAR(50) NOT NULL,
    user_id INT  NOT NULL,
    PRIMARY KEY(course_id, user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES [User](user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Changeset mspawan:13 Create Teaches Table
CREATE TABLE Teaches (
    course_id VARCHAR(50) NOT NULL,
    user_id INT  NOT NULL,
    PRIMARY KEY (course_id, user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES [User](user_id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Changeset mspawan:15 Create Content Table
CREATE TABLE Content (
    content_id SMALLINT  NOT NULL,
    s_id SMALLINT  NOT NULL,
    c_id SMALLINT  NOT NULL,
    t_id SMALLINT  NOT NULL,
    is_hidden TINYINT DEFAULT 0,
    owned_by VARCHAR(15) DEFAULT 'faculty',
    PRIMARY KEY (content_id, s_id, c_id, t_id),
    CHECK (owned_by IN ('faculty', 'ta')),
    FOREIGN KEY (c_id, t_id, s_id) REFERENCES Section (chapter_no, tbook_id, sno)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:16 Create TextContent Table
CREATE TABLE TextContent (
    content_id SMALLINT  NOT NULL,
    s_id SMALLINT  NOT NULL,
    c_id SMALLINT  NOT NULL,
    t_id SMALLINT  NOT NULL,
    data TEXT NOT NULL,
    PRIMARY KEY  (content_id, s_id, c_id, t_id),
    FOREIGN KEY (content_id, s_id, c_id, t_id) REFERENCES Content(content_id, s_id, c_id, t_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:17 Create ImageContent Table
CREATE TABLE ImageContent (
    content_id SMALLINT  NOT NULL,
    s_id SMALLINT  NOT NULL,
    c_id SMALLINT  NOT NULL,
    t_id SMALLINT  NOT NULL,
    data VARBINARY NOT NULL,
    PRIMARY KEY  (content_id, s_id, c_id, t_id),
    FOREIGN KEY (content_id, s_id, c_id, t_id) REFERENCES Content(content_id, s_id, c_id, t_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:18 Create Activity Table
CREATE TABLE Activity (
    activity_id SMALLINT  NOT NULL,
    content_id SMALLINT  NOT NULL,
    answer TINYINT NOT NULL,
    s_id SMALLINT  NOT NULL,
    c_id SMALLINT  NOT NULL,
    t_id SMALLINT  NOT NULL,
    PRIMARY KEY (activity_id, content_id, s_id, c_id, t_id),
    FOREIGN KEY (content_id, s_id, c_id, t_id) REFERENCES Content(content_id, s_id, c_id, t_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:19 Create Answer Table
CREATE TABLE Answer (
    answer_id SMALLINT  NOT NULL,
    activity_id SMALLINT  NOT NULL,
    content_id SMALLINT  NOT NULL,
    s_id SMALLINT  NOT NULL,
    c_id SMALLINT  NOT NULL,
    t_id SMALLINT  NOT NULL,
    answer_text TEXT NOT NULL,
    justification TEXT NOT NULL,
    PRIMARY KEY (answer_id, activity_id, content_id, s_id, c_id, t_id),
    FOREIGN KEY (activity_id, content_id, s_id, c_id, t_id) REFERENCES Activity (activity_id, content_id, s_id, c_id, t_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Changeset mspawan:20 Trigger to create chater number
CREATE TRIGGER before_insert_chapter
ON Chapter
INSTEAD OF INSERT
AS
BEGIN
    INSERT INTO Chapter (chapter_code, cno)
    SELECT CONCAT('chap', cno), cno FROM inserted;
END;
