# User-defined Workflow

# 1. 实现日志
## 1.1 Debug
* MultipartHttpServletRequest is required  
 主要原因是@SpringBootApplication所自带的自动配置MultipartSolver不能支持PUT方法，在Application.java中实现MultipartResolver Bean即可。  
 搜索问题时需要将问题拆分查找最可能出问题的地方，此例中需要注意Http Method的支持问题，用多种method进行测试。