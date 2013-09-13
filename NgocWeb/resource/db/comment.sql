DROP TABLE IF EXISTS nhp_comment;
DROP TABLE IF EXISTS nhp_target;
DROP TABLE IF EXISTS nhp_like;

CREATE TABLE nhp_comment (
	comment_id 				BIGINT(20) NOT NULL,
		INDEX(comment_id),
	account_name 			VARCHAR(128),
		INDEX(account_name),
	content 				BLOB ,
	target_id 				BIGINT(20) NOT NULL,
		INDEX(target_id),
	token 					VARCHAR(256) NOT NULL DEFAULT '',
		INDEX(token),
	created 				DATETIME ,
		INDEX(created),
	updated 				DATETIME ,
	status 					TINYINT NOT NULL DEFAULT 1,
		INDEX(status),
	approved_by 			VARCHAR(128),
		INDEX(approved_by),
	updated_by 				VARCHAR(128),
	parent_comment_id 		BIGINT(20),
		INDEX(parent_comment_id),
	total_likes 			INT	NOT NULL DEFAULT 0,
		INDEX(total_likes),
	FOREIGN KEY (parent_comment_id) REFERENCES nhp_comment(comment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci; 


CREATE TABLE nhp_target (
  	target_id 				BIGINT(20) NOT NULL,
  	target 					VARCHAR(255) DEFAULT NULL,
  		INDEX(target),
  	target_url				VARCHAR(255) DEFAULT NULL,
  	created 				DATETIME,
  		INDEX(created),
  	comment_number 			INT(11) DEFAULT '0',
  		INDEX(comment_number),
  	token 					VARCHAR(64) DEFAULT NULL,
  		INDEX(token),
  	info 					TEXT,
  	PRIMARY KEY (target_id),
  	UNIQUE KEY (target, token)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci; 

CREATE TABLE nhp_like (
  	account_name 			VARCHAR(255) NOT NULL,
  		INDEX (account_name),
  	target_id 				BIGINT(20) NOT NULL,
  		INDEX (target_id),
  	comment_id 				BIGINT(20),
  		INDEX (comment_id),
  	created 				DATETIME,
  		INDEX (created),
  	PRIMARY KEY (account_name,target_id,comment_id),
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci; 