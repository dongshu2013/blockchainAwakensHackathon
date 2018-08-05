/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `walletalarm`;


# Dump of table portfolio
# ------------------------------------------------------------

DROP TABLE IF EXISTS `portfolio`;

CREATE TABLE `portfolio` (
  `portfolio_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`portfolio_id`),
  UNIQUE KEY `portfolio_address_user_UNIQUE` (`name`,`user_id`),
  KEY `portfolio_user_id` (`user_id`),
  CONSTRAINT `portfolio_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table wallet
# ------------------------------------------------------------

DROP TABLE IF EXISTS `wallet`;

CREATE TABLE `wallet` (
  `wallet_id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `blockchain_type` enum('ETH') COLLATE utf8_unicode_ci DEFAULT 'ETH',
  `portfolio_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `wallet_address_user_UNIQUE` (`address`,`user_id`),
  KEY `wallet_user_id` (`user_id`),
  KEY `wallet_portfolio_id` (`portfolio_id`),
  CONSTRAINT `wallet_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `wallet_portfolio_id` FOREIGN KEY (`portfolio_id`) REFERENCES `portfolio` (`portfolio_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table contract
# ------------------------------------------------------------

DROP TABLE IF EXISTS `contract`;

CREATE TABLE `contract` (
  `contract_id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `symbol` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `decimals` int(11) DEFAULT 0,
  `blockchain_type` enum('ETH') COLLATE utf8_unicode_ci DEFAULT 'ETH',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `contract_address_UNIQUE` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table block_batch
# ------------------------------------------------------------

DROP TABLE IF EXISTS `block_batch`;

CREATE TABLE `block_batch` (
  `block_batch_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_block_number` bigint(20) NOT NULL,
  `end_block_number` bigint(20) NOT NULL,
  `scan_status` enum('OPEN', 'PROCESSING', 'CLOSED') COLLATE utf8_unicode_ci DEFAULT 'OPEN',
  `notify_status` enum('OPEN', 'PROCESSING', 'CLOSED') COLLATE utf8_unicode_ci DEFAULT 'OPEN',
  `scan_server` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `notify_server` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `scan_start_time` datetime DEFAULT NULL,
  `scan_end_time` datetime DEFAULT NULL,
  `scan_reset_time` datetime DEFAULT NULL,
  `notify_start_time` datetime DEFAULT NULL,
  `notify_end_time` datetime DEFAULT NULL,
  `notify_reset_time` datetime DEFAULT NULL,
  `total_transactions` int(11) DEFAULT 0,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`block_batch_id`),
  UNIQUE KEY `block_batch_UNIQUE` (`start_block_number`,`end_block_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table transaction
# ------------------------------------------------------------

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `transaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `hash` varchar(66) COLLATE utf8_unicode_ci NOT NULL,
  `block_batch_id` bigint(20) DEFAULT NULL,
  `blockchain_type` enum('ETH') COLLATE utf8_unicode_ci DEFAULT 'ETH',
  `matching_address` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `from` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `to` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `status` enum('SUCCESS','FAIL') COLLATE utf8_unicode_ci DEFAULT 'SUCCESS',
  `time` datetime NOT NULL,
  `fee` decimal(27,18) NOT NULL,
  `value` decimal(27,18) NOT NULL,
  `contract_id` int(11) DEFAULT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `symbol` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `note` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `transaction_hash_UNIQUE` (`hash`),
  KEY `transaction_block_batch_id` (`block_batch_id`),
  CONSTRAINT `transaction_block_batch_id` FOREIGN KEY (`block_batch_id`) REFERENCES `block_batch` (`block_batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  KEY `transaction_contract_id` (`contract_id`),
  CONSTRAINT `transaction_contract_id` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table wallet_transaction
# ------------------------------------------------------------

DROP TABLE IF EXISTS `wallet_transaction`;

CREATE TABLE `wallet_transaction` (
  `wallet_transaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `hash` varchar(66) COLLATE utf8_unicode_ci NOT NULL,
  `blockchain_type` enum('ETH') COLLATE utf8_unicode_ci DEFAULT 'ETH',
  `from` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `to` varchar(42) COLLATE utf8_unicode_ci NOT NULL,
  `status` enum('SUCCESS','FAIL') COLLATE utf8_unicode_ci DEFAULT 'SUCCESS',
  `time` datetime NOT NULL,
  `fee` decimal(27,18) NOT NULL,
  `value` decimal(27,18) NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `symbol` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `note` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`wallet_transaction_id`),
  UNIQUE KEY `transaction_hash_UNIQUE` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table default_setting
# ------------------------------------------------------------

DROP TABLE IF EXISTS `default_setting`;

CREATE TABLE `default_setting` (
  `default_setting_id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `category` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `text` varchar(300) COLLATE utf8_unicode_ci NOT NULL,
  `value` tinyint(1) NOT NULL,
  `sort_order` int(11) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`default_setting_id`),
  UNIQUE KEY `type_UNIQUE` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `timezone` varchar(10) COLLATE utf8_unicode_ci DEFAULT '',
  `notification_code` varchar(250) COLLATE utf8_unicode_ci DEFAULT NULL,
  `os` enum('IOS', 'ANDROID') COLLATE utf8_unicode_ci DEFAULT 'IOS',
  `version` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `device_UNIQUE` (`device_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table setting
# ------------------------------------------------------------

DROP TABLE IF EXISTS `setting`;

CREATE TABLE `setting` (
  `setting_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `default_setting_id` int(11) NOT NULL,
  `value` tinyint(1) DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`setting_id`),
  UNIQUE KEY `user_id_default_setting_id_UNIQUE` (`user_id`,`default_setting_id`),
  KEY `setting_user_id` (`user_id`),
  KEY `setting_default_setting_id` (`default_setting_id`),
  CONSTRAINT `setting_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `setting_default_setting_id` FOREIGN KEY (`default_setting_id`) REFERENCES `default_setting` (`default_setting_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table job
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job`;

CREATE TABLE `job` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `type` enum('NOTIFICATION','BLOCK_SCRAPPING','BLOCK_BATCH_MAINTAINER') COLLATE utf8_unicode_ci DEFAULT 'NOTIFICATION',
  `status` enum('STARTED','FINISHED') COLLATE utf8_unicode_ci DEFAULT 'STARTED',
  `comment` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table notification
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `notification_status` enum('UNREAD','READ') COLLATE utf8_unicode_ci DEFAULT 'UNREAD',
  `message` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `data` varchar(1000) COLLATE utf8_unicode_ci,
  `notification_type` enum('TRANSACTION') COLLATE utf8_unicode_ci DEFAULT 'TRANSACTION',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`notification_id`),
  KEY `notification_user_id` (`user_id`),
  CONSTRAINT `notification_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table login
# ------------------------------------------------------------

DROP TABLE IF EXISTS `login`;

CREATE TABLE `login` (
  `login_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ip_address` varchar(50) COLLATE utf8_unicode_ci DEFAULT '',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`login_id`),
  KEY `login_user_id` (`user_id`),
  CONSTRAINT `login_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table feedback
# ------------------------------------------------------------

DROP TABLE IF EXISTS `feedback`;

CREATE TABLE `feedback` (
  `feedback_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `message` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `comment` varchar(500) COLLATE utf8_unicode_ci,
  `feedback_status` enum('RECEIVED','FIXED', 'NOTIFIED') COLLATE utf8_unicode_ci DEFAULT 'RECEIVED',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`feedback_id`),
  KEY `feedback_user_id` (`user_id`),
  CONSTRAINT `feedback_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table coinmarketcap
# ------------------------------------------------------------

DROP TABLE IF EXISTS `coinmarketcap`;
CREATE TABLE `coinmarketcap` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `symbol` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  UNIQUE KEY `coinmarketcap_id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;