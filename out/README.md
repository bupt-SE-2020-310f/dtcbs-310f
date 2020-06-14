##### 运行前准备
1. 修改`/conf/database.properties`文件中数据库连接属性
> 开发时默认使用mysql，支持修改为sqlserver
2. 创建本地数据库`dtcbs310f`
3. 不需要手动建表

##### 运行方式
1. 提供3个可执行jar包程序，可在命令行使用`java -jar xxx.jar`运行或者双击运行
2. `dtcbs-310f`为服务器后台程序，双击运行无界面
3. `ClientUI`为客户端程序，图形化界面
4. `ServerUI`为服务端程序，图形化界面，相当于远程连接工具

##### 补充说明
1. `ClientUI`和`ServerUI`的接入以`dtcbs-310f`先成功运行为基础
2. 为方便使用，`ServerUI`关闭时会主动通知`dtcbs-310f`关闭