package com.growith.global.aop;

import com.growith.global.annotation.CreateAlarm;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


@Aspect
@Component
public class AlarmEventAop implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @AfterReturning(value = "@annotation(createAlarm)", returning = "retVal")
    public void afterReturning(CreateAlarm createAlarm, Object retVal) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = createAlarm.classInfo();
        if (clazz.isInstance(retVal)) {
            Constructor<?> constructor = clazz.getDeclaredConstructor(retVal.getClass());
            Object event = constructor.newInstance(retVal);
            eventPublisher.publishEvent(event);
        }
    }

}
