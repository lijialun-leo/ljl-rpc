package com.ljl.rpc.handle;


import com.ljl.rpc.annotation.RPCClientPackage;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by Administrator on 2019/9/10 0010.
 */
public class ClientPackageScan  implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RPCClientPackage.class.getName()));
        String[] basePackages = annoAttrs.getStringArray("basePackage");
        //自定义的 包扫描器
        ClientPackageScanHandle scanHandle = new ClientPackageScanHandle(beanDefinitionRegistry,false);
        //扫描指定路径下的接口
        scanHandle.doScan(basePackages);
    }
}


