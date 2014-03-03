package com.sohu.smc.core.server.builer;

import com.sohu.smc.core.annotation.RequestMapping;
import com.sohu.smc.core.annotation.RequestMatcher;
import com.sohu.smc.core.annotation.RequestMethod;
import com.sohu.smc.core.server.Action;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 * User: zhangsuozhu
 * Date: 13-1-14
 * Time: 下午5:32
 */
public class ActionBuilder extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(ActionBuilder.class);

    public void builer(ProxyFactory factory, List<String> packages) {
        Set<Class<?>> classSet = PackageScanner.getClasses(RequestMapping.class, packages);  //得到目标包下所有的action类
        try {
            for (Class cls : classSet) {
                RequestMapping t = (RequestMapping) cls.getAnnotation(RequestMapping.class);
                String folderurl = formatClassUrl(t.value()[0]);  //formtter
                Object obj = factory.getObject(cls);    //生成被代理的对象实例
                //开始处理每个方法
                for (Method m : cls.getDeclaredMethods()) { //获得当前类的所有方法，但不包括继承过来的方法
                    if (m.isAnnotationPresent(RequestMapping.class)) {
                        t = m.getAnnotation(RequestMapping.class);

                        String url = folderurl + formatMethodUrl(t.value(), m);   //生成最终的url
                        Class[] ccc = m.getParameterTypes();
                        RequestMethod[] methods = t.method();           //该action 响应的http method
                        if (m.getReturnType() != String.class || ccc.length > 2) {       //如果返回类型不是string 或者参数多于2个的话，认为是错误的，无法生成
                            throw new IllegalArgumentException(cls.getName() + "." + m.getName() + " return type or param error.");
                        }
                        if (m.getModifiers() != Modifier.PUBLIC) {
                            throw new IllegalArgumentException(cls.getName() + "." + m.getName() + " is not public.");
                        }
                        for (Class c : ccc) {
                            if (c != HttpRequest.class && c != HttpResponse.class) {    // 如果参数的类型不是request resp的话，也认为是错误的
                                throw new IllegalArgumentException(cls.getName() + "." + m.getName() + " param type error.");
                            }
                        }
                        //得到 action可以处理的 httpmethod 使用位操作
                        int methodValue = 0;
                        for (RequestMethod method : methods) {
                            methodValue |= method.getValue();
                        }
                        //目标类的全路径，只要不重复即可，此处是用被代理类的包 +proxy +method+className 的方式
                        String proxyName = cls.getPackage().getName() + ".proxy." + m.getName() + cls.getSimpleName();
                        byte[] code = BytesBuilder.dump(proxyName, m);      // get byte code
                        Class<?> exampleClass = this.defineClass(proxyName, code, 0,
                                code.length);                  //load into jvm


                        Constructor c1 = exampleClass.getDeclaredConstructor(new Class[]{String.class, cls});
                        Action o = (Action) c1.newInstance(new Object[]{url, obj});    //new

                        o.setMethod((byte) methodValue);  //http method只有8种，所以可以用一个byte来保存

                        if (RequestMatcher.SINGLE == t.matcher()) {
                            if (SingleActionMapping.getInstance().getAction(url) == null) {

                                SingleActionMapping.getInstance().addAction(url, o);
                            } else {    //如果发现有url相同的action，报错
                                throw new IllegalArgumentException(cls.getName() + "." + m.getName() + " error.found same url:" + url);
                            }

                        } else {
                            // todo
                        }

                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 格式化url，成为    /folder  样式的
     *
     * @param url
     * @return
     */
    private String formatClassUrl(String url) {
        url = StringUtils.trimToEmpty(url);
        if (StringUtils.isBlank(url)) {
            return url;
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }


    private String formatMethodUrl(String[] urlArray, Method method) {
        if (urlArray == null || urlArray.length < 1) {
            return "/" + method.getName().toLowerCase();
        }
        String url = StringUtils.trimToEmpty(urlArray[0]);
        if (StringUtils.isBlank(url)) {
            return "/" + method.getName().toLowerCase();
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        return url;
    }
}
