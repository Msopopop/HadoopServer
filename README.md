# 基于Hadoop与HBase集群的Dicom解析系统

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0) [![Build Status](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer.svg?branch=master)](https://travis-ci.com/sonoscape-HadoopProject-xjtu/HadoopServer)

该系统是基于Hadoop开发的Dicom解析系统，能够实现以下功能
  - 接受客户端发送的DICOM文件（通过FTP服务器），同时针对DICOM文件生成单张或多张JPEG图片
  - 将DICOM文件以及JPEG图片存放到HDFS中
  - 解析DICOM文件将患者信息存入HBase

> Apache Hadoop是一款支持数据密集型分布式应用程序并以Apache 2.0许可协议发布的开源软件框架。
> 它支持在商品硬件构建的大型集群上运行的应用程序。Hadoop是根据谷歌公司发表的MapReduce和Google文件系统的论文自行实现而成。所有的Hadoop模块都有一个基本假设，即硬件故障是常见情况，应该由框架自动处理。 
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually* written in Markdown! To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.

### 依赖

该程序依赖Maven打包，并使用了以下开源组件（见[pom.xml]）

* [Weasis] - Weasis is a DICOM viewer available as a desktop application or as a web-based application.
* [Dcm4che] - A collection of open source applications and utilities for the healthcare enterprise

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

Dillinger requires [Node.js](https://nodejs.org/) v4+ to run.

Install the dependencies and devDependencies and start the server.

```sh
$ cd dillinger
$ npm install -d
$ node app
```

For production environments...

```sh
$ npm install --production
$ NODE_ENV=production node app
```

### Plugins

Dillinger is currently extended with the following plugins. Instructions on how to use them in your own application are linked below.

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
#### Building for source
For production release:
```sh
$ gulp build --prod
```
Generating pre-built zip archives for distribution:
```sh
$ gulp build dist --prod
```
### Docker
Dillinger is very easy to install and deploy in a Docker container.

By default, the Docker will expose port 8080, so change this within the Dockerfile if necessary. When ready, simply use the Dockerfile to build the image.

```sh
cd dillinger
docker build -t joemccann/dillinger:${package.json.version} .
```
This will create the dillinger image and pull in the necessary dependencies. Be sure to swap out `${package.json.version}` with the actual version of Dillinger.

Once done, run the Docker image and map the port to whatever you wish on your host. In this example, we simply map port 8000 of the host to port 8080 of the Docker (or whatever port was exposed in the Dockerfile):

```sh
docker run -d -p 8000:8080 --restart="always" <youruser>/dillinger:${package.json.version}
```

Verify the deployment by navigating to your server address in your preferred browser.

```sh
127.0.0.1:8000
```

### Todos

 - Write MORE Tests
 - Add Night Mode

License
----
EPL-2.0

   [Weasis]: <https://github.com/nroduit/Weasis>
   [Dcm4che]: <https://www.dcm4che.org/>
   [pom.xml]: <https://github.com/sonoscape-HadoopProject-xjtu/HadoopServer/blob/master/pom.xml>
