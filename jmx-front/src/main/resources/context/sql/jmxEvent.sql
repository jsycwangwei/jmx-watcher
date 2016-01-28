DELIMITER $$

/**SET GLOBAL event_scheduler = ON$$ **/    -- required for event to execute but not create
CREATE EVENT `jmx`.`e_check_jmx_server`

ON SCHEDULE

EVERY 1 DAY

DO
	BEGIN
	    INSERT INTO jmx_server(host_id, app_id, host_ip, add_time)
		SELECT d.host_id,a.app_id,d.inner_ip,NOW()
		FROM app_info a,hostgrp b,hosts_groups c,host_info d
		WHERE d.host_id = c.hostid
		AND b.grpid = c.groupid
		AND b.app_id = a.app_id
		AND a.delete_flag = '0'
		AND b.delete_flag = '0'
		AND c.delete_flag = '0'
		AND d.delete_flag = '0'
		AND NOT EXISTS(SELECT app_id FROM jmx_server WHERE app_id=a.app_id);
	END$$

DELIMITER ;