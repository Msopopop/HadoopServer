# 基于Hadoop的DICOM信息解析、存储系统

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![Build Status](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer.svg?branch=master)](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer)

## 特性

该系统是基于Hadoop开发的Dicom解析系统，能够实现以下功能

  - 接受客户端发送的DICOM文件（通过监听特定文件路径），同时针对DICOM文件生成JPEG图片
  - 将DICOM文件以及JPEG图片存放到HDFS中
  - 解析DICOM文件并将信息存入HBase
  - 解析后端传送的GSPS文件并将标注信息存入对应DICOM文件的HBase中
  
> 医疗数位影像传输协定（DICOM）是一组通用的标准协定，在对于医学影像的处理、储存、打印、传输上。它包含了档案格式的定义及网络通信协定。DICOM是以TCP/IP为基础的应用协定，并以TCP/IP联系各个系统。两个能接受DICOM格式的医疗仪器间，可借由DICOM格式的档案，来接收与交换影像及病人资料。 
  
  该系统旨在能与所有兼容DICOM协议的设备兼容互通。

## 搭建Hadoop集群

该程序需要[Hadoop]、[Hbase]、[Zookeeper]、[JDK](>= 8)才能正常工作。

该程序在*CentOS 7*及*Debian 8*上部署测试成功。

### 前期工作
#### 网络配置

若使用IP配置，所有机器的私有地址需要为静态地址。
1. 修改主机名

    ```sh
    sudo hostnamectl set-hostname HOST_NAME
    ```
    所有机器（master及slave）都要修改，此步骤可以省略，但是要在`/etc/hosts`中加入机器名所对应的私有IP。
2. 设置Hosts

    假设集群master的私有IP为`172.19.120.35`，slave服务器群的私有IP为

    ```
    172.19.120.36 ~ 172.19.120.135
    ```
    
    在所有集群机器的`/etc/hosts`中追加以下内容
    ```
    master 172.19.120.35
    slave1 172.19.120.36
    slave2 172.19.120.37
    slave3 172.19.120.38
    ...
    slave100 172.19.120.135
    
    #如果你没有设置hostname
    HOST_NAME 127.19.120.xx
    ```
    
#### 用户配置

1. 建立用户及组

    可以建立一个专门的用户来运行Hadoop、HBase、Zookeeper及相关进程：
    
    此步骤可省略。
    ```sh
    $ sudo groupadd hadoopGrp //Create a user group
    $ sudo useradd -s /bin/bash \
                   -d /home/hadoop \
                   -m hadoop \
                   -g hadoopGrp
    $ sudo passwd hadoop //Setup password
    ```
    
2. 设置权限
    
    修改所有机器的`/etc/sudoers`，添加以下内容
    ```
    #For Debian
    hadoop ALL=(ALL:ALL) ALL
    
    #For CentOS
    hadoop ALL=(ALL) ALL
    ```
    
3. 设置防火墙

    在配置前可以关闭所有机器的防火墙，在配置后再开启
    
    ```sh
    #For CentOS
    $ sudo systemctl stop firewalld.service
    $ sudo systemctl disable firewalld.service
    
    #For Debian
    $ chkconfig iptables off
    ```
    
    对于CentOS还需要关闭selinux，修改`/etc/selinux/config`中内容如下
    ```
    SELINUX=disabled
    ```
    
4. SSH登录
    
    1. 在master节点上执行
    
        ```sh
        $ sudo su hadoop
        $ ssh slave1
        ```
        输入密码成功登陆后输入`exit`退出；
        
    2. 生成RSA Key
    
        ```sh
        $ mkdir .ssh
        $ cd ~/.ssh
        $ ssh-keygen -t rsa
        $ cat id_rsa.pub >> authorized_keys
        ```
        查看`authorized_keys`文件，应有类似内容
        ```
        ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDBJXXJthPSknQcn hadoop@master
        ```
        
    3. 为slave机器设置SSH Key登录
    
        在master节点上执行
        ```sh
        $ ssh hadoop@slave1 'mkdir ~/.ssh'
        $ scp authorized_keys hadoop@slave1:~/.ssh
        //For each slave machine
        ... 
        ```
        
        在复制完后，可以用`ssh slave1`命令来测试是否配置成功。
        
        如果无误，应该不会出现`Permission denied (publickey).`这样的错误。
        
