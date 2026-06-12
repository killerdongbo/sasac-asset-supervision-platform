# 企业资产管理系统 — API 接口设计文档

> 文档版本：v1.0  
> 更新时间：2026-04-27  
> 基础路径：`/api/v1`  
> 作者：知微

---

## 1. 通用规范

### 1.1 认证方式

除登录接口外，所有接口通过 Header 传递 JWT：

```http
Authorization: Bearer <access_token>
```

### 1.2 统一响应

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "success": true
}
```

### 1.3 错误码

| code | 含义 |
|---|---|
| 0 | 成功 |
| 1001 | 业务异常 |
| 1002 | 参数校验失败 |
| 4001 | 未登录或 token 无效 |
| 4003 | 权限不足 |
| 4040 | 资源不存在 |
| 9999 | 系统异常 |

### 1.4 分页参数

| 参数 | 类型 | 默认 | 说明 |
|---|---|---|---|
| page | int | 1 | 页码 |
| page_size | int | 20 | 每页数量，最大 100 |

### 1.5 分页响应

```json
{
  "total": 100,
  "page": 1,
  "page_size": 20,
  "items": []
}
```

---

## 2. 认证接口

### 2.1 登录

`POST /auth/login`

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "access_token": "jwt-token",
  "token_type": "bearer",
  "expires_in": 604800
}
```

### 2.2 当前用户信息

`GET /auth/me`

响应：

```json
{
  "id": 1,
  "username": "admin",
  "real_name": "管理员",
  "roles": [{"id": 1, "role_code": "super_admin"}],
  "permissions": ["asset:list", "asset:create"]
}
```

---

## 3. 用户与权限接口

### 3.1 用户列表

`GET /users/list?page=1&page_size=20&keyword=张三`

### 3.2 创建用户

`POST /users`

```json
{
  "username": "zhangsan",
  "password": "123456",
  "real_name": "张三",
  "phone": "13800000000",
  "department_id": 1
}
```

### 3.3 更新用户

`PUT /users/{user_id}`

```json
{
  "real_name": "张三",
  "phone": "13800000000",
  "department_id": 1,
  "is_active": true
}
```

### 3.4 删除用户

`DELETE /users/{user_id}`

### 3.5 角色接口

| 接口 | 方法 | 说明 |
|---|---|---|
| `/users/roles` | GET | 角色列表 |
| `/users/roles` | POST | 创建角色 |
| `/users/roles/{role_id}` | PUT | 更新角色 |
| `/users/roles/{role_id}` | DELETE | 删除角色 |

### 3.6 权限与部门

| 接口 | 方法 | 说明 |
|---|---|---|
| `/users/permissions` | GET | 权限树 |
| `/users/departments` | GET | 部门树 |

---

## 4. 资产基础接口

### 4.1 资产分类列表

`GET /assets/categories`

响应为树形结构。

### 4.2 创建资产分类

`POST /assets/categories`

```json
{
  "name": "办公设备",
  "code": "office_device",
  "asset_type": "fixed",
  "parent_id": null,
  "sort_order": 0
}
```

### 4.3 资产位置列表

`GET /assets/locations`

### 4.4 资产列表

`GET /assets?page=1&page_size=20&keyword=电脑&asset_type=fixed&status=normal&category_id=1`

响应：

```json
{
  "total": 1,
  "page": 1,
  "page_size": 20,
  "items": [
    {
      "id": 1,
      "asset_code": "FA-20260427-000001",
      "name": "联想笔记本",
      "asset_type": "fixed",
      "status": "normal"
    }
  ]
}
```

### 4.5 创建资产

`POST /assets`

```json
{
  "name": "联想笔记本",
  "asset_type": "fixed",
  "category_id": 1,
  "specification": "ThinkPad T14",
  "manufacturer": "Lenovo",
  "purchase_date": "2026-04-27",
  "purchase_price": 5999.00,
  "location_id": 1,
  "department_id": 1,
  "extra_attrs": {}
}
```

### 4.6 资产详情/更新/删除

| 接口 | 方法 | 说明 |
|---|---|---|
| `/assets/{asset_id}` | GET | 资产详情 |
| `/assets/{asset_id}` | PUT | 更新资产 |
| `/assets/{asset_id}` | DELETE | 删除资产 |

---

## 5. 巡检接口

### 5.1 巡检任务列表

`GET /inspections/tasks?page=1&page_size=20&status=processing`

### 5.2 创建巡检任务

`POST /inspections/tasks`

```json
{
  "task_name": "4月办公设备巡检",
  "scope_type": "location",
  "scope_config": {"location_ids": [1, 2]},
  "start_date": "2026-04-27",
  "end_date": "2026-04-30",
  "assignee_ids": [3, 4],
  "remarks": "重点检查办公区电脑"
}
```

