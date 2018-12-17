# 基于Hadoop的DICOM信息解析系统

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![Build Status](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer.svg?branch=master)](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer) [![codebeat badge](https://codebeat.co/badges/74dffbf8-42a7-4029-b69f-bc1697e70b5f)](https://codebeat.co/projects/github-com-sonoscape-hadoopproject-xjtu-hadoopserver-master) [![codecov](https://codecov.io/gh/sonoscape-HadoopProject-xjtu/HadoopServer/branch/master/graph/badge.svg)](https://codecov.io/gh/sonoscape-HadoopProject-xjtu/HadoopServer)

欢迎来到本项目！请前往[wiki](/wiki/主页)以了解更多资讯。

## 致谢

该程序使用了以下开源组件（见[pom.xml](/pom.xml)），在此表示感谢：

* [hadoop] - The Apache Hadoop software library is a framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models.
* [hbase] - Apache HBase™ is the Hadoop database, a distributed, scalable, big data store. 
* [zookeeper] - Apache ZooKeeper is an effort to develop and maintain an open-source server which enables highly reliable distributed coordination.
* [weasis] - Weasis is a DICOM viewer available as a desktop application or as a web-based application.
* [dcm4che] - A collection of open source applications and utilities for the healthcare enterprise.
* [log4j] - Apache Log4j 2 is an upgrade to Log4j that provides significant improvements over its predecessor, Log4j 1.x, and provides many of the improvements available in Logback while fixing some inherent problems in Logback’s architecture.
* [Gaoyp12138-dicom] - 提供了[DicomParseUtils.java](/src/main/java/Utils/DicomParseUtil.java)的部分代码
* [Dillinger] - A awesome markdown editor.

本项目还得到了西安交通大学 [生命科学与技术学院][slst]、[深圳开立生物技术有限公司][sonoscape]相关人员的指导，在此一并表示感谢。

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