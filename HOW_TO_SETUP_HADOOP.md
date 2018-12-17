
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

    ```text
    172.19.120.36 ~ 172.19.120.135
    ```
    
    在所有集群机器的`/etc/hosts`中追加以下内容
    ```text
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
    ```text
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
    ```text
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
        $ mkdir tmp
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
| slave1 | 172.19.120.36 | Hadoop、HBase、Zookeeper、JDK |
| slave2 | 172.19.120.37 | Hadoop、HBase、Zookeeper、JDK |
| ... | ... | ... |
| slave100 | 172.19.120.135 | Hadoop、HBase、Zookeeper、JDK |

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

在所有服务器上，下载[JDK][JDK]并解压至服务器：

```shell
$ cd /usr/local
$ sudo wget https://download.oracle.com/otn-pub/java/jdk/8u192-b12/750e1c8617c5452694857ad95c3ee230/jdk-8u192-linux-x64
.tar.gz
$ sudo tar zxvf jdk-8u192-linux-x64.tar.gz
```

### Hadoop
**在`master`服务器上**

- 使用`hadoop`用户（下同），在[Apache Hadoop Releases Repo](https://archive.apache.org/dist/hadoop/common/)下载Hadoop-2.6.0并解压至服务器：

    ```shell
    $ cd ~
    $ wget https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
    $ tar zxvf hadoop-2.6.0.tar.gz
    ```
    
- 修改`/home/hadoop/hadoop-2.6.0/etc/hadoop`目录下`hadoop-env.sh`文件内`JAVA_HOME`：
    ```shell
    export JAVA_HOME=/usr/local/jdk1.8.0_192
    ```

- 修改`/home/hadoop/hadoop-2.6.0/etc/hadoop`目录下`core-site.xml`内`fs.defaultFS`键值：
    ```xml
    <configuration>
        <property>
            <name>fs.defaultFS</name>
            <value>hdfs://master:9000</value>
        </property>
        <property>
            <name>hadoop.tmp.dir</name>
            <value>/home/hadoop/tmp</value>
        </property>
    </configuration>
    ```
    
- 修改`/home/hadoop/hadoop-2.6.0/etc/hadoop`目录下`hdfs-site.xml`内相关键值：
    ```xml
    <configuration>
        <property>
            <name>dfs.namenode.name.dir</name>
            <value>file:/home/hadoop/hadoop-2.6.0/dfs/name</value>
        </property>
        <property>
            <name>dfs.datanode.data.dir</name>
            <value>file:/home/hadoop/hadoop-2.6.0/dfs/data</value>
        </property>
        <property>
            <name>dfs.permissions</name>
            <value>false</value>
        </property>
    </configuration>
    ```
- 修改`/home/hadoop/hadoop-2.6.0/slaves`文件，在其中加入所有slave机器的域名或名称：
  
  ```text
  slave1
  slave2
  slave3
  ...
  ```

- 拷贝`hadoop-2.6.0`文件夹至所有slave机器：

  ```shell
  $ scp -r /home/hadoop/hadoop-2.6.0 slave1:/home/hadoop
  $ scp -r /home/hadoop/hadoop-2.6.0 slave2:/home/hadoop
  $ ...
  ```  

### HBase

**在`master`服务器上**

- 在[Apache HBase Releases Repo](https://archive.apache.org/dist/hbase/hbase-1.0.0/hbase-1.0.0-bin.tar.gz)下载Hbase-1.0.0并解压至服务器：

    ```shell
    $ cd ~
    $ wget https://archive.apache.org/dist/hbase/hbase-1.0.0/hbase-1.0.0-bin.tar.gz
    $ tar zxvf hbase-1.0.0-bin.tar.gz
    ```

- 类似地，修改`/home/hadoop/hbase-1.0.0/conf/hbase-env.sh`文件中的`JAVA_HOME`与`HBASE_MANAGES_ZK`：

    ```shell
    export HBASE_MANAGES_ZK=false
    export JAVA_HOME=/usr/local/jdk1.8.0_192
    ```

- 类似地，修改`/home/hadoop/hbase-1.0.0/conf/regionservers`文件，在其中加入所有slave机器的域名或名称：
    
    ```text
    slave1
    slave2
    slave3
    ...
    ```  
- 类似地，修改`/home/hadoop/hbase-1.0.0/conf/hbase-site.xml`文件，在其中加入相关信息：
     
     ```xml
     <configuration>
          <property>
              <name>hbase.rootdir</name>
              <value>hdfs://master:9000/hbase</value>
          </property>
          <property>
              <name>hbase.zookeeper.quorum</name>
              <value>slave1,slave2,...,slave100</value>
          </property>
          <property>
              <name>hbase.tmp.dir</name>
              <value>/home/hadoop/hbase-1.0.0/hbasedata</value>
          </property>
          <property>
              <name>hbase.cluster.distributed</name>
              <value>true</value>
          </property>
     	  <property>
              <name>hbase.master.info.port</name>
              <value>60010</value>
          </property>
     </configuration>
     ```  

- 类似地，拷贝`hbase-1.0.0`文件夹至所有slave机器：
      
        ```shell
        $ scp -r /home/hadoop/hbase-1.0.0 slave1:/home/hadoop
        $ scp -r /home/hadoop/hbase-1.0.0 slave2:/home/hadoop
        ...
        ```  

### Zookeeper

**在每台slave机器上**

- 在[Apache Zookeeper Releases Repo](https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz)下载Zookeeper-3.4.6并解压至服务器：

    ```shell
    $ cd ~
    $ wget ttps://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
    $ tar zxvf zookeeper-3.4.6.tar.gz
    ```

- 将`/home/hadoop/zookeeper-3.4.6/conf/zoo_sample.cfg`更名为`/home/hadoop/zookeeper-3.4.6/conf/zoo.cfg`

- 修改`zoo.cfg`，修改`dataDir`路径，并加入所有`slave`节点信息：

    ```text
    dataDir=/home/hadoop/zookeeper-3.4.6/data
    server.1=slave1:2888:3888
    server.2=slave2:2888:3888
    ...
    server.100=slave100:2888:3888
    ```

- 在`/home/hadoop/zookeeper-3.4.6/data`下创建`myid`文件，
内容应为节点编号数字；例如`slave1`的`myid`文件内容应为`1`，以此类推；


[hadoop]: <https://hadoop.apache.org/>
[hbase]: <https://hbase.apache.org/>
[sonoscape]: <http://www.sonoscape.com.cn/>
[zookeeper]: <https://zookeeper.apache.org/>
[JDK]:<https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>

### 环境变量配置

- **在`master`节点**的`/etc/profile`文件中追加以下内容：
    ```shell
    # JAVA PATH
    JAVA_HOME=/usr/local/jdk1.8.0_192
    JRE_HOME=/usr/local/jdk1.8.0_192/jre
    CLASS_PATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
    PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin  
    
    # HADOOP PATH
    HADOOP_HOME=/home/hadoop/hadoop-2.6.0
    PATH=$PATH:$HADOOP_HOME/bin
    
    # HBASE PATH
    HBASE_HOME=/home/hadoop/hbase-1.0.0
    PATH=$PATH:$HBASE_HOME/bin
    
    export JAVA_HOME JRE_HOME CLASS_PATH HADOOP_HOME HBASE_HOME PATH   
    export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"
    ```

- **在`slave`节点**的`/etc/profile`文件中追加以下内容：
    ```shell
    # JAVA PATH
    JAVA_HOME=/usr/local/jdk1.8.0_192
    JRE_HOME=/usr/local/jdk1.8.0_192/jre
    CLASS_PATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
    export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
    
    # HADOOP PATH
    HADOOP_HOME=/home/hadoop/hadoop-2.6.0
    PATH=$PATH:$HADOOP_HOME/bin
    
    # HBASE PATH
    HBASE_HOME=/home/hadoop/hbase-1.0.0
    PATH=$PATH:$HBASE_HOME/bin
    
    # Zookeeper PATH
    ZOOKEEPER_HOME=/home/hadoop/zookeeper-3.4.6
    PATH=$PATH:$ZOOKEEPER_HOME/bin:$ZOOKEEPER_HOME/conf
    
    export JAVA_HOME JRE_HOME CLASS_PATH HADOOP_HOME HBASE_HOME ZOOKEEPER_HOME PATH
    export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native
    ```
    
- 执行以下命令使环境变量修改生效:
    ```shell
    $ source /etc/profile
    ```

- 运行`java -version`命令，应该能够得到类似的信息：
    ```text
    java version "1.8.0_192"
    Java(TM) SE Runtime Environment (build 1.8.0_192-b12)
    Java HotSpot(TM) 64-Bit Server VM (build 25.192-b12, mixed mode)
    ```