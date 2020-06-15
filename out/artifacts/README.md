##### 运行前准备
1. 修改`/conf/database.properties`文件中数据库连接属性
> 数据库平台使用mysql
2. 创建本地数据库`dtcbs310f`
3. 不需要手动建表

##### 说明
1. 提供3个可执行jar包程序，可在命令行使用`java -jar xxx.jar`运行或者双击运行
2. `dtcbs-310f`为服务器后台程序，双击运行无界面
3. `ClientUI`为客户端程序，图形化界面
4. `ServerUI`为服务端程序，图形化界面，相当于远程连接工具
5. `dtcbs-310f`需要与`conf`文件夹在同一目录下运行

##### 推荐运行方式
1. 进入路径，命令行运行`java -jar dtcbs-310f.jar`
2. 双击运行`ClientUI.jar`（可多个）和`ServerUI.jar`

##### 推荐退出方式
1. 先将所有处于PowerOn状态的`ClientUI`执行PowerOff
2. 将所有处于CheckIn状态的`ClientUI`执行CheckOut，然后关闭窗口
3. 最后关闭`ServerUI`窗口，会主动尝试自动关闭`dtcbs-310f`

##### 注意事项
！！！在`dtcbs-310f`关闭后仍处于CheckIn状态未关闭的`ClientUI`将会引起下次运行`dtcbs-310f`报错！！！
