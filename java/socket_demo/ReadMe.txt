
准备工作：
1. 在Windows上设置共享文件夹，确保大族server能正常访问
2. 将要打印的图片复制到共享文件夹。

开始测试：

1. 打开Windows CMD
2. 切换到当前目录，例如：cd D:\LaserPrintDrv
3. 验证JAVA环境：java -version   看是否能正确显示JAVA版本信息

4. 用写字板或其他文本编辑器，打开LaserDrvSvc.java文件。
   依据实际环境和参数修改如下代码：
   //配置大族打印机的IP和端口
   laserDrvSvc.init("112.74.76.96", 808);
   //配置要打印的模板名和图片文件名
   Map<String, String> retMap = laserDrvSvc.runLaserPrintTask("mytemp", "myimage.jpg");

5. 编译： javac LaserDrvSvc.java
6. 运行： java LaserDrvSvc
7. 检查输出信息，将信息复制给我

   