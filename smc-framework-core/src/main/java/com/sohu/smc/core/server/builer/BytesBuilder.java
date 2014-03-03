package com.sohu.smc.core.server.builer;

import com.sohu.smc.core.server.Action;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * 生成代理类的bytecode
 * User: zhangsuozhu
 * Date: 13-1-14
 * Time: 下午5:54
 */
public final class BytesBuilder implements Opcodes {
    private static final Logger log = LoggerFactory.getLogger(BytesBuilder.class);
    private static final char DOT = '.';
    private static final char SPLITER = '/';

    /**
     * @param destClass 目标类 即要生成的类
     * @param method    被代理的方法
     * @return
     * @throws Exception
     */
    public static final byte[] dump(String destClass, Method method)  {
        String basePath = Action.class.getName().replace(DOT, SPLITER);  //目标类的父类
        String srcPath = method.getDeclaringClass().getName().replace(DOT, SPLITER);    //被代理的类对象
        destClass = destClass.replace(DOT, SPLITER);          //目标类


        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, destClass, null, basePath, null);   //类声明
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "action", "L" + srcPath + ";", null, null);     //声明被代理的对象引用  private final T action;
            fv.visitEnd();
        }
        //以下是构造方法
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;L" + srcPath + ";)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, basePath, "<init>", "(Ljava/lang/String;)V");






            Label l1 = new Label();
            mv.visitLabel(l1);       //调用Object的构造方法

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, destClass, "action", "L" + srcPath + ";");
            Label l2 = new Label();
            mv.visitLabel(l2);     //为action赋值

            mv.visitInsn(RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "L" + destClass + ";", null, l0, l3, 0);
            mv.visitLocalVariable("url", "Ljava/lang/String;", null, l0, l3, 1);
            mv.visitLocalVariable("action", "L" + srcPath + ";", null, l0, l3, 2);
            mv.visitMaxs(2, 3);
            mv.visitEnd();      //声明局部变量
        }
        //构造方法end
        //开始生成execute方法
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "execute", "(Lorg/jboss/netty/handler/codec/http/HttpRequest;Lorg/jboss/netty/handler/codec/http/HttpResponse;)Ljava/lang/String;", null, new String[]{"java/lang/Exception"});
            //方法参数类型
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, destClass, "action", "L" + srcPath + ";");          //取得action 的引用
             //以下是类似spring mvc的一个参数智能匹配,被代理的方法参数可以不是固定的类型，比如业务没有涉及到resp的逻辑，方法参数就可以没有resp参数
            String paramStr = "";
            int i = 1;
            for (Class<?> ca : method.getParameterTypes()) {
                i++;
                if (ca == HttpRequest.class) {
                    mv.visitVarInsn(ALOAD, 1);
                } else {
                    mv.visitVarInsn(ALOAD, 2);
                }

                paramStr += "L" + ca.getName().replace(DOT, SPLITER) + ";";
            }
            //参数处理完毕

            mv.visitMethodInsn(INVOKEVIRTUAL, srcPath, method.getName(), "(" + paramStr + ")Ljava/lang/String;");     //调用action的execute方法
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + destClass + ";", null, l0, l1, 0);
            mv.visitLocalVariable("req", "Lorg/jboss/netty/handler/codec/http/HttpRequest;", null, l0, l1, 1);
            mv.visitLocalVariable("resp", "Lorg/jboss/netty/handler/codec/http/HttpResponse;", null, l0, l1, 2);
            mv.visitMaxs(i, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream("r:/"+destClass.substring(destClass.lastIndexOf("/"))+".class");
//            fos.write(cw.toByteArray());
//            fos.close();
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);  //log exception
//            throw new RuntimeException(e);
//        }

        return cw.toByteArray();
    }

//    public static void main(String[] args) throws Exception {
//        byte[] code=dump("com.sohu.TestAction",FirstAction.class,"action");
//                 BytesBuilder app=new BytesBuilder();
//        Class<?> exampleClass =  app.defineClass("com.sohu.TestAction", code, 0,
//                code.length);
//    }
}
