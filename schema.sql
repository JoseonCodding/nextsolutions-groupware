-- NextSolutions 그룹웨어 DB 스키마
-- MySQL 9.x 기준
-- 실행: mysql -u root -p team2_db < schema.sql

USE team2_db;

-- 1. 직원 테이블
CREATE TABLE IF NOT EXISTS employee (
    emp_seq INT PRIMARY KEY AUTO_INCREMENT,
    employeeId VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    emp_nm VARCHAR(50) NOT NULL,
    deptName VARCHAR(100),
    position VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    active TINYINT(1) DEFAULT 1,
    phone VARCHAR(20),
    birth DATE,
    hireDate DATE,
    resignDate DATE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 게시판 테이블
CREATE TABLE IF NOT EXISTS board_board (
    board_id INT PRIMARY KEY AUTO_INCREMENT,
    board_name VARCHAR(100) NOT NULL,
    board_type VARCHAR(50),
    access_role VARCHAR(100),
    use_comment TINYINT(1) DEFAULT 1,
    use_like TINYINT(1) DEFAULT 1,
    is_active TINYINT(1) DEFAULT 1,
    is_deleted TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 게시글 테이블
CREATE TABLE IF NOT EXISTS board_post (
    post_id INT PRIMARY KEY AUTO_INCREMENT,
    board_id INT NOT NULL,
    employeeId VARCHAR(20),
    title VARCHAR(255) NOT NULL,
    content LONGTEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    is_deleted TINYINT(1) DEFAULT 0,
    status VARCHAR(50),
    docType VARCHAR(50),
    FOREIGN KEY (board_id) REFERENCES board_board(board_id),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 좋아요 테이블
CREATE TABLE IF NOT EXISTS board_like (
    like_id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    employeeId VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_emp (post_id, employeeId),
    FOREIGN KEY (post_id) REFERENCES board_post(post_id),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. 댓글 테이블
CREATE TABLE IF NOT EXISTS board_comment (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    employeeId VARCHAR(20) NOT NULL,
    parent_comment_id BIGINT DEFAULT NULL,
    content LONGTEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (post_id) REFERENCES board_post(post_id),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. 게시글 조회 기록
CREATE TABLE IF NOT EXISTS board_post_view (
    post_id INT NOT NULL,
    employeeId VARCHAR(20) NOT NULL,
    viewed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, employeeId),
    FOREIGN KEY (post_id) REFERENCES board_post(post_id),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 7. 게시판 조회수 통계
CREATE TABLE IF NOT EXISTS board_view_stats (
    board_id INT NOT NULL,
    view_date DATE NOT NULL,
    view_count INT DEFAULT 0,
    board_name VARCHAR(100),
    PRIMARY KEY (board_id, view_date),
    FOREIGN KEY (board_id) REFERENCES board_board(board_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 8. 출퇴근 테이블
CREATE TABLE IF NOT EXISTS attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employeeId VARCHAR(20) NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    modified_by VARCHAR(20),
    modified_at DATETIME,
    modification_reason TEXT,
    state_type VARCHAR(50),
    status VARCHAR(50),
    approval_date DATETIME,
    time_inout VARCHAR(20),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 9. 연차 테이블
CREATE TABLE IF NOT EXISTS annual_leave (
    leave_id INT PRIMARY KEY AUTO_INCREMENT,
    employeeId VARCHAR(20) NOT NULL,
    leave_type VARCHAR(50),
    state_type VARCHAR(50) DEFAULT '대기',
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    used_date DATE,
    used_reason TEXT,
    approval_date DATETIME,
    firstSign VARCHAR(20),
    secondSign VARCHAR(20),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 10. 일정 테이블
CREATE TABLE IF NOT EXISTS schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    cate VARCHAR(50),
    start_date DATE,
    start_time VARCHAR(10),
    end_date DATE,
    end_time VARCHAR(10),
    repeat_check INT DEFAULT 0,
    holiday VARCHAR(50),
    alarm VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    employeeId VARCHAR(20),
    is_sent INT DEFAULT 0,
    sent_at DATETIME,
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 11. 프로젝트 테이블
CREATE TABLE IF NOT EXISTS TB_PJT_BASC (
    PJT_SN INT PRIMARY KEY AUTO_INCREMENT,
    gid VARCHAR(100) NOT NULL,
    ver INT NOT NULL DEFAULT 1,
    PJT_NM VARCHAR(255) NOT NULL,
    PJT_STTS_CD VARCHAR(50) DEFAULT '대기',
    PJT_BGNG_DT DATE,
    PJT_END_DT DATE,
    employeeId VARCHAR(20),
    TB_PJT_APR VARCHAR(20),
    content LONGTEXT,
    FRST_REG_DT DATETIME DEFAULT CURRENT_TIMESTAMP,
    LAST_MDFCN_DT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    LAST_MDFR_ID VARCHAR(20),
    FRST_RGTR_ID VARCHAR(20),
    USE_YN VARCHAR(1) DEFAULT 'Y',
    ATCH_FILE_SN1 VARCHAR(255),
    ORG_FILE_NM1 VARCHAR(255),
    ATCH_FILE_SN2 VARCHAR(255),
    ORG_FILE_NM2 VARCHAR(255),
    ATCH_FILE_SN3 VARCHAR(255),
    ORG_FILE_NM3 VARCHAR(255),
    docType VARCHAR(50),
    TB_PJT_BASCcol VARCHAR(255),
    TB_PJT_BASCcol1 VARCHAR(255),
    firstSign VARCHAR(20),
    secondSign VARCHAR(20),
    approvedBy VARCHAR(20),
    UNIQUE KEY uk_gid_ver (gid, ver),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 12. 프로젝트 뷰
CREATE OR REPLACE VIEW VIEW_PJT_BASC AS
SELECT * FROM TB_PJT_BASC
WHERE (USE_YN IS NULL OR USE_YN = 'Y');

-- 13. 전자결재 테이블
CREATE TABLE IF NOT EXISTS approval (
    approval_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT,
    employeeId VARCHAR(20),
    approval_type VARCHAR(50),
    status VARCHAR(50) DEFAULT '대기',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    firstSign VARCHAR(20),
    secondSign VARCHAR(20),
    approvedBy VARCHAR(20),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 14. 결재자 테이블
CREATE TABLE IF NOT EXISTS approver (
    approver_id INT PRIMARY KEY AUTO_INCREMENT,
    approval_id INT NOT NULL,
    employeeId VARCHAR(20),
    approval_order INT,
    status VARCHAR(50) DEFAULT '대기',
    approved_at DATETIME,
    FOREIGN KEY (approval_id) REFERENCES approval(approval_id),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 15. 문서 테이블
CREATE TABLE IF NOT EXISTS document (
    doc_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT,
    employeeId VARCHAR(20),
    file_path VARCHAR(500),
    org_file_nm VARCHAR(255),
    version INT DEFAULT 1,
    gid VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =============================================
-- 기본 데이터 삽입
-- =============================================

-- 관리자 계정 (사번: 00000001, 비밀번호: 1234)
INSERT INTO employee (employeeId, password, emp_nm, deptName, position, role, active)
VALUES ('00000001', '1234', '관리자', '경영지원팀', '대표', 'ADMIN', 1);

-- 기본 게시판 3개 생성
INSERT INTO board_board (board_name, board_type, use_comment, use_like, is_active)
VALUES
('공지사항', 'notice', 0, 0, 1),
('자유게시판', 'free', 1, 1, 1),
('커스텀게시판', 'custom', 1, 1, 1);
