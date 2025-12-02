# supervisory-system

数据库： gov_duban_prod  区委
        gov_duban2_prod 区政府

### 表：bz_form

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| name | VARCHAR | YES | NULL | 名称 |
| type | VARCHAR | YES | NULL | 类型 |
| type_id | INTEGER | YES | NULL | 类型ID |
| fill_cycle | INTEGER | YES | NULL | 通报周期 |
| actual_gear | INTEGER | YES | NULL | 档位 |
| predicted_gear | INTEGER | YES | NULL | 预估档位 |
| creator | VARCHAR | YES | NULL | 创建人 |
| creator_id | VARCHAR | YES | NULL | 创建人ID |
| date_type | INTEGER | YES | NULL | 日期类型字段 |
| year | INTEGER | YES | NULL | 年份 |
| quarter | INTEGER | YES | NULL | 季度 |
| leading_department | VARCHAR | YES | NULL | 牵头单位 |
| leading_department_id | VARCHAR | YES | NULL | 牵头单位ID |
| responsible_dept | VARCHAR | YES | NULL | 责任部门 |
| responsible_dept_id | VARCHAR | YES | NULL | 责任部门ID |
| operator | VARCHAR | YES | NULL | 操作人 |
| operator_id | VARCHAR | YES | NULL | 操作人ID |
| assigner | VARCHAR | YES | NULL | 交办人 |
| assigner_id | VARCHAR | YES | NULL | 交办人ID |
| check_status | VARCHAR | YES | NULL | 审核状态（1:任务审核；2:阶段性审核；3:报表审核；4:指标审核），用逗号分隔 |
| process_instance_id | VARCHAR | YES | NULL | 审核流水号 |
| process_instance_review_ids | TEXT | YES | NULL | 审核的人，使用逗号分隔 |
| process_instance_target_review_ids | TEXT | YES | NULL | 指标审核的人，使用逗号分隔 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### bz_form_target

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| bz_form_id | INTEGER | YES | NULL | 表单ID |
| name | VARCHAR | YES | NULL | 名称 |
| dept | VARCHAR | YES | NULL | 部门 |
| dept_id | VARCHAR | YES | NULL | 部门ID |
| predicted_gear | INTEGER | YES | NULL | 预估档位 |
| actual_gear | INTEGER | YES | NULL | 实际档位 |
| work_progress | TEXT | YES | NULL | 工作进展 |
| issues | TEXT | YES | NULL | 存在问题 |
| review_status | INTEGER | YES | NULL | 审核状态（0：提交；1：通过；2：退回） |
| reviewer_id | VARCHAR | YES | NULL | 审核人ID |
| delete | BOOLEAN | YES | false | 删除标记 |
| operator | VARCHAR | YES | NULL | 操作人 |
| operator_id | VARCHAR | YES | NULL | 操作人ID |
| assigner | VARCHAR | YES | NULL | 交办人 |
| assigner_id | VARCHAR | YES | NULL | 交办人ID |
| check_status | VARCHAR | YES | NULL | 审核状态（1:任务审核；2：阶段性审核；3：报表审核；4：指标审核），用逗号分隔 |
| leading_department | VARCHAR | YES | NULL | 牵头单位 |
| leading_department_id | VARCHAR | YES | NULL | 牵头单位ID |
| process_instance_id | VARCHAR | YES | NULL | 审核流水号 |
| process_instance_review_ids | TEXT | YES | NULL | 审核的人，使用逗号分隔 |
| gear_desc | TEXT | YES | NULL | 档位描述 |
| major_rule_change | TEXT | YES | NULL | 重大规则变动 |
| attachment | TEXT | YES | NULL | 附件 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### bz_form_target_record

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| target_id | INTEGER | YES | NULL | 目标ID |
| work_progress | TEXT | YES | NULL | 工作进展 |
| issue | TEXT | YES | NULL | 问题 |
| operator | VARCHAR | YES | NULL | 操作人 |
| operator_id | VARCHAR | YES | NULL | 操作人ID |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_by | VARCHAR | YES | NULL | 更新人 |
| process_instance_id | VARCHAR | YES | NULL | 审核流水号 |
| status | INTEGER | YES | NULL | 状态（1：审核中；2：审核完成） |
| target_name | VARCHAR | YES | NULL | 目标名称 |
| predicted_gear | INTEGER | YES | NULL | 预估档位 |

