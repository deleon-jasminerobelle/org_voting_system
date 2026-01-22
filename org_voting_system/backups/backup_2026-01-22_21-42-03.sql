-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: org_voting_system_fixed
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `candidates`
--

DROP TABLE IF EXISTS `candidates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `candidates` (
  `candidate_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `position_id` bigint(20) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `full_name` varchar(255) NOT NULL,
  `platform` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`candidate_id`),
  KEY `FK7cjd8wcj65d4gerees70rvcl9` (`position_id`),
  CONSTRAINT `FK7cjd8wcj65d4gerees70rvcl9` FOREIGN KEY (`position_id`) REFERENCES `positions` (`position_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `candidates`
--

LOCK TABLES `candidates` WRITE;
/*!40000 ALTER TABLE `candidates` DISABLE KEYS */;
INSERT INTO `candidates` VALUES (7,4,'Experienced leader with vision for the future','Juan Dela Cruz','Innovation and Excellence'),(8,4,'Dedicated to student welfare and academic excellence','Maria Santos','Unity and Progress'),(9,5,'Focused on innovation and technology','Pedro Reyes','Tech for All'),(10,5,'Committed to environmental sustainability','Ana Garcia','Green Future'),(11,6,'Organized and detail-oriented','Carlos Lopez','Efficiency First'),(12,6,'Excellent communication skills','Sofia Martinez','Connect and Communicate');
/*!40000 ALTER TABLE `candidates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `elections`
--

DROP TABLE IF EXISTS `elections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `elections` (
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `election_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_datetime` datetime(6) NOT NULL,
  `start_datetime` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `status` enum('ACTIVE','CLOSED','UPCOMING') NOT NULL,
  `organization` varchar(255) NOT NULL,
  PRIMARY KEY (`election_id`),
  KEY `FKncm1jn0m42hvnikns41yprhfn` (`created_by`),
  CONSTRAINT `FKncm1jn0m42hvnikns41yprhfn` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `elections`
--

LOCK TABLES `elections` WRITE;
/*!40000 ALTER TABLE `elections` DISABLE KEYS */;
INSERT INTO `elections` VALUES ('2026-01-22 12:27:12.000000',NULL,3,'2026-01-23 12:27:12.000000','2026-01-22 11:27:12.000000','Election for student council positions','Student Council Election 2026','ACTIVE','Organization');
/*!40000 ALTER TABLE `elections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `password_reset_tokens` (
  `created_at` datetime(6) NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `token_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UKla2ts67g4oh2sreayswhox1i6` (`user_id`),
  UNIQUE KEY `UK71lqwbwtklmljk3qlsugr1mig` (`token`),
  CONSTRAINT `FKk3ndxg5xp6v7wd4gjyusp15gq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `positions` (
  `max_votes` int(11) NOT NULL,
  `election_id` bigint(20) NOT NULL,
  `position_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `position_name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`position_id`),
  KEY `FKmeuw4aix52klf62v949aw6en7` (`election_id`),
  CONSTRAINT `FKmeuw4aix52klf62v949aw6en7` FOREIGN KEY (`election_id`) REFERENCES `elections` (`election_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `positions`
--

LOCK TABLES `positions` WRITE;
/*!40000 ALTER TABLE `positions` DISABLE KEYS */;
INSERT INTO `positions` VALUES (1,3,4,'President',NULL),(1,3,5,'Vice President',NULL),(1,3,6,'Secretary',NULL);
/*!40000 ALTER TABLE `positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` enum('ROLE_ADMIN','ROLE_ELECTION_OFFICER','ROLE_VOTER') NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UK716hgxp60ym1lifrdgp67xt5k` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_ELECTION_OFFICER'),(3,'ROLE_VOTER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `birthdate` date NOT NULL,
  `has_voted` bit(1) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `organization` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `section` varchar(255) NOT NULL,
  `student_number` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK6th577uijqxjb16etmo0jciob` (`student_number`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1990-01-01','\0','','2026-01-21 14:34:09.000000',1,1,'newadmin@example.com','NewAdmin','NewAdmin User','User',NULL,'Organization','$2a$10$j1KyLNxZAX78EDLBAcTGgejapsOg0IP5qRhZfIJJJz2QoaCHQJwfq','Admin','ADMIN001','newadmin'),('1990-01-01','\0','','2026-01-21 14:34:09.000000',2,2,'neweo@example.com','NewElection','NewElection Officer','Officer',NULL,'Organization','$2a$10$UggF68Aqr.Zihc9oHzgeZ..HV13SqACypOerTHBFPdMsrQMqg8oo2','EO','EO001','neweo'),('1995-05-15','\0','','2026-01-21 14:34:09.000000',3,3,'newvoter@example.com','NewJohn','NewJohn Doe','Doe',NULL,'Organization','$2a$10$l3u3sdNSSR1Htv4uosez6eYJnSMnKh10qIkyZ80l7oeSNQaMzGxoe','Voter','VOTER001','newvoter'),('2026-01-08','\0','','2026-01-22 12:00:27.000000',3,5,'test@example.com','TEST','TEST USER','USER','','CS','$2a$10$daR1vFj8rWn047eAaiexeOleUdSsvaUlqXX5Iw2BM0T9t10WbBZuK','1','00000000000000','testuser');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `votes`
--

DROP TABLE IF EXISTS `votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `votes` (
  `candidate_id` bigint(20) NOT NULL,
  `election_id` bigint(20) NOT NULL,
  `position_id` bigint(20) NOT NULL,
  `vote_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `voted_at` datetime(6) NOT NULL,
  `voter_id` bigint(20) NOT NULL,
  `vote_hash` varchar(255) NOT NULL,
  `voter_token` varchar(255) NOT NULL,
  PRIMARY KEY (`vote_id`),
  UNIQUE KEY `UK3jnpcvt6667lh549nkvxkb9dt` (`election_id`,`position_id`,`voter_id`),
  KEY `FKgr0htccc82fco8rhtkxcuct4g` (`candidate_id`),
  KEY `FKq5c5hie2fhw7fv5eudj35f70v` (`position_id`),
  KEY `FK94ah680c6uad0t12uj2ib9nam` (`voter_id`),
  CONSTRAINT `FK94ah680c6uad0t12uj2ib9nam` FOREIGN KEY (`voter_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKgr0htccc82fco8rhtkxcuct4g` FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`candidate_id`),
  CONSTRAINT `FKodk7ilbhf33jk4bm3b526j8ir` FOREIGN KEY (`election_id`) REFERENCES `elections` (`election_id`),
  CONSTRAINT `FKq5c5hie2fhw7fv5eudj35f70v` FOREIGN KEY (`position_id`) REFERENCES `positions` (`position_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `votes`
--

LOCK TABLES `votes` WRITE;
/*!40000 ALTER TABLE `votes` DISABLE KEYS */;
INSERT INTO `votes` VALUES (7,3,4,1,'2026-01-22 12:33:15.000000',5,'bef833753e84d604c8304ec31ad2942fa8edb825e82f628ad2e2680e373dc525','ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d'),(10,3,5,2,'2026-01-22 12:33:15.000000',5,'1f077f35efd95b72eb26c8738462be83a9135f5ddf605f410793bcf16defa3f4','ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d'),(11,3,6,3,'2026-01-22 12:33:15.000000',5,'29e2fa7a7dc3a13daa3ccacb703a7ac9047ab7a8e85d66455798375b5f925981','ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d');
/*!40000 ALTER TABLE `votes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'org_voting_system_fixed'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-22 21:42:03
