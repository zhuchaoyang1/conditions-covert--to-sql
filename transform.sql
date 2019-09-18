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

 Date: 18/09/2019 20:56:15
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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trans_user
-- ----------------------------
INSERT INTO `trans_user` VALUES (1, 'zcy', 'admin');
INSERT INTO `trans_user` VALUES (2, 'ceshi', 'ceshi');
INSERT INTO `trans_user` VALUES (3, 'admin', 'admin');

SET FOREIGN_KEY_CHECKS = 1;
