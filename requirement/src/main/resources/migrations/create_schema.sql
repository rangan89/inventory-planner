CREATE TABLE `FDP_FSN_BAND` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `time_frame` varchar(50) NOT NULL DEFAULT '',
  `sales_band` int(11) DEFAULT NULL,
  `pv_band` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_time` (`fsn`,`time_frame`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `FDP_FSN_SALES_BUCKET` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `warehouse` varchar(30) NOT NULL DEFAULT '',
  `week` int(11) NOT NULL,
  `sale_qty` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_wh_week` (`fsn`,`warehouse`,`week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `FDP_INTRANSIT` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `warehouse` varchar(50) NOT NULL DEFAULT '',
  `pending_po_qty` int(11) DEFAULT NULL,
  `open_req_qty` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_warehouse` (`fsn`,`warehouse`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


CREATE TABLE `FDP_WH_INVENTORY` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `warehouse` varchar(50) NOT NULL DEFAULT '',
  `quantity` int(11) DEFAULT NULL,
  `qoh` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_warehouse` (`fsn`,`warehouse`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `FORECAST` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `warehouse` varchar(50) NOT NULL DEFAULT '',
  `forecast` varchar(200) NOT NULL DEFAULT '',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_wh_forecast` (`fsn`,`warehouse`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `GROUP` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `proc_type` varchar(100) NOT NULL DEFAULT '',
  `is_enabled` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `GROUP_FSN` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) unsigned NOT NULL,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `is_enabled` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_id` (`group_id`,`fsn`),
  KEY `group_id_2` (`group_id`),
  CONSTRAINT `group_fsn_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `GROUP` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `POLICY` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) unsigned NOT NULL,
  `fsn` varchar(20) DEFAULT NULL,
  `policy_type` varchar(40) NOT NULL DEFAULT '',
  `policy_value` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_fsn_policy` (`group_id`,`fsn`,`policy_type`),
  CONSTRAINT `policy_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `GROUP` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `PRODUCT_INFO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `vertical` varchar(30) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `super_category` varchar(50) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `fsp` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn` (`fsn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `REQUIREMENT` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) NOT NULL DEFAULT '',
  `warehouse` varchar(30) NOT NULL DEFAULT '',
  `quantity` int(11) DEFAULT NULL,
  `supplier` varchar(50) DEFAULT NULL,
  `mrp` float DEFAULT NULL,
  `app` float DEFAULT NULL,
  `currency` varchar(11) DEFAULT NULL,
  `sla` int(11) DEFAULT NULL,
  `international` tinyint(1) DEFAULT NULL,
  `state` varchar(20) DEFAULT NULL,
  `proc_type` varchar(20) DEFAULT '{DAILY_PLANNIng}',
  `is_enabled` tinyint(1) DEFAULT NULL,
  `is_current` tinyint(1) DEFAULT NULL,
  `override_comment` varchar(200) DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  `requirement_snapshot_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_wh_enabled` (`fsn`,`warehouse`,`is_enabled`),
  KEY `requirement_snapshot_id` (`requirement_snapshot_id`),
  CONSTRAINT `requirement_ibfk_1` FOREIGN KEY (`requirement_snapshot_id`) REFERENCES `REQUIREMENT_SNAPSHOT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `requirement_snapshot` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `forecast` varchar(200) DEFAULT NULL,
  `inventory_qty` int(11) DEFAULT NULL,
  `qoh` int(11) DEFAULT NULL,
  `pending_po_qty` int(11) DEFAULT NULL,
  `open_req_qty` int(11) DEFAULT NULL,
  `iwit_intransit_qty` int(11) DEFAULT NULL,
  `policy` text,
  `group_id` bigint(20) unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `requirement_snapshot_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `ip_groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `VERTICAL_CONFIGURATION` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `vertical` varchar(30) NOT NULL DEFAULT '',
  `field_name` varchar(30) NOT NULL DEFAULT '',
  `field_value` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `vertical_field` (`vertical`,`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `WAREHOUSE` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `code` varchar(20) NOT NULL DEFAULT '',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wh_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `group_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `state` varchar(32) NOT NULL,
  `is_auto_approved` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `unique_idx_group_state` (`group_id`,`state`)
);

ALTER TABLE `projection_states` 
    ADD COLUMN `is_current` TINYINT(1) NOT NULL DEFAULT 0 AFTER `currency`;

ALTER TABLE `projection_states` 
    ADD COLUMN `fsn` VARCHAR(45) NOT NULL AFTER `is_current`;

ALTER TABLE `projection_states` 
    ADD COLUMN `proc_type` VARCHAR(45) NULL AFTER `fsn`;



