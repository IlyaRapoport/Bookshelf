-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: bookshelf
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `book_author` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `book_description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `book_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `file_for_download` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `filename` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaaw1q71x73au52leowe1x2jsr` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (128,' ','','pdfnew',NULL,'c63c4168-ba22-4c95-95a8-f300c7014cd8.IntelliJIDEA_ReferenceCard.pdf',1),(130,' ','','imgpdf',NULL,'0f32fb89-1151-4b78-ac57-e0c7a20e0a34.bookshelf-icon-vector-3243335.jpg',1),(133,' ','','imgpdf2',NULL,'73c77e4f-d6df-4465-9bf3-654c9c46056a.harry.jpg',1),(136,' ','','img',NULL,NULL,1),(137,' ','','pdf',NULL,NULL,1),(138,' ','','name',NULL,'96ab5dcf-aa4a-46db-a428-0a6f2d78bf98.IntelliJIDEA_ReferenceCard.pdf',1),(140,' ','','pdf',NULL,'ce2adac5-033b-4235-862a-b2159b2ba9b2.IntelliJIDEA_ReferenceCard.pdf',1),(142,' ','','pdfimg',NULL,'8c3324d7-3d47-485a-8022-b328f69d4265.harry.jpg',1),(145,' ','','pdfimg',NULL,'f0743285-7423-4ac4-8fab-9efe272f23ed.bookshelf-icon-vector-3243335.jpg',1);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-06 10:10:12
