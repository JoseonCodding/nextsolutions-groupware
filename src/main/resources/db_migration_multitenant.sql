-- ============================================================
-- 멀티테넌트 마이그레이션: company 테이블 생성 + 기존 테이블에 company_id 추가
-- 실행 순서: 1) company 테이블 생성 → 2) 기본 회사 데이터 INSERT → 3) ALTER TABLE
-- ============================================================

-- 1. 회사 테이블 생성
CREATE TABLE IF NOT EXISTS company (
    company_id   INT PRIMARY KEY AUTO_INCREMENT,
    company_nm   VARCHAR(100) NOT NULL,
    plan         VARCHAR(20)  DEFAULT 'FREE',
    owner_email  VARCHAR(100),
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- 2. 기본 회사 데이터 (기존 데이터 보존용 - company_id = 1)
INSERT INTO company (company_nm, plan, owner_email)
VALUES ('NextSolutions', 'PRO', 'admin@nextsolutions.com');

-- 3. 기존 테이블에 company_id 컬럼 추가
ALTER TABLE employee     ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE approval     ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE document     ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE schedule     ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE attendance   ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE annual_leave ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE TB_PJT_BASC  ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE board_board  ADD COLUMN company_id INT DEFAULT 1;
ALTER TABLE board_post   ADD COLUMN company_id INT DEFAULT 1;

-- 4. 기존 데이터 전부 company_id = 1 로 업데이트 (이미 DEFAULT 1 이지만 명시)
UPDATE employee     SET company_id = 1 WHERE company_id IS NULL;
UPDATE approval     SET company_id = 1 WHERE company_id IS NULL;
UPDATE document     SET company_id = 1 WHERE company_id IS NULL;
UPDATE schedule     SET company_id = 1 WHERE company_id IS NULL;
UPDATE attendance   SET company_id = 1 WHERE company_id IS NULL;
UPDATE annual_leave SET company_id = 1 WHERE company_id IS NULL;
UPDATE TB_PJT_BASC  SET company_id = 1 WHERE company_id IS NULL;
UPDATE board_board  SET company_id = 1 WHERE company_id IS NULL;
UPDATE board_post   SET company_id = 1 WHERE company_id IS NULL;

-- 5. NOT NULL 제약 추가 (데이터 업데이트 후)
ALTER TABLE employee     MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE approval     MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE document     MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE schedule     MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE attendance   MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE annual_leave MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE TB_PJT_BASC  MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE board_board  MODIFY COLUMN company_id INT NOT NULL;
ALTER TABLE board_post   MODIFY COLUMN company_id INT NOT NULL;

-- 6. 외래키 설정 (선택사항 - 성능상 생략 가능)
-- ALTER TABLE employee ADD CONSTRAINT fk_emp_company FOREIGN KEY (company_id) REFERENCES company(company_id);
