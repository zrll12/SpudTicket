# SpudTicket

## If you need English version pleae go to:

- [README.en.md](https://github.com/zrll12/SpudTicket/blob/master/README.en.md)

## 介绍
一个适用于mc1.16及以上的spigot插件，用于售票，检票。

## 安装教程

1. 下载最新版发行版并放入plugins文件夹
2. 启动服务器
3. 游戏内运行/linkdb链接MySql数据库
4. 编辑config.yml配置其他选项并重启

## 告示牌格式
1. 售票相关：
- 第一行：[ticket]
- 第二行：获取交通卡：Get your spud++!；充值：Charge your spud++!；查询余额：Check money left
2. 闸机相关：
- 第一行：[gate]
- 第二行：站号(格式：“线路号:车站号”，例：“1.01”)
- 第三行：方向，结构：告示牌后面放任意方块，方块左边或右边放置栅栏门。左：<-；右：->
- 第四行：操作，进站：in；出站：out

## 特殊说明

1. 由于数据库用户名和密码加密存储，请在游戏内通过/linkdb来链接数据库
2. 告示牌左键使用，若需要破坏告示牌，请运行/breaksign，完成请运行/donebreaksign
3. 数据库表格使用：ticket存储公交卡信息，deals存储消费信息（进站，出站，充值）
4. 关于构建的版本（从v1.1开始）：
   (1)min：仅包含插件代码编译后的文件
   (2)complete：包含插件和md5算法编译后的文件（建议使用min版本无法加载后再选用此版本）

## 功能

1. 自动颁发交通卡
2. 出站自动扣费，进站最低余额10
3. 充值使用vault扣费

## 已知问题

1. 充值时自动输入的命令带有不能识别的后缀

## 待添加功能

1. 分段计价
2. 退卡
3. 单程票
4. 自动移除一定时间内未易且余额为0的卡

## 参与贡献

1.  Fork 本仓库
2.  提交代码
3.  新建 Pull Request
