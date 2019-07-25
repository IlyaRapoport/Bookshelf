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
INSERT INTO `books` VALUES (25,'auth1','sfsgsdfgzdf','name1',NULL,NULL,1),(26,'auth2','dsfsdfsd','name2',NULL,NULL,1),(46,'auth2','sdf','dsfsd',NULL,NULL,1),(66,'asdas','asdasd','asdasd',NULL,NULL,15),(68,' ','dfasdas','name',NULL,NULL,1),(69,'asdasd','asdasd','sdasd',NULL,NULL,1),(70,'sdfsdf','sdfsd','dsfsdf',NULL,NULL,1),(71,'sdfsdf','sdfsd','dsfsdf',NULL,NULL,1),(72,'asdasd','asdsa','test',NULL,NULL,1),(73,'afdaf','sadas','test1',NULL,'0841043a-d3ca-4d21-88ae-9378a390922e.harry.jpg',1),(74,' ','dasdas','test2',NULL,NULL,1),(75,'auth2','asdasd','test3',NULL,NULL,1),(76,' ','sadas','testt',NULL,NULL,1),(78,'1','1','1',NULL,NULL,1);
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

-- Dump completed on 2019-07-25 10:10:34