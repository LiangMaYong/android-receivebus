package com.liangmayong.receivebus.dispatch;

import com.liangmayong.receivebus.annotations.OnReceiveEvent;
import com.liangmayong.receivebus.annotations.UnReceiveEvent;
import com.liangmayong.receivebus.listener.ReceiveBusListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ReceiveBusDispatchListener
 *
 * @author LiangMaYong
 * @version 1.0
 */
public final class ReceiveBusDispatchListener implements ReceiveBusListener {

    public static final String[] AIR_PROXY_PREFIX = {"onReceiveEvent", "onEvent"};

    private static final Map<Class<?>, Map<String, Method>> methods = new HashMap<>();

    private final Object object;

    public ReceiveBusDispatchListener(Object object) {
        this.object = object;
        if (methods.containsKey(object.getClass())) {
            return;
        } else {
            Map<String, Method> methodMap = this.parserOnEvent(object);
            methods.put(object.getClass(), methodMap);
        }
    }

    private Map<String, Method> parserOnEvent(Object object) {
        Map<String, Method> methodMap = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                UnReceiveEvent unAir = method.getAnnotation(UnReceiveEvent.class);
                if (unAir == null) {
                    OnReceiveEvent onAir = method.getAnnotation(OnReceiveEvent.class);
                    boolean proxy = onAir != null;
                    if (!proxy) {
                        for (int j = 0; j < AIR_PROXY_PREFIX.length; j++) {
                            if (method.getName().startsWith(AIR_PROXY_PREFIX[j])) {
                                proxy = true;
                                break;
                            }
                        }
                    }
                    if (proxy) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1) {
                            methodMap.put(parameterTypes[0].getName(), method);
                        }
                    }
                }
            }
        }
        return methodMap;
    }

    private Method getProxyMethod(Class<?> clazz) {
        if (methods.containsKey(object.getClass())) {
            return methods.get(object.getClass()).get(clazz.getName());
        }
        return null;
    }

    @Override
    public void onReceiveEvent(Object event) {
        if (event != null) {
            Class<?> clazz = event.getClass();
            Method method = getProxyMethod(clazz);
            if (method != null) {
                method.setAccessible(true);
                try {
                    method.invoke(object, event);
                } catch (Exception e) {
                }
            }
        }
    }
}
