package net.glaso.ca.business.scheduler.bean;

import net.glaso.ca.business.scheduler.service.CaScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan( basePackageClasses = { CaScheduler.class })
public class CaSchedulerConfig {

	@Bean(name="scheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {

		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize( 2 );
		threadPoolTaskScheduler.setThreadNamePrefix( "ThreadPoolTaskScheduler" );

		return threadPoolTaskScheduler;
	}
}
