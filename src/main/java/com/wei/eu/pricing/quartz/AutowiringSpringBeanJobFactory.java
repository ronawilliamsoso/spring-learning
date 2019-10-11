package com.wei.eu.pricing.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Support for Spring Autowiring for Quartz Jobs.
 */
public final class AutowiringSpringBeanJobFactory
                extends SpringBeanJobFactory
                implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext( final ApplicationContext context ) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance( @NonNull final TriggerFiredBundle bundle )
                    throws Exception {
        final Object job = super.createJobInstance( bundle );
        beanFactory.autowireBean( job );
        return job;
    }
}
