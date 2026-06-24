# 图书管理系统 2.0 — 前后端异步交互 JSON 报文格式契约

## 1. 图书搜索接口

**请求方式**: `GET`
**请求路径**: `/api/search`
**请求参数** (URL Query):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 搜索关键词，模糊匹配书名/作者/ISBN |
| category_id | int | 否 | 分类ID，0或不传表示全部分类 |

**响应格式**:
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {
            "id": 1,
            "title": "Java编程思想",
            "author": "Bruce Eckel",
            "isbn": "978-0131872486",
            "categoryId": 1,
            "categoryName": "编程",
            "stock": 5,
            "coverPath": "uploads/abc123.jpg"
        }
    ]
}
```

---

## 2. 图书借阅接口

**请求方式**: `POST`
**请求路径**: `/api/borrow`
**请求头**: `Content-Type: application/json`
**请求体**:
```json
{
    "book_id": 1,
    "reader_id": 1
}
```

**成功响应**:
```json
{
    "code": 200,
    "msg": "借阅成功",
    "data": {
        "record_id": 1,
        "book_title": "Java编程思想",
        "reader_name": "张三",
        "borrow_time": "2026-06-24 20:30:00",
        "stock": 4
    }
}
```

**库存不足响应**:
```json
{
    "code": 400,
    "msg": "库存不足，无法借阅",
    "data": null
}
```

**异常响应**:
```json
{
    "code": 500,
    "msg": "借阅异常：图书不存在",
    "data": null
}
```

---

## 3. 图书归还接口

**请求方式**: `POST`
**请求路径**: `/api/return`
**请求头**: `Content-Type: application/json`
**请求体**:
```json
{
    "record_id": 1
}
```

**成功响应**:
```json
{
    "code": 200,
    "msg": "归还成功",
    "data": null
}
```

---

## 4. 分类查询接口

**请求方式**: `GET`
**请求路径**: `/api/categories`
**响应格式**:
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {"id": 1, "name": "编程"},
        {"id": 2, "name": "文学"},
        {"id": 3, "name": "历史"},
        {"id": 4, "name": "科学"},
        {"id": 5, "name": "艺术"}
    ]
}
```

---

## 5. 读者查询接口

**请求方式**: `GET`
**请求路径**: `/api/readers`
**响应格式**:
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {"id": 1, "name": "张三", "cardNo": "R2024001"},
        {"id": 2, "name": "李四", "cardNo": "R2024002"}
    ]
}
```

---

## 6. 图片上传接口

**请求方式**: `POST`
**请求路径**: `/api/upload`
**请求头**: `Content-Type: multipart/form-data`
**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件（jpg/jpeg/png/gif，最大5MB） |

**成功响应**:
```json
{
    "code": 200,
    "msg": "上传成功",
    "data": {
        "file_path": "uploads/a1b2c3d4.jpg",
        "file_name": "原始文件名.jpg"
    }
}
```

---

## 7. 添加图书接口

**请求方式**: `POST`
**请求路径**: `/api/book/add`
**请求头**: `Content-Type: application/json`
**请求体**:
```json
{
    "title": "新书名",
    "author": "作者",
    "isbn": "978-xxx",
    "category_id": 1,
    "stock": 5,
    "cover_path": "uploads/cover.jpg"
}
```

**成功响应**:
```json
{
    "code": 200,
    "msg": "添加图书成功",
    "data": null
}
```

---

## 统一响应格式约定

所有接口均返回以下 JSON 结构：

```json
{
    "code": 200,       // 200=成功, 400=客户端错误, 500=服务器异常
    "msg": "操作说明",  // 人类可读的提示信息
    "data": {}         // 业务数据，可为 null
}
```

前端通过 `code` 字段判断请求是否成功，通过 `msg` 字段展示给用户提示。
