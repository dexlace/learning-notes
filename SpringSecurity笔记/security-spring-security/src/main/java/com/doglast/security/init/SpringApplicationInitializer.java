package com.doglast.security.init;


import com.doglast.security.config.ApplicationConfig;
import com.doglast.security.config.WebConfig;
import com.doglast.security.config.WebSecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author Administrator
 * @version 1.0
 **/
public class SpringApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    //spring容器，相当于加载 applicationContext.xml
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ApplicationConfig.class, WebSecurityConfig.class};
    }

    //servletContext，相当于加载springmvc.xml
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    //url-mapping
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
