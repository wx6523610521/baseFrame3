-- ============================================================
-- 基础框架 — 数据库初始化脚本
-- 版本: V1 (2026-05-13)
-- 最小表集: 保证登录鉴权流程可运行
-- ============================================================

DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_account;
DROP TABLE IF EXISTS sys_dictionary;
DROP TABLE IF EXISTS sys_logs;
DROP TABLE IF EXISTS sys_permissions;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

-- === 用户表 ===
CREATE TABLE sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(127) NOT NULL,
    sex         VARCHAR(63)  DEFAULT NULL,
    phonenum    CHAR(11)     DEFAULT NULL,
    email       VARCHAR(127) DEFAULT NULL,
    companyname VARCHAR(511) DEFAULT NULL,
    user_type   SMALLINT     DEFAULT 0,
    createtime  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatetime  TIMESTAMP    DEFAULT NULL,
    updateuser  INT          DEFAULT NULL,
    isdeleted   BOOLEAN      NOT NULL DEFAULT FALSE
);
COMMENT ON COLUMN sys_user.username    IS '用户名';
COMMENT ON COLUMN sys_user.sex         IS '性别';
COMMENT ON COLUMN sys_user.phonenum    IS '手机号';
COMMENT ON COLUMN sys_user.email       IS '电子邮箱';
COMMENT ON COLUMN sys_user.companyname IS '公司名称';
COMMENT ON COLUMN sys_user.user_type   IS '用户类型: 0-C端普通用户 1-B端后台管理员';
COMMENT ON COLUMN sys_user.createtime  IS '创建时间';
COMMENT ON COLUMN sys_user.isdeleted   IS '是否删除';

-- === 账户表 ===
CREATE TABLE sys_account (
    id             BIGSERIAL PRIMARY KEY,
    accountname    VARCHAR(127) NOT NULL DEFAULT 'default',
    accountpwd     VARCHAR(63)  NOT NULL DEFAULT '0',
    userid         INT          NOT NULL,
    accounttype    SMALLINT     NOT NULL DEFAULT 0,
    accountstatus  SMALLINT     NOT NULL DEFAULT 1,
    lastlogintime  TIMESTAMP    DEFAULT NULL,
    headimage      VARCHAR(255) DEFAULT NULL,
    nickname       VARCHAR(63)  DEFAULT NULL,
    wechatopenid   VARCHAR(63)  DEFAULT NULL,
    isdeleted      BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_sys_account_userid ON sys_account(userid);

COMMENT ON COLUMN sys_account.accountname   IS '登录名';
COMMENT ON COLUMN sys_account.accountpwd    IS '加密密码';
COMMENT ON COLUMN sys_account.userid        IS '关联用户Id';
COMMENT ON COLUMN sys_account.accounttype   IS '账户类型';
COMMENT ON COLUMN sys_account.accountstatus IS '账号状态 0 禁用 1 正常';
COMMENT ON COLUMN sys_account.lastlogintime IS '最后登录时间';
COMMENT ON COLUMN sys_account.headimage     IS '头像';
COMMENT ON COLUMN sys_account.nickname      IS '昵称';

-- === 角色表 ===
CREATE TABLE sys_role (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(63) NOT NULL,
    mark       VARCHAR(63) NOT NULL,
    isdefault  BOOLEAN DEFAULT FALSE,
    status     SMALLINT DEFAULT 1,
    sort       INT DEFAULT NULL,
    createtime TIMESTAMP DEFAULT NULL,
    creator    INT DEFAULT NULL,
    updatetime TIMESTAMP DEFAULT NULL,
    updateuser INT DEFAULT NULL,
    isdeleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE sys_user_role (
    userid INT NOT NULL,
    roleid INT NOT NULL,
    PRIMARY KEY (userid, roleid)
);

-- === 权限表 ===
CREATE TABLE sys_permissions (
    id             BIGSERIAL PRIMARY KEY,
    displayname    VARCHAR(63)  NOT NULL,
    permissionname VARCHAR(63)  NOT NULL,
    url            VARCHAR(255) DEFAULT NULL,
    tags           VARCHAR(255) DEFAULT NULL,
    level          SMALLINT     NOT NULL DEFAULT 0,
    parentid       INT          DEFAULT NULL,
    sort           INT          DEFAULT NULL,
    type           VARCHAR(63)  DEFAULT NULL,
    status         SMALLINT     DEFAULT NULL,
    createtime     TIMESTAMP    DEFAULT NULL,
    creator        INT          DEFAULT NULL,
    updatetime     TIMESTAMP    DEFAULT NULL,
    updateuser     INT          DEFAULT NULL,
    isdeleted      BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE sys_role_permission (
    roleid       INT NOT NULL,
    permissionid INT NOT NULL,
    PRIMARY KEY (roleid, permissionid)
);

-- === 字典表 ===
CREATE TABLE sys_dictionary (
    id       BIGSERIAL PRIMARY KEY,
    type     VARCHAR(63)  DEFAULT NULL,
    typename VARCHAR(127) DEFAULT NULL,
    code     VARCHAR(63)  DEFAULT NULL,
    name     VARCHAR(255) DEFAULT NULL,
    upid     INT          DEFAULT NULL,
    level    SMALLINT     DEFAULT NULL
);

-- === 日志表 ===
CREATE TABLE sys_logs (
    id           BIGSERIAL PRIMARY KEY,
    createtime   TIMESTAMP NOT NULL,
    userid       VARCHAR(36)  DEFAULT NULL,
    username     VARCHAR(127) DEFAULT NULL,
    costtime     BIGINT       DEFAULT NULL,
    logtype      VARCHAR(31)  DEFAULT NULL,
    logcontent   VARCHAR(511) DEFAULT NULL,
    method       VARCHAR(127) DEFAULT NULL,
    requestparam VARCHAR(2047) DEFAULT NULL,
    ip           VARCHAR(46)  DEFAULT NULL,
    result       VARCHAR(2047) DEFAULT NULL
);

-- ============================================================
-- 初始化数据: 默认管理员 admin / admin123
-- ============================================================
INSERT INTO sys_user (id, username, phonenum, user_type, createtime, isdeleted)
VALUES (1, '管理员', '13800000000', 1, NOW(), false);

INSERT INTO sys_account (id, accountname, accountpwd, userid, accounttype, accountstatus, nickname, isdeleted)
VALUES (1, 'admin',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh',
        1, 1, 1, '管理员', false);
-- 密码: admin123 (BCrypt)
