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


CREATE TABLE `last_app_supplier` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(11) DEFAULT NULL,
  `warehouse` varchar(11) DEFAULT NULL,
  `last_supplier` varchar(11) DEFAULT NULL,
  `last_app` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL DEFAULT '',
  `name` varchar(45) NOT NULL DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`code`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


alter table projection_states add column fsn varchar(20);
update projection_states as ps set fsn = (select fsn from projections where id = ps.projection_id ) where fsn = null;

//populating existing fsn from projections to projection_states
update projection_states as ps set fsn = (select fsn from projections where id = ps.projection_id ) where fsn is NULL

alter table projection_states add column is_current TINYINT(1);
update projection_states ps join projections p on ps.projection_id=p.id and p.enabled=1 and p.current_state=ps.state set ps.is_current=1;
update projection_states as ps set enabled = 1 where projection_id in (select id from projections where enabled = 1 );

alter table projection_states add column requirement_snapshot_id BIGINT(20) unsigned, add key(requirement_snapshot_id);

ALTER TABLE `projection_states` ADD CONSTRAINT `projection_states_req_snapshot_id` FOREIGN KEY (`requirement_snapshot_id`) REFERENCES `requirement_snapshot` (`id`)
update projection_states as ps set enabled = 1 where projection_id in (select id from projections where enabled = 1 );



alter table projection_states add column proc_type varchar(20);
update  projection_states as ps set proc_type= (select proc_type from projections where id = ps.projection_id );

alter table ip_groups add column is_enabled TINYINT(1) NOT NULL DEFAULT 0;
update ip_groups set is_enabled= 1 where tag = "rp_planning";

alter table `INTRANSIT` add column `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
