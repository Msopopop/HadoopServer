
# 搭建Hadoop集群

该程序需要[Hadoop]、[Hbase]、[Zookeeper]、[JDK](>= 8)才能正常工作。

该程序在*CentOS 7*及*Debian 8*上部署测试成功。

## 前期工作
### 网络配置

若使用IP配置，所有机器的私有地址需要为静态地址。
1. 修改主机名

    对于centOS用户，执行
    ``shell
    $ sudo hostnamectl set-hostname HOST_NAME
    ```
    
    对于Debian用户，将`HOSTNAME=HOST_NAME`写入`/etc/hostname`后`$ sudo reboot`即可。
    
    所有机器（master及slave）都建议修改，此步骤可以省略，但是必须要在`/etc/hosts`中加入机器名所对应的私有IP（注意不是127.0.0.1）。
   
2. 设置Hosts

    假设集群master（或者叫NameNode）的私有IP为`172.19.120.35`，slave服务器群的私有IP为

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
    HOST_NAME 127.19.120.xx
    ```
    
### 用户配置

1. 建立用户及组

    可以建立一个专门的用户来运行Hadoop、HBase、Zookeeper及相关进程：
    
    此步骤可省略。
    ```shell
    $ sudo groupadd hadoopGrp
    $ sudo useradd -s /bin/bash \
                   -d /home/hadoop \
                   -m hadoop \
                   -g hadoopGrp
    $ sudo passwd hadoop
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
    
    ```shell
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
    
        ```shell
        $ sudo su hadoop
        $ ssh slave1
        ```
        输入密码成功登陆后输入`exit`退出；
        
    2. 生成RSA Key
    
        ```shell
        $ mkdir .ssh
        $ cd ~/.ssh
        $ ssh-keygen -t rsa
        $ cat id_rsa.pub >> authorized_keys
        ```
        查看`authorized_keys`文件，应有类似内容
        ```
        ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDBJiVLWYXAuaSDX9dyfCS7q1XqPGrH/jD1mZ5gfQjBrtkd/AqBs5YQpY1mGdY102r2HrLEYlALaWekqKBXo8AOOyIUmYN+qAlSbtDGIAB17JOp20LtH27lzuaPUcTeK5NhtA21YC5xVuYvUy+BY9rBs8gHRhEIbO7aAWCPXsQ4TFnoH/eE9A+CVe90cBBUBLjpyrW7bhBvA+L4gHYAw9qZOdtMnEdaw3vCPf5iKYYrG0biC7U/TR9leRE1KEIDlNxKCHML2yBdAmvAjBFg9km7IIlegFiojmeS58001p2ib4d/DnnViSKa0awOTru5ocy+iQWjTnX5s4JthPSknQcn hadoop@master
        ```
        
    3. 为slave机器设置SSH Key登录
    
        在master节点上执行
        ```shell
        $ ssh hadoop@slave1 'mkdir ~/.ssh'
        $ scp authorized_keys hadoop@slave1:~/.ssh
        //For each slave machine
        ... 
        ```
        
        在复制完后，可以用`ssh slave1`命令来测试是否配置成功。
        
        如果无误，应该不会出现`Permission denied (publickey).`这样的错误。
        
## 安装环境

每台机器需要安装的组件列表及信息如下：

| 机器类型 | IP | 组件 |
| :------:| :------: | :------: |
| master | 172.19.120.35 | Hadoop、HBase、JDK |
| slave | 172.19.120.36~135 | Hadoop、HBase、Zookeeper、JDK |

每种组件的安装路径如下，如不同请自行调整对应的配置文件：

| 组件 | 路径 |
| :------| :------ |
| Hadoop | /home/hadoop/hadoop-2.6.0/ |
| HBase | /home/hadoop/hbase-1.0.0/ |
| Zookeeper | /home/hadoop/zookeeper-3.4.6/ |
| JDK | /usr/local/jdk-1.8.0_192/ |

**Hadoop/HBase/Zookeeper版本有搭配要求，请勿盲目选择最新的发行版！**

详见[Apache HBase Configuration](https://hbase.apache.org/book.html#basic.prerequisites)的4.1小节

### JDK

在Oracle[网站][JDK]下载JDK并解压至服务器：

```shell
$ cd /usr/local
$ wget https://download.oracle.com/otn-pub/java/jdk/8u192-b12/750e1c8617c5452694857ad95c3ee230/jdk-8u192-linux-x64
.tar.gz
$ tar zxvf jdk-8u192-linux-x64.tar.gz
```

### Hadoop

在[Apache Hadoop Releases Repo](https://archive.apache.org/dist/hadoop/common/)下载Hadoop-2.6.0并解压至服务器：

```shell
$ cd /usr/local
$ wget https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
$ tar zxvf hadoop-2.6.0.tar.gz
```
### HBase

### Zookeeper
    
License
---
EPL-2.0

   [hadoop]: <https://hadoop.apache.org/>
   [hbase]: <https://hbase.apache.org/>
   [sonoscape]: <http://www.sonoscape.com.cn/>
   [zookeeper]: <https://zookeeper.apache.org/>
   [JDK]:<https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>