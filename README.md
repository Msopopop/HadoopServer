# 基于Hadoop的DICOM信息解析、存储系统

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![Build Status](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer.svg?branch=master)](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer)[![codebeat badge](https://codebeat.co/badges/74dffbf8-42a7-4029-b69f-bc1697e70b5f)](https://codebeat.co/projects/github-com-sonoscape-hadoopproject-xjtu-hadoopserver-master)

## 特性

该系统是基于Hadoop开发的Dicom解析系统，能够实现以下功能

  - 接受客户端发送的DICOM文件（通过监听特定文件路径），同时针对DICOM文件生成JPEG图片
  - 将DICOM文件以及JPEG图片存放到HDFS中
  - 解析DICOM文件并将信息存入HBase
  - 解析后端传送的GSPS文件并将标注信息存入对应DICOM文件的HBase中
  
> 医疗数位影像传输协定（DICOM）是一组通用的标准协定，在对于医学影像的处理、储存、打印、传输上。它包含了档案格式的定义及网络通信协定。DICOM是以TCP/IP为基础的应用协定，并以TCP/IP联系各个系统。两个能接受DICOM格式的医疗仪器间，可借由DICOM格式的档案，来接收与交换影像及病人资料。 
  
  该系统旨在能与所有兼容DICOM协议的设备兼容互通。

## 配置环境

请参照[环境搭建指南](/HOW_TO_SETUP_HADOOP.md)。

## 编译及生成

本项目基于[Maven](https://maven.apache.org/)，您可以直接使用Maven来生成可执行的jar包

```shell
$ git clone
```

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

该程序使用了以下开源组件（见[pom.xml](/pom.xml)），在此表示感谢：

* [hadoop] - The Apache Hadoop software library is a framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models.
* [hbase] - Apache HBase™ is the Hadoop database, a distributed, scalable, big data store. 
* [zookeeper] - Apache ZooKeeper is an effort to develop and maintain an open-source server which enables highly reliable distributed coordination.
* [weasis] - Weasis is a DICOM viewer available as a desktop application or as a web-based application.
* [dcm4che] - A collection of open source applications and utilities for the healthcare enterprise.
* [log4j] - Apache Log4j 2 is an upgrade to Log4j that provides significant improvements over its predecessor, Log4j 1.x, and provides many of the improvements available in Logback while fixing some inherent problems in Logback’s architecture.
* [Gaoyp12138-dicom] - 提供了[DicomParseUtils.java](/src/main/java/Utils/DicomParseUtil.java)的大部分内容
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
