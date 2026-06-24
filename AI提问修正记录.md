# AI 提问修正记录

## 修正记录 1：BorrowDao 缺少 Book 类导入

**问题描述**：
AI 首次生成的 `BorrowDao.java` 代码中，使用了 `Book` 类但遗漏了对应的 `import` 语句，导致编译报错。

**编译错误信息**：
```
BorrowDao.java:[36,13] 找不到符号
  符号:   类 Book
  位置: 程序包 com.booksystem.dao
```

**分析过程**：
1. 查看编译错误定位到 `BorrowDao.java` 第 36 行
2. 检查文件头部的 import 声明，发现只有 `BorrowRecord` 和 `DBUtil` 的导入
3. 在 `borrowBook()` 方法中使用了 `Book book = bookDao.getBookById(bookId);`，但 `Book` 类未导入

**修正方法**：
在 `BorrowDao.java` 的 import 区域添加：
```java
import com.booksystem.model.Book;
```

**修正后结果**：编译成功通过。

---

## 修正记录 2：BorrowServlet 中 JSON 解析方式优化

**问题描述**：
AI 首次生成的 `BorrowServlet` 中，使用 `Map<String, Integer>` 直接解析 JSON 请求体。由于 Gson 解析数字时默认为 `Double` 类型，导致 `.get("book_id")` 返回的是 `Double` 而非 `Integer`，运行时可能出现类型转换异常。

**分析过程**：
1. 测试借阅功能时发现，部分请求返回 `ClassCastException`
2. 检查日志发现 Gson 将 JSON 中的数字解析为 `Double` 类型
3. 前端发送 `{"book_id": 1, "reader_id": 1}`，Gson 解析后 Map 中的值是 `Double` 类型的 `1.0`

**修正方法**：
将 `BorrowServlet` 中的类型转换从：
```java
Integer bookId = paramMap.get("book_id");  // 可能为 null（类型不匹配）
```
改为安全的类型转换：
```java
Integer bookId = paramMap.get("book_id") != null ? ((Number) paramMap.get("book_id")).intValue() : null;
```

同时在 `BorrowDao.borrowBook()` 方法中增加对 `book.getStock() <= 0` 的提前检查，避免并发场景下的库存扣减失败。

**修正后结果**：借阅功能正常运行，事务控制正确，库存扣减与借阅记录插入同步成功或回滚。

---

## 总结

通过以上两次修正，体现了以下学习要点：
1. **Java 编译基础**：import 语句的完整性和必要性
2. **JSON 序列化特性**：Gson 默认将数字解析为 `Double`，需要显式类型转换
3. **事务控制思维**：在事务中操作需要确保连接一致性，使用 `ThreadLocal` 管理连接
4. **异常边界防御**：通过 try-catch + JSON 响应替代 Tomcat 默认错误页面
