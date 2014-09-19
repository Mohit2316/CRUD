# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigint auto_increment not null,
  purchase_key              bigint,
  comment                   varchar(255),
  user_id                   bigint,
  created_by                varchar(255),
  time_stamp                varchar(255),
  if_delete                 tinyint(1) default 0,
  constraint pk_comment primary key (id))
;

create table purchase_request (
  id                        bigint auto_increment not null,
  cost_center_number        integer,
  cost_center_name          varchar(255),
  requisition_type          varchar(255),
  asset_category            varchar(255),
  acc_category              varchar(255),
  asset_tag                 varchar(255),
  cost_estimate             float,
  source_estimate           float,
  requested_delivery_date   date,
  detailed_item             varchar(255),
  quantity                  integer,
  vendor_name               varchar(255),
  req_id                    varchar(255),
  approver                  varchar(255),
  po_number                 varchar(255),
  vp_signature              varchar(255),
  vp_typed_name             varchar(255),
  date_sig_vp               datetime,
  software_approver_signature_1 varchar(255),
  date_sign_software_1      datetime,
  software_approver_signature_2 varchar(255),
  date_sign_software_2      datetime,
  created_at                datetime,
  updated_at                datetime,
  approved_at               datetime,
  processed_at              datetime,
  comment_financer_reject   varchar(255),
  comment_financer_accept   varchar(255),
  comment_approver_rejct    varchar(255),
  comment_approver_accept   varchar(255),
  payment_method            varchar(255),
  file_id                   varchar(255),
  status                    integer,
  request_status            integer,
  approver_id               bigint,
  notes                     varchar(255),
  user_id                   bigint,
  version                   integer not null,
  constraint pk_purchase_request primary key (id))
;

create table sending_mails (
  id                        bigint auto_increment not null,
  from_status               integer,
  to_status                 integer,
  role                      integer,
  constraint pk_sending_mails primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  user_name                 varchar(255),
  name                      varchar(255),
  version                   bigint not null,
  created_at                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sha_password              varbinary(64) not null,
  auth_token                varchar(255),
  reset_token               varchar(255),
  role                      integer,
  approver_id               bigint,
  constraint uq_user_user_name unique (user_name),
  constraint pk_user primary key (id))
;

alter table purchase_request add constraint fk_purchase_request_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_purchase_request_user_1 on purchase_request (user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table comment;

drop table purchase_request;

drop table sending_mails;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

