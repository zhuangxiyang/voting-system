create table sys_vote_user
(
   id                   bigint not null auto_increment comment '主键',
   user_name            varchar(64) comment '名称',
   user_code            varchar(64) comment '账号',
   email                varchar(128) comment '邮箱',
   telephone            varchar(64) comment '手机号码',
   password             varchar(256) comment '密码',
   header_url           varchar(256) comment '头像',
   login_status         tinyint comment '启用状态',
   station_code         varchar(64) comment '组织编码',
   password_expire_time datetime comment '密码过期时间',
   create_user          varchar(64) comment '创建人',
   update_user          varchar(64) comment '修改人',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   del_flag             tinyint(4) comment '删除标识',
   primary key (id)
);

alter table sys_vote_user comment '用户';



create table bu_activity
(
   id                   bigint not null auto_increment comment '主键',
   act_no               varchar(64) comment '活动编码',
   act_name             varchar(64) comment '活动名称',
   act_start_time       datetime comment '活动开始时间',
   act_end_time         datetime comment '活动结束时间',
   act_status           varchar(16) comment '活动状态',
   winner               varchar(64) comment '获选人',
   winner_id_card       varchar(64) comment '获选人身份证',
   winner_num           varchar(64) comment '获选人票数',
   create_user          varchar(64) comment '创建人',
   update_user          varchar(64) comment '修改人',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   del_flag             tinyint(4) comment '删除标识',
   primary key (id)
);

alter table bu_activity comment '活动';



create table bu_candidate_record
(
   id                   bigint not null auto_increment comment '主键',
   act_no               varchar(64) comment '活动编码',
   candidate_name       varchar(64) comment '候选人名称',
   candidate_id_card    varchar(64) comment '候选人身份证',
   candidate_mail       varchar(64) comment '候选人邮箱',
   create_user          varchar(64) comment '创建人',
   update_user          varchar(64) comment '修改人',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   del_flag             tinyint(4) comment '删除标识',
   primary key (id)
);

alter table bu_candidate_record comment '候选人记录';


create table bu_voting_record
(
   id                   bigint not null auto_increment comment '主键',
   act_no               varchar(64) comment '活动编码',
   candidate_id_card    varchar(64) comment '候选人身份证',
   candidate_name       varchar(64) comment '候选人名称',
   voter_name           varchar(64) comment '投票人名称',
   voter_id_card        varchar(64) comment '投票人身份证',
   voter_mail           varchar(64) comment '投票人邮箱',
   create_user          varchar(64) comment '创建人',
   update_user          varchar(64) comment '修改人',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '修改时间',
   del_flag             tinyint(4) comment '删除标识',
   primary key (id)
);

alter table bu_voting_record comment '投票记录';


CREATE TABLE sys_serial_number (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  serial_code varchar(64) DEFAULT NULL COMMENT '编码',
  serial_name varchar(64) DEFAULT NULL COMMENT '名称',
  serial_num bigint(20) DEFAULT '0' COMMENT '流水号总数',
  serial_rule varchar(64) DEFAULT NULL COMMENT '规则',
  serial_num_count int(11) DEFAULT NULL COMMENT '流水位数',
  remark varchar(256) DEFAULT NULL COMMENT '备注',
  status tinyint(4) DEFAULT NULL COMMENT '0-关闭,1-启用',
  create_user varchar(64) DEFAULT NULL COMMENT '创建人',
  update_user varchar(64) DEFAULT NULL COMMENT '修改人',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '修改时间',
  del_flag tinyint(4) DEFAULT NULL COMMENT '0-正常,1-已删除',
  PRIMARY KEY (id),
  KEY Index_serial_coe (serial_code)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8 COMMENT='流水号管理';


CREATE TABLE sys_serial_index (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  serial_code varchar(64) DEFAULT NULL COMMENT '流水号编码',
  serial_param varchar(64) DEFAULT NULL COMMENT '前缀（自定义参数）',
  serial_date varchar(64) DEFAULT NULL COMMENT '日期',
  serial_num_count varchar(64) DEFAULT NULL COMMENT '流水位数',
  serial_index bigint(20) DEFAULT NULL COMMENT '当前流水号',
  version bigint(20) DEFAULT NULL COMMENT '版本号',
  create_user varchar(64) DEFAULT NULL COMMENT '创建人',
  update_user varchar(64) DEFAULT NULL COMMENT '修改人',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '修改时间',
  del_flag tinyint(4) DEFAULT NULL COMMENT '0-正常,1-已删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8 COMMENT='当前流水号统计表';




INSERT INTO sys_vote_user (user_name,user_code,password,login_status,create_user,update_user,create_time,update_time,del_flag) VALUES ('超级管理员','admin','$2a$10$L1ooXiChOiCWri2SnU8dBOfWU4Pnh1IlrF0CsUiZQe/smJvd9Clcq',1,'admin','admin','2022-07-04 12:00:00','2022-07-04 12:00:00',0);


INSERT INTO sys_serial_number (serial_code,serial_name,serial_num,serial_rule,serial_num_count,remark,status,create_user,update_user,create_time,update_time,del_flag) VALUES ('VOTING_CODE','活动编码',0,'{PARAMS(ACT)}{DATE(yyMMdd)}{NO(3)}',3,null,1,'admin','admin','2022-07-04 18:42:23','2022-07-04 18:42:46',0);

