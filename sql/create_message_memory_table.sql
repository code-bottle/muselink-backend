create database if not exists muselink;

use muselink;

create table if not exists chat_message_memory(
    id bigint not null auto_increment comment '主键ID',
    user_id bigint not null default 1 comment '用户ID',
    conversation_id varchar(255) not null comment '会话ID',
    message_order int not null comment '消息顺序',
    message_type varchar(64) not null comment '消息类型',
    message_content mediumtext not null comment '消息内容',
    create_time timestamp not null default current_timestamp comment '创建时间',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted boolean not null default false comment '逻辑删除',
    primary key(id),
    index idx_conversation_id(conversation_id),
    index idx_conversation_message_order(conversation_id, message_order)
)engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = 'AI会话消息表';