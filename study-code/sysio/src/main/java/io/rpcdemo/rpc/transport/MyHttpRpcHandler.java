package io.rpcdemo.rpc.transport;

import io.rpcdemo.rpc.Dispatcher;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.util.SerDerUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/1
 */
public class MyHttpRpcHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream in = req.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(in);
        try {
            MyContent myContent = (MyContent) oin.readObject();
            //获取对象
            String serviceName = myContent.getName();
            String methodName = myContent.getMethod();
            Object[] args = myContent.getArgs();
            Class<?>[] parameterTypes = myContent.getParameterTypes();

            Object impl = Dispatcher.getInstance().get(serviceName);
            Class<?> clazz = impl.getClass();

            Object res = null;
            try {
                Method method = clazz.getMethod(methodName, parameterTypes);
                res = method.invoke(impl, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }


            MyContent respContent = new MyContent();
            respContent.setRes(res);
            byte[] respBytes = SerDerUtil.ser(respContent);

            ServletOutputStream out = resp.getOutputStream();
            out.write(respBytes);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
