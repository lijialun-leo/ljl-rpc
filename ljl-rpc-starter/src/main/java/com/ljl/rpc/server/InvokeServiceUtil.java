package com.ljl.rpc.server;

import com.google.gson.Gson;

import com.ljl.rpc.client.RPCRequest;
import com.ljl.rpc.util.ApplicationContextProvider;
import com.ljl.rpc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvokeServiceUtil {

    public static Map<String,Integer> map = new ConcurrentHashMap<String, Integer>();

    @Autowired
    private static ApplicationContextProvider applicationContex;

    //private static ObjectMapper objectMapper;
    /**
     * 反射调用相应实现类并结果
     * @param request
     * @return
     */
    public static String invoke(RPCRequest request){
        String result=null;
        Object obj = null;
        try {
            Class implClass=Class.forName(request.getClassName());
            Object[] parameters=request.getParameters();
            int parameterNums=request.getParameters().length;
            Class[] parameterTypes=new Class[parameterNums];
            for (int i = 0; i <parameterNums ; i++) {
                parameterTypes[i]=parameters[i].getClass();
            }
            Method method=implClass.getDeclaredMethod(request.getMethodName(),parameterTypes);
            //获取到注册的服务bean
            Object implObj= applicationContex.getBean(StringUtil.toLowerCaseFirstOne(implClass.getSimpleName()));
            obj=method.invoke(implObj,parameters);
            //将返回值转成json格式
            Gson gson = new Gson();
            result = gson.toJson(obj);
            map.put(request.getClassName(), (map.get(request.getClassName()).intValue()+1));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        /*if(map.get(request.getClassName()).intValue() <1000){

        }else{
            result = "服务已暂停";
        }*/
        return result;
    }

}
