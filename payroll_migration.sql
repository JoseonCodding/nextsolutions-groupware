-- 급여 모듈 DB 마이그레이션
-- 실행: mysql -u team2_user -p1234 team2_db < payroll_migration.sql

USE team2_db;

-- 직원 기본급 설정 테이블
CREATE TABLE IF NOT EXISTS employee_salary (
    salary_id       INT PRIMARY KEY AUTO_INCREMENT,
    employee_id     VARCHAR(20) NOT NULL,
    company_id      INT NOT NULL,
    base_salary     BIGINT NOT NULL DEFAULT 0,
    meal_allowance  INT NOT NULL DEFAULT 100000,
    transport_allowance INT NOT NULL DEFAULT 50000,
    effective_from  DATE NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_emp_sal (employee_id, effective_from),
    FOREIGN KEY (employee_id) REFERENCES employee(employeeId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 월별 급여명세서 테이블
CREATE TABLE IF NOT EXISTS payroll (
    payroll_id          INT PRIMARY KEY AUTO_INCREMENT,
    employee_id         VARCHAR(20) NOT NULL,
    company_id          INT NOT NULL,
    year_month          VARCHAR(7) NOT NULL,
    -- 지급 항목
    base_salary         BIGINT NOT NULL DEFAULT 0,
    meal_allowance      BIGINT NOT NULL DEFAULT 0,
    transport_allowance BIGINT NOT NULL DEFAULT 0,
    overtime_pay        BIGINT NOT NULL DEFAULT 0,
    total_pay           BIGINT NOT NULL DEFAULT 0,
    -- 공제 항목
    deduct_pension      BIGINT NOT NULL DEFAULT 0,
    deduct_health       BIGINT NOT NULL DEFAULT 0,
    deduct_care         BIGINT NOT NULL DEFAULT 0,
    deduct_employ       BIGINT NOT NULL DEFAULT 0,
    deduct_tax          BIGINT NOT NULL DEFAULT 0,
    deduct_local_tax    BIGINT NOT NULL DEFAULT 0,
    total_deduct        BIGINT NOT NULL DEFAULT 0,
    -- 실수령
    net_pay             BIGINT NOT NULL DEFAULT 0,
    -- 근태
    work_days           INT NOT NULL DEFAULT 0,
    absence_days        INT NOT NULL DEFAULT 0,
    -- 상태
    status              VARCHAR(20) NOT NULL DEFAULT '대기',
    note                VARCHAR(500),
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_payroll (employee_id, year_month)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
