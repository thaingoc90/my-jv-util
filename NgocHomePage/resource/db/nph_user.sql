DROP TABLE IF EXISTS nhp_group_permission;
DROP TABLE IF EXISTS nhp_permission;
DROP TABLE IF EXISTS nhp_user;
DROP TABLE IF EXISTS nhp_group;

CREATE TABLE nhp_group (
	gid						INT					NOT NULL AUTO_INCREMENT,
	gname					VARCHAR(32)			NOT NULL,
	gdesc					VARCHAR(128),
	gis_system				TINYINT(1)				NOT NULL DEFAULT 0,
	INDEX (gname),
	PRIMARY KEY (gid)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- Default user groups
INSERT INTO nhp_group (gid, gis_system, gname, gdesc) VALUES 
	(1, 1, 'Administrators', 'Administrators have all permissions'),
	(2, 0, 'Members', 'Normal users');

CREATE TABLE nhp_user (
	uid						INT					NOT NULL AUTO_INCREMENT,
	uemail					VARCHAR(64)			NOT NULL,
		UNIQUE INDEX (uemail),
	ulogin_name				VARCHAR(64)			NOT NULL,
		UNIQUE INDEX (ulogin_name),
	udisplay_name			VARCHAR(128),
	ubirthday				DATETIME,
	upassword				VARCHAR(64),
	gid						INT					DEFAULT 0,
		INDEX(gid),
	uis_disabled			TINYINT(1)			NOT NULL DEFAULT 0,
	ucreated_date			DATETIME			NOT NULL DEFAULT 0,
	utime_login				DATETIME,
	ulast_login				DATETIME,
	uforget_token 			VARCHAR(64),
	FOREIGN KEY gid(gid) REFERENCES nhp_group(gid) ON DELETE CASCADE,
    PRIMARY KEY (uid)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
-- Default User Account: admin/password
INSERT INTO nhp_user (uid, uemail, ulogin_name, udisplay_name, upassword, gid, ucreated_date) VALUES (1, 'admin@localhost', 'admin', 'admin', 'b65dfc81cc6d76c32d2f52eaec95cff0', 1, NOW());

-- Table permission
CREATE TABLE nhp_permission (
	id						VARCHAR(64)				NOT NULL,
	pdesc					VARCHAR(255),
	parent_id				VARCHAR(64),
	FOREIGN KEY (parent_id) REFERENCES nhp_permission(id) ON DELETE CASCADE,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
INSERT INTO nhp_permission (id, pdesc) VALUES ('ADMIN', 'See menu-admin');
INSERT INTO nhp_permission (id, pdesc) VALUES ('ADMIN_USER', 'All privileges on menu-group');
INSERT INTO nhp_permission (id, pdesc) VALUES ('ADMIN_GROUP', 'All privileges on menu-user');

-- Table for relation between permission and group
CREATE TABLE nhp_group_permission (
	gid					INT,
	pid					VARCHAR(64),
	INDEX (pid),
	PRIMARY KEY (gid, pid),
	FOREIGN KEY gid(gid) REFERENCES nhp_group(gid) ON DELETE CASCADE,
	FOREIGN KEY pid(pid) REFERENCES nhp_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- Table menu
CREATE TABLE nhp_menu (
	mid					INT(8)					NOT NULL AUTO_INCREMENT,
	mname				VARCHAR(64)				NOT NULL DEFAULT "",
	murl				VARCHAR(128)			NOT NULL DEFAULT "",
	mposition			INT(8)					NOT NULL DEFAULT 0,	
	mparent_id			INT(8),
	mpermission			VARCHAR(64),
	PRIMARY KEY (mid),
	INDEX (mposition),
	INDEX (mparent_id),
	FOREIGN KEY mpermission(mpermission) REFERENCES nhp_permission(id) ON DELETE SET NULL,
	FOREIGN KEY mparent_id(mparent_id) REFERENCES nhp_menu(mid) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

INSERT INTO nhp_menu (mname, murl, mposition, mparent_id) VALUES
	('Manage', '', 1, NULL),
	('Setting', '', 2, NULL),
	('Groups', 'manage/groups', 1, 1),
	('Users', 'manage/users', 2, 1),
	('Permissions', 'manage/permissions', 3, 1),
	('Accounts', 'setting/account', 1, 2),
	('Mail', 'setting/mail', 2, 2);
	
CREATE TABLE nhp_config (
 	c_key            VARCHAR(32)         	NOT NULL,
    c_value          TEXT,
    PRIMARY KEY (c_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
