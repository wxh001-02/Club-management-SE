CREATE DATABASE IF NOT EXISTS club_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE club_db;

DROP TABLE IF EXISTS club_member;
DROP TABLE IF EXISTS club;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE club (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    logo VARCHAR(255),
    description TEXT,
    created_date DATE NOT NULL,
    status INT NOT NULL DEFAULT 1 COMMENT '1: 正常, 0: 禁用',
    president_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_president_id (president_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE club_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 0 COMMENT '0: 待审核, 1: 已通过, -1: 已拒绝',
    join_date DATE NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_club_id (club_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_club_user (club_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN'),
('president1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'PRESIDENT'),
('member1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'MEMBER');

INSERT INTO club (name, logo, description, created_date, status, president_id) VALUES
('计算机协会', 'https://example.com/computer.jpg', '计算机协会是一个致力于推广计算机技术和编程文化的学术性社团。', '2024-01-15', 1, 2),
('摄影协会', 'https://example.com/photo.jpg', '摄影协会聚集了一群热爱摄影的同学，共同探索光影艺术的魅力。', '2024-02-20', 1, 2);

INSERT INTO club_member (club_id, user_id, status, join_date) VALUES
(1, 2, 1, '2024-01-15'),
(2, 2, 1, '2024-02-20'),
(1, 3, 1, '2024-03-01');
