-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.28-MariaDB-log - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for dns
CREATE DATABASE IF NOT EXISTS `dns` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `dns`;

-- Dumping structure for table dns.access_log
CREATE TABLE IF NOT EXISTS `access_log` (
  `id` int(11) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `access_time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table dns.access_log: ~10 rows (approximately)
DELETE FROM `access_log`;
INSERT INTO `access_log` (`id`, `user_name`, `ip_address`, `domain`, `access_time`) VALUES
	(1, 'cham', '127.0.0.1', 'http://localhost', '2024-11-06 14:54:04'),
	(2, 'ngoc', '127.0.0.1', 'http://github.com', '2024-11-06 14:54:27'),
	(3, 'cham', '127.0.0.1', 'http://localhost', '2024-11-06 14:59:25'),
	(5, 'cham', '127.0.0.1', 'http://localhost', '2024-11-06 15:21:09'),
	(6, 'cham', '127.0.0.1', 'http://google.com', '2024-11-06 16:26:24'),
	(7, 'ngoc', '127.0.0.1', 'http://localhost', '2024-11-06 16:26:48'),
	(8, 'dieu', '127.0.0.1', 'http://github.com', '2024-11-06 16:27:16'),
	(9, 'chau', '127.0.0.1', 'http://youtube.com', '2024-11-06 16:31:31'),
	(10, 'chau', '127.0.0.1', 'http://youtube.com', '2024-11-06 16:31:44'),
	(11, 'chau', '127.0.0.1', 'http://meomeo.net', '2024-11-06 16:31:57');

-- Dumping structure for table dns.dns_records
CREATE TABLE IF NOT EXISTS `dns_records` (
  `id` int(11) NOT NULL,
  `domain` varchar(255) NOT NULL,
  `ip_address` varchar(15) NOT NULL,
  `record_type` enum('A','AAAA','CNAME','MX') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table dns.dns_records: ~10 rows (approximately)
DELETE FROM `dns_records`;
INSERT INTO `dns_records` (`id`, `domain`, `ip_address`, `record_type`) VALUES
	(1, 'google.com', '142.250.76.14', 'A'),
	(2, 'localhost', '127.0.0.1', 'A'),
	(3, 'youtube.com', '142.250.198.46', 'A'),
	(4, 'xu.edu', '69.167.157.13', 'A'),
	(5, 'xi.net', '209.126.235.60', 'A'),
	(6, 'lb-140-82-112-3-iad.github.com', '140.82.112.3', 'A'),
	(7, 'facebook.com', '163.70.159.35', 'A'),
	(8, 'github.com', '20.205.243.166', 'A'),
	(9, 'meomeo.net', '3.19.116.195', 'A'),
	(10, 'telepathy.com', '67.227.241.203', 'A');

-- Dumping structure for table dns.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table dns.users: ~4 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `username`, `password`) VALUES
	(1, 'cham', '123'),
	(2, 'ngoc', '000'),
	(3, 'dieu', '111'),
	(4, 'chau', '222');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
