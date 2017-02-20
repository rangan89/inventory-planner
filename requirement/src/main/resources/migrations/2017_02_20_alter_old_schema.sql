CREATE TABLE `REQUIREMENT_SNAPSHOT` (
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


CREATE TABLE `LAST_APP_SUPPLIER` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(11) DEFAULT NULL,
  `warehouse` varchar(11) DEFAULT NULL,
  `last_supplier` varchar(11) DEFAULT NULL,
  `last_app` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table projection_states add column fsn varchar(20);

alter table projection_states add column is_current Boolean;

alter table projection_states add column requirement_snapshot_id BigInt(11);

alter table projection_states add column proc_type varchar(20);

alter table ip_groups add column is_enabled bool;

