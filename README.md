# NAMI：专为微信小程序服务端开发而生
*面向前端开发人员的傻瓜式后端服务框架*

## 什么是NAMI
微信小程序的前端框架，官方命名为MINA；那我们的非官方后端就呼应一下，姑且命名为NAMI（纳米）。<br/>
NAMI提供了开发一个小程序服务端所需的**所有**服务，**包括但不仅限于处理request请求、接收和处理websocket、与微信服务端交互并维护access_token、处理微信服务端登录鉴权、发送模板消息、接收微信支付事件**，等等。

## NAMI受众
### 前端开发工程师
不需了解JAVA、PHP或其他后端语言，不需要安装TOMCAT，不需要LAMP，简单的入门就可以为前端工程师开发的小程序搭配上强大的后端服务。

### 专注于业务实现，而不想纠结于技术的小程序后端开发者
也许你是有经验的JAVA或PHP程序员，但是leader给你开发小程序的时间不多了，面对着鉴权、支付、模板消息，看着微信官方文档的token心跳、加密解密，你头都大了。NAMI可以帮助你快速完成一个功能完整的小程序。

### JAVA程序员
NAMI采用纯粹的JAVA语言开发，对微信官方API进行封装，内置动态脚本引擎，对微信官方服务端API进行全封装。MINA也可以成为你JAVA项目的其中一个外部JAR包。

## NAMI特性
- 可直接运行于任意主流IAAS或PAAS或docker容器,如阿里云、网易蜂巢
- 内置JDK,内置tomcat,支持跨平台(windows/linux/macOS)
- javaEE技术架构，成熟的横向扩展方案，支持高并发、高可用系统需求，能支持大型/超大型系统
- 傻瓜化脚本开发模式,只关注业务逻辑,不纠结技术实现
- 支持javascript,groovy,java语言开发业务逻辑,总有一款合你意
- 微信API封装,消息、支付、鉴权简单实现

## NAMI关键模块
- request请求门面(REST模式)
- 配置化websocket
- 内置脚本逻辑引擎(groovy+javascript)
- 全量微信API封装
- 解压即可运行的容器式封装
- 小程序文件上传下载体系封装

## 开始使用(待补充)
- 编译/下载
- 启动
- hello 小程序!
- 进阶

## 关于我们
- woden ([https://github.com/wodenwang/](https://github.com/wodenwang/ "woden的github"))
<br/>
微信公众号: **全栈生姜头** 
<br/>
![](http://i.imgur.com/bfh9QVR.jpg)

- borball ([https://github.com/borball/weixin-sdk](https://github.com/borball/weixin-sdk "微信SDK"))
