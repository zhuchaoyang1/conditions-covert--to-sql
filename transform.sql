/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : transform

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 20/09/2019 16:31:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for trans_user
-- ----------------------------
DROP TABLE IF EXISTS `trans_user`;
CREATE TABLE `trans_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `start_time` datetime(0) DEFAULT NULL,
  `end_time` datetime(0) DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `age` int(3) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trans_user
-- ----------------------------
INSERT INTO `trans_user` VALUES (1, 'zcy', 'admin', '2019-09-19 08:48:37', '2019-09-20 08:48:41', 1, 11);
INSERT INTO `trans_user` VALUES (2, 'ceshi', 'ceshi', '2019-09-04 08:48:45', '2019-09-17 08:48:49', 0, 52);
INSERT INTO `trans_user` VALUES (3, 'admin', 'admin', '2019-10-05 08:48:56', '2019-10-20 08:49:01', NULL, 0);
INSERT INTO `trans_user` VALUES (4, 'admin2', 'test', '2019-09-27 08:49:21', '2019-09-27 09:49:25', 0, 400);
INSERT INTO `trans_user` VALUES (5, 'test', 'test', '2019-09-20 14:51:21', NULL, 0, 22);

SET FOREIGN_KEY_CHECKS = 1;