### 安装环境

#### JDK

在Oracle[网站][JDK]下载JDK并解压至服务器：

```sh
$ cd /usr/local
$ wget https://download.oracle.com/otn-pub/java/jdk/8u192-b12/750e1c8617c5452694857ad95c3ee230/jdk-8u192-linux-x64
.tar.gz
$ tar zxvf jdk-8u192-linux-x64.tar.gz
```

#### Hadoop

在[Apache Hadoop Releases Repo](https://archive.apache.org/dist/hadoop/common/)下载Hadoop-2.6.0并解压至服务器：

**Hadoop/HBase/Zookeeper版本有搭配要求，请勿盲目选择最新的发行版，详见[Apache HBase Configuration](https://hbase.apache.org/book.html#basic.prerequisites)**
```sh
$ cd /usr/local
$ wget https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
$ tar zxvf hadoop-2.6.0.tar.gz
```
#### HBase

#### Zookeeper


### Plugins

Dillinger 

is currently extended with the following plugins. Instructions on how to use them in your own application are 
linked below.

| Plugin | README |
| ------ | ------ |
| Dropbox | [plugins/dropbox/README.md][PlDb] |
| Github | [plugins/github/README.md][PlGh] |
| Google Drive | [plugins/googledrive/README.md][PlGd] |
| OneDrive | [plugins/onedrive/README.md][PlOd] |
| Medium | [plugins/medium/README.md][PlMe] |
| Google Analytics | [plugins/googleanalytics/README.md][PlGa] |


### Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantanously see your updates!

Open your favorite Terminal and run these commands.

First Tab:
```sh
$ node app
```

Second Tab:
```sh
$ gulp watch
```

(optional) Third:
```sh
$ karma test
```
### Building for source
For production release:
```sh
$ gulp build --prod
```
Generating pre-built zip archives for distribution:
```sh
$ gulp build dist --prod
```

## Todos

 该系统将来会实现的功能
   - SCP服务器功能
   - 针对多帧DICOM文件生成多帧JPEG图片
   - 追加多次GSPS标记
   - 对标记内容做数据挖掘
   
   
## 致谢

该程序使用了以下开源组件（见[pom.xml][pom]），在此表示感谢：

* [hadoop] - The Apache Hadoop software library is a framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models.
* [hbase] - Apache HBase™ is the Hadoop database, a distributed, scalable, big data store. 
* [zookeeper] - Apache ZooKeeper is an effort to develop and maintain an open-source server which enables highly reliable distributed coordination.
* [weasis] - Weasis is a DICOM viewer available as a desktop application or as a web-based application.
* [dcm4che] - A collection of open source applications and utilities for the healthcare enterprise.
* [log4j] - Apache Log4j 2 is an upgrade to Log4j that provides significant improvements over its predecessor, Log4j 1.x, and provides many of the improvements available in Logback while fixing some inherent problems in Logback’s architecture.
* [Gaoyp12138-dicom] - 提供了[DicomParseUtils.java][DicomParseUtils]的大部分内容
* [Dillinger] - A awesome markdown editor.

本项目还得到了西安交通大学[生命科学与技术学院][slst]、[深圳开立生物技术有限公司][sonoscape]相关人员的指导，在此一并表示感谢。

License
---
EPL-2.0

   [Weasis]: <https://github.com/nroduit/Weasis>
   [Dcm4che]: <https://www.dcm4che.org/>
   [log4j]: <https://logging.apache.org/log4j/2.x/>
   [hadoop]: <https://hadoop.apache.org/>
   [hbase]: <https://hbase.apache.org/>
   [sonoscape]: <http://www.sonoscape.com.cn/>
   [zookeeper]: <https://zookeeper.apache.org/>
   [Gaoyp12138-dicom]: <https://github.com/Gaoyp12138/dicom>
   [Dillinger]: <https://dillinger.io>
   [slst]:<http://slst.xjtu.edu.cn>
   [JDK]:<https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>
   [pom]: <https://github.com/sonoscape-HadoopProject-xjtu/HadoopServer/blob/master/pom.xml#L124>
   [DicomParseUtils]: <https://github.com/sonoscape-HadoopProject-xjtu/HadoopServer/blob/master/src/main/java/Utils/DicomParseUtil.java>