### 5.3 巡检任务详情

`GET /inspections/tasks/{task_id}`

### 5.4 发布巡检任务

`POST /inspections/tasks/{task_id}/publish`

### 5.5 开始巡检

`POST /inspections/tasks/{task_id}/start`

### 5.6 提交巡检明细结果

`POST /inspections/tasks/{task_id}/items/{item_id}/result`

```json
{
  "result_type": "abnormal",
  "actual_location_id": 2,
  "actual_status": "repair",
  "abnormal_type": "damaged",
  "abnormal_desc": "屏幕损坏",
  "photos": ["minio://assets/photo1.jpg"]
}
```

### 5.7 完成巡检任务

`POST /inspections/tasks/{task_id}/complete`

### 5.8 我的巡检任务

`GET /inspections/my`

---

## 6. 盘点接口

### 6.1 盘点任务列表

`GET /inventories/tasks?page=1&page_size=20&status=processing`

### 6.2 创建盘点任务

`POST /inventories/tasks`

```json
{
  "task_name": "2026年4月资产盘点",
  "scope_type": "department",
  "scope_config": {"department_ids": [1, 2]},
  "start_date": "2026-04-27",
  "end_date": "2026-05-05",
  "assignee_ids": [3, 4]
}
```

### 6.3 发布盘点任务

`POST /inventories/tasks/{task_id}/publish`

### 6.4 提交盘点结果

`POST /inventories/tasks/{task_id}/items/{item_id}/result`

```json
{
  "asset_code": "FA-20260427-000001",
  "actual_location_id": 2,
  "actual_status": "normal",
  "result_type": "location_mismatch",
  "diff_desc": "资产实际在二楼会议室"
}
```

### 6.5 批量提交盘点结果

`POST /inventories/tasks/{task_id}/batch-result`

```json
{
  "items": [
    {
      "asset_code": "FA-20260427-000001",
      "actual_location_id": 2,
      "actual_status": "normal"
    }
  ]
}
```

### 6.6 生成差异报告

`POST /inventories/tasks/{task_id}/generate-report`

### 6.7 查询差异报告

`GET /inventories/tasks/{task_id}/report`

### 6.8 提交差异调整

`POST /inventories/tasks/{task_id}/adjustments`

```json
{
  "item_id": 100,
  "adjustment_type": "update_location",
  "reason": "盘点确认资产位置已变更"
}
```

---

## 7. 审批接口

| 接口 | 方法 | 说明 |
|---|---|---|
| `/approvals/todo` | GET | 待我审批 |
| `/approvals/mine` | GET | 我发起的 |
| `/approvals/done` | GET | 我已审批 |
| `/approvals/{id}/approve` | POST | 审批通过 |
| `/approvals/{id}/reject` | POST | 审批驳回 |

---

## 8. 文件接口

| 接口 | 方法 | 说明 |
|---|---|---|
| `/files/upload` | POST | 上传文件到 MinIO |
| `/files/{file_id}` | GET | 获取文件信息 |
| `/files/{file_id}/download` | GET | 下载文件 |

---

## 9. 后续扩展接口

Phase 3/4 增加：采购、流转、维保维修、折旧、报表、预警、公告、供应商等接口。

### 9.1 资产全生命周期接口补充

生命周期主线：采购 → 入库 → 领用 → 调拨 → 盘点 → 报废。

| 接口 | 方法 | 说明 |
|---|---|---|
| `/flows` | GET | 查询资产流转单据，支持 `flow_type=stock_in/assign/transfer/disposal` 过滤 |
| `/flows` | POST | 创建资产流转单据，`flow_type` 区分入库、领用、调拨、报废 |
| `/flows/{id}/execute` | POST | 执行已审批或待执行单据，更新资产状态/位置/部门/责任人并写入生命周期 |
| `/assets/{id}/events` | GET | 查询资产生命周期事件 |

创建流转单请求示例：

```json
{
  "flow_type": "assign",
  "asset_id": 9001,
  "target_location_id": 9004,
  "target_department_id": 9002,
  "target_user_id": 9001,
  "reason": "研发人员领用办公电脑"
}
```

`flow_type` 枚举：

| 值 | 中文 | 执行结果 |
|---|---|---|
| `stock_in` | 入库 | 更新位置/部门，资产状态为正常 |
| `assign` | 领用 | 更新位置/部门/责任人，资产状态为借用中/使用中 |
| `transfer` | 调拨 | 更新位置/部门/责任人，资产状态保持不变 |
| `disposal` | 报废 | 资产状态为已报废 |
