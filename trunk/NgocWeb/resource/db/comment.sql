DROP TABLE IF EXISTS nhp_comment;

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