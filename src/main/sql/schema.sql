-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE login;
-- 使用数据库
use login;

-- 创建表
DROP TABLE t_user;
CREATE TABLE t_user(
  uuid BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  nickname VARCHAR(50) NOT NULL COMMENT '登录后显示的昵称',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录时的用户名',
  password VARCHAR(120) NOT NULL COMMENT '登录密码',
  salt VARCHAR(120) NOT NULL COMMENT '密码盐',
  email VARCHAR(30) NOT NULL UNIQUE COMMENT '邮箱，可以用来登录或重置密码',
  phone VARCHAR(20) NOT NULL COMMENT '手机号码，用来重置密码',
  register_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  status INT NOT NULL DEFAULT 0 COMMENT '0表示未激活，1表示已激活',
  activate_code VARCHAR(250) COMMENT '激活码',
  send_activate_code_time DATETIME COMMENT '上次发送激活码的时间',
  validate_code VARCHAR(250) COMMENT '验证码',
  send_validate_code_time DATETIME COMMENT '上次发送验证码的时间',
  PRIMARY KEY (uuid)
)AUTO_INCREMENT = 1000 DEFAULT CHARSET = utf8 COMMENT '用户表';

# CREATE TABLE t_user(
#   nickname VARCHAR(50) NOT NULL,
#   username VARCHAR(50) NOT NULL ,
#   password VARCHAR(120) NOT NULL ,
#   email VARCHAR(30) NOT NULL ,
#   phone VARCHAR(20) NOT NULL ,
#   register_time TIMESTAMP,
#   status INT NOT NULL DEFAULT 0,
#   activate_code VARCHAR(250) ,
#   send_activate_code_time TIMESTAMP,
#   validate_code VARCHAR(250) ,
#   send_validate_code_time TIMESTAMP
# )

-- 初始化数据
INSERT INTO t_user (nickname,username,password,email,phone,activate_code) VALUES ('aa','aa','4124BC0A9335C27F086F24BA207A4912','nightynight_cc@163.com','15522331234','123254121');

UPDATE t_user SET status=1 WHERE username='aa';