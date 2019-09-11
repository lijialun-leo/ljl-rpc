# RPC框架
自己设计一个的轻量级的RPC框架 这个版本是Spring boot版本
需要下载zookeeper

ljl-rpc-starter 是RPC框架的源码
ljl-rpc-server 是一个测试项目

## 1.配置文件
```
zookeeper:
  hosts: 127.0.0.1:2181
  baseackage : com.ljl.server.service.Impl

netty:
  port: 8885
```
## 2.扫描注解
@RPCClientPackage(basePackage = "com.ljl.server.service")
再启动类上加入你想要扫描的接口包

## 3.服务端
在serviceImpl中写上@RPCServer 认为该类是一个服务
```
@Service
@RPCServer
public class serverWorld2 {
	public String message(String world){
		return "Hello world";
	} 
}
```
## 4.客户端<br>
写一个service写上@RPCClient 认为其实一个消费接口 主要不要再改类上面添加@Service等注解 因为有自己写的方法注入bean
```
@RPCClient
public interface clientWorld {
	@RPCURL(className="serverWorld2",methodName="message") 
	public List message(String world);
}
```
最后就像调用实现类一样在controller中调用<br>
```
@Controller
@RequestMapping("/clientWorld")
public class clientWorldController {
	
	@Autowired
	private clientWorld clientWorld;
	
	@RequestMapping("sendMessage")
	@ResponseBody
	public List sendMessage(String message){
		return clientWorld.message(message);
	}

}