### bz_instruction

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| bz_form_id | INTEGER | YES | NULL | 表单ID |
| bz_issue_id | INTEGER | YES | NULL | 问题ID |
| bz_target_id | INTEGER | YES | NULL | 目标ID |
| reviewer_id | VARCHAR | YES | NULL | 审核人ID |
| reviewer | VARCHAR | YES | NULL | 审核人 |
| content | TEXT | YES | NULL | 内容 |
| review_time | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 审核时间 |

### public.bz_type

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| name | VARCHAR | YES | NULL | 名称 |
| description | TEXT | YES | NULL | 描述 |
| type | VARCHAR | YES | NULL | 类型 |
| type_id | INTEGER | YES | NULL | 类型ID |
| delete | BOOLEAN | YES | false | 删除标记 |
| order_num | INTEGER | YES | NULL | 排序 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |

### duban_check

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| task_id | BIGINT | YES | NULL | 任务ID |
| stage_id | BIGINT | YES | NULL | 阶段ID |
| bz_form_id | BIGINT | YES | NULL | 表单ID |
| bz_issue_id | BIGINT | YES | NULL | 问题ID |
| bz_form_target_id | BIGINT | YES | NULL | 表单目标ID |
| bz_issue_target_id | BIGINT | YES | NULL | 问题目标ID |
| data_json | TEXT | YES | NULL | 数据JSON |
| status | INTEGER | YES | NULL | 状态（1：审核中；2：通过审核；3：驳回） |
| check_type | INTEGER | YES | NULL | 审核类型<br>1：任务进度提交审核<br>2：任务阶段性目标提交审核<br>3：885清单列表详情修改提交审核<br>4：885清单列表指标修改提交审核<br>5：885问题列表详情修改提交审核<br>6：885问题列表指标修改提交审核<br>7：任务办结提交审核<br>8：任务终结提交审核 |
| operator | VARCHAR | YES | NULL | 操作人 |
| operator_id | VARCHAR | YES | NULL | 操作人ID |
| process_instance_id | VARCHAR | YES | NULL | 流程实例ID |
| flow_id | VARCHAR | YES | NULL | 流程ID |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### check_history

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| check_id | INTEGER | YES | NULL | 审核ID |
| reviewer | VARCHAR | YES | NULL | 审核人 |
| reviewer_id | INTEGER | YES | NULL | 审核人ID |
| role | VARCHAR | YES | NULL | 角色 |
| role_code | VARCHAR | YES | NULL | 角色代码 |
| review_status | INTEGER | YES | NULL | 审核状态 |
| review_comment | TEXT | YES | NULL | 审核意见 |
| review_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 审核时间 |

### consultation

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| content | TEXT | YES | NULL | 咨询内容 |
| person | VARCHAR | YES | NULL | 咨询人 |
| phone | VARCHAR | YES | NULL | 联系电话 |
| create_time | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| leading_department | VARCHAR | YES | NULL | 牵头部门 |
| expert | VARCHAR | YES | NULL | 专家 |

### ducha_report

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 自增主键 |
| tasks | TEXT | YES | NULL | 任务字段 |
| report_name | VARCHAR | YES | NULL | 报告名字 |
| submitter | VARCHAR | YES | NULL | 报送人 |
| submitter_id | VARCHAR | YES | NULL | 报送人ID |
| is_submitted | BOOLEAN | YES | false | 是否报送，默认为未报送 |
| is_deleted | BOOLEAN | YES | false | 是否删除，默认为未删除 |
| leading_official | VARCHAR | YES | NULL | 报送领导 |
| leading_official_id | VARCHAR | YES | NULL | 领导ID，使用字符串类型 |
| report_file | VARCHAR | YES | NULL | 报告文件路径，假设以字符串形式存储 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| period | INTEGER | YES | NULL | 期数字段 |
| task_count | INTEGER | YES | NULL | 任务总数 |
| in_progress_count | INTEGER | YES | NULL | 推进中的任务数 |
| overdue_count | INTEGER | YES | NULL | 超期的任务数 |
| issue_count | INTEGER | YES | NULL | 存在问题的任务 |
| complete_on_time_count | INTEGER | YES | NULL | 按时完结 |
| complete_count | INTEGER | YES | NULL | 完结任务 |

### external_task

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| content | TEXT | YES | NULL | 内容 |
| deadline | VARCHAR | YES | NULL | 截止时间 |
| update_by | VARCHAR | YES | NULL | 更新人 |
| leading_department | VARCHAR | YES | NULL | 牵头部门 |
| create_time | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |

### field

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| name | VARCHAR | YES | NULL | 名称 |
| description | TEXT | YES | NULL | 描述 |
| parent_id | INTEGER | YES | NULL | 父节点 |
| delete | BOOLEAN | YES | false | 删除标记 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### instruction

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| task_id | INTEGER | YES | NULL | 任务ID |
| bz_form_id | BIGINT | YES | NULL | 表单ID |
| bz_issue_id | BIGINT | YES | NULL | 问题ID |
| bz_form_target_id | BIGINT | YES | NULL | 表单目标ID |
| bz_issue_target_id | BIGINT | YES | NULL | 问题目标ID |
| reviewer_id | VARCHAR | YES | NULL | 审核人ID |
| reviewer | VARCHAR | YES | NULL | 审核人 |
| content | TEXT | YES | NULL | 内容 |
| review_time | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 审核时间 |

### instruction_reply

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| instruction_id | INTEGER | YES | NULL | 批示ID |
| reply_content | TEXT | YES | NULL | 回复内容 |
| operator | VARCHAR | YES | NULL | 操作人 |
| operator_id | VARCHAR | YES | NULL | 操作人ID |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |

### membership

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| leading_department | VARCHAR | YES | NULL | 牵头部门 |
| leading_department_id | VARCHAR | YES | NULL | 牵头部门ID |
| responsible_person | VARCHAR | YES | NULL | 责任人 |
| responsible_person_id | VARCHAR | YES | NULL | 责任人ID |
| priority | INTEGER | YES | NULL | 优先级 |
| phone | VARCHAR | YES | NULL | 电话 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### progress_report

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| task_id | BIGINT | YES | NULL | 任务ID |
| stage_node_id | INTEGER | YES | NULL | 阶段节点ID |
| progress | TEXT | YES | NULL | 进展情况 |
| issues_and_challenges | TEXT | YES | NULL | 问题与挑战 |
| requires_coordination | BOOLEAN | YES | false | 是否需要协调 |
| next_steps | TEXT | YES | NULL | 下一步计划 |
| handler | VARCHAR | YES | NULL | 处理人 |
| phone | VARCHAR | YES | NULL | 联系电话 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| status | INTEGER | YES | NULL | 状态<br>1: 已撤回<br>2：撤回失败<br>3：完成（包括审核成功状态）<br>4：审核包括审核中<br>5: 审核失败 |
| revoke_desc | TEXT | YES | NULL | 撤回描述字段 |
| tb_file_url | VARCHAR | YES | NULL | 通报文件 |
| check_id | BIGINT | YES | NULL | 审核ID |
| submit_id | VARCHAR | YES | NULL | 提交人 |
| process_instance_id | VARCHAR | YES | NULL | 流程实例ID |
| flow_id | VARCHAR | YES | NULL | 流程ID |
| update_by | VARCHAR | YES | NULL | 更新人 |
| delete | BOOLEAN | YES | false | 删除标记，默认值为false |
| delete_by | VARCHAR | YES | NULL | 删除人 |
| delete_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 删除时间 |

### stage_node

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | SERIAL | NOT NULL | 无 | 主键，自增ID |
| task_id | INTEGER | YES | NULL | 任务ID |
| stage_goal | TEXT | YES | NULL | 阶段目标 |
| deadline | DATE | YES | NULL | 截止日期 |
| status | INTEGER | YES | NULL | 状态<br>1: 正常推进<br>2：完成<br>3：已逾期（废除，使用OverdueDays记录逾期）<br>4: 审核中（办结申请，承办领导审批） |
| overdue_days | INTEGER | YES | NULL | 逾期天数字段，当状态为3时，计算超期天数 |
| process_instance_id | VARCHAR | YES | NULL | 审核流水号 |
| cb_done_desc | TEXT | YES | NULL | 承办人办结申请描述 |
| cb_done_file | VARCHAR | YES | NULL | 申请文件 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |

### task

| 字段名 | 类型 | 允许为空 | 默认值 | 描述 |
| --- | --- | --- | --- | --- |
| id | BIGSERIAL | NOT NULL | 无 | 主键，自增ID |
| task_type | INTEGER | YES | NULL | 任务类型<br>0：督查室任务（现在用的）<br>1：我的交办和承办任务 |
| source | VARCHAR | YES | NULL | 任务来源 |
| content | TEXT | YES | NULL | 任务内容 |
| leading_official | VARCHAR | YES | NULL | 牵头区领导 |
| leading_official_id | VARCHAR | YES | NULL | 牵头区领导id |
| leading_official_order | INTEGER | YES | NULL | 牵头区领导排序 |
| leading_department | VARCHAR | YES | NULL | 牵头单位 |
| leading_department_id | VARCHAR | YES | NULL | 牵头单位ID |
| leading_department_order | INTEGER | YES | NULL | 牵头单位排序 |
| deadline | DATE | YES | NULL | 完成时间 |
| created_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 创建时间 |
| progress | TEXT | YES | NULL | 具体进展 |
| issues_and_challenges | TEXT | YES | NULL | 存在的问题或者困难 |
| requires_coordination | BOOLEAN | YES | NULL | 是否需要领导协调解决 |
| instruction | TEXT | YES | NULL | 批示 |
| status | INTEGER | YES | NULL | 任务状态<br>优化前：1：待接收；2：正常推进；3：已超期；4：审核中（承办领导）；5：审核中（交办人）；6：已完成；7：取消审核中（承办领导）；8：取消审核中（交办人）；9：已终止<br>优化后：1：待接收；2：正常推进；6：已完成；9：已终止；12：审核中 |
| overdue_days | INTEGER | YES | NULL | 逾期天数字段，当状态为3时，计算超期天数 |
| is_urgent | BOOLEAN | YES | NULL | 是否催办 |
| is_review | BOOLEAN | YES | NULL | 是否需要承办领导审核 |
| next_steps | TEXT | YES | NULL | 下一个阶段 |
| handler | VARCHAR | YES | NULL | 经办人 |
| handler_id | VARCHAR | YES | NULL | 经办人id |
| phone | VARCHAR | YES | NULL | 电话 |
| updated_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 更新时间 |
| completed_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 办结时间 |
| source_date | DATE | YES | NULL | 来源时间（到天） |
| responsible_person | VARCHAR | YES | NULL | 责任人 |
| responsible_person_id | VARCHAR | YES | NULL | 责任人ID |
| task_period | INTEGER | YES | NULL | 任务周期<br>1:短期；2：中期；3：长期；4：大于6个月 |
| field_id | INTEGER | YES | NULL | 所属领域（int类型） |
| field_ids | VARCHAR | YES | NULL | 所属领域，第一级ID列表 |
| field_second_ids | VARCHAR | YES | NULL | 第二级领域 ID 列表 |
| field_third_ids | VARCHAR | YES | NULL | 第三级领域 ID 列表 |
| field_names | VARCHAR | YES | NULL | 领域名称列表 |
| co_organizer | VARCHAR | YES | NULL | 协办单位 |
| co_organizer_id | VARCHAR | YES | NULL | 协办单位id |
| cb_done_desc | TEXT | YES | NULL | 承办人办结申请描述 |
| cb_done_file | VARCHAR | YES | NULL | 申请文件 |
| closure_review_result | INTEGER | YES | NULL | 办结审核结果 |
| closure_review_desc | TEXT | YES | NULL | 办结审核描述 |
| closure_review_file | VARCHAR | YES | NULL | 办结审核文件 |
| cancel_desc | TEXT | YES | NULL | 取消描述 |
| cancel_file | VARCHAR | YES | NULL | 取消文件 |
| assigner | VARCHAR | YES | NULL | 交办人 |
| assigner_id | VARCHAR | YES | NULL | 交办人Id |
| undertaker | VARCHAR | YES | NULL | 承办人 |
| undertaker_id | VARCHAR | YES | NULL | 承办人id |
| tb_file_url | VARCHAR | YES | NULL | 同步文件url |
| fill_cycle | INTEGER | YES | NULL | 填报周期 |
| is_filled | BOOLEAN | YES | NULL | 是否填报 |
| check_status | VARCHAR | YES | NULL | 审核状态<br>1: 任务审核；2：阶段性审核；3：报表审核；4：指标审核，用逗号分割 |
| process_instance_id | VARCHAR | YES | NULL | 审核流水号 |
| process_instance_report_id | VARCHAR | YES | NULL | 填报审核流水号 |
| process_instance_review_ids | VARCHAR | YES | NULL | 审核的人，使用逗号分割 |
| delete | BOOLEAN | YES | false | 删除标记，默认值为false |
| delete_by | VARCHAR | YES | NULL | 删除人 |
| delete_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 删除时间 |
| operator | VARCHAR | YES | NULL | 操作人 |
| operation_at | TIMESTAMP WITHOUT TIME ZONE | YES | NULL | 操作时间 |
| count_down_type | INTEGER | YES | NULL | 倒计时类型<br>1:三个月后到期、2:一个月后到期、3:半个月后到期、4:一周后到期 |
| count_down | DATE | YES | NULL | 倒计时日期 |
| count_down_days | INTEGER | YES | NULL | 倒计时天数 |

































