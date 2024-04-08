---

## 项目简介

这次分享个人博客系统，界面简洁大气，功能齐全，是不可多得的比较容易的系统，非常适合毕业设计或者课程设计。

本系统基于**JSP+Servlet+JDBC+Mysql**。涉及技术少，易于理解，适合**JavaWeb 初学者**学习使用。

**难度等级：新手**

## 技术栈

### 编辑器

IntelliJ IDEA 2019.1.1 (Ultimate Edition)

### 前端技术

基础：html+css+JavaScript

框架：[BootStrap](https://www.bootcss.com/)+[JQuery](https://jquery.com/)

### 后端技术

Jsp+Servlet

数据库：mysql 5.7.27（个人测试使用）

jdk 版本：1.8.0_181（个人测试使用）

tomcat 版本：8.5.34（个人测试使用）

## 本地运行

1.下载 zip 直接解压或安装 git 后执行克隆命令

2.使用 Idea 打开项目，配置 jdk、tomcat 和所需 jar 包。
项目所依赖 jar 包在**web/WEB-INF/lib**文件夹下。

3.打开 Navicat For Mysql，创建 blog 数据库，复制**blog.sql**中的文件内容运行。

4.修改**jdbc.properties**中数据库相关的内容。

5.发布到 tomcat 中，具体访问链接看 tomcat 配置，若未修改则[http://localhost:8080/](http://localhost:8080/)为首页。
[http://localhost:8080/login.html](http://localhost:8080/login.html)为控制台页面。
系统中不存在初始账号，可以**在 user 表中新增一个用户**即可。

## 注意

-   该项目未声明 mysql、jdk、tomcat 使用版本，以上版本号均为个人测试使用版本。
-   注意**修改 jdbc.properties 中数据库相关的内容。**
-   系统中不存在初始账号，可以**在 user 表中新增一个用户**即可。

## 项目截图

![image-20240408下午85354172](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85354172.png)

![image-20240408下午85832612](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85832612.png)

![image-20240408下午85848377](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85848377.png)

![image-20240408下午85907793](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85907793.png)

![image-20240408下午85921218](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85921218.png)

![image-20240408下午85933768](/Users/jose/personal/Code/back/ItemSet/Java/10005. 个人博客系统/README.assets/image-20240408下午85933768.png)
