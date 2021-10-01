package edu.hue.community.config;

import edu.hue.community.Quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author 47552
 * @date 2021/10/01
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean postScoreRefreshJob() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreTriggerFactoryBean(JobDetail postScoreRefreshJob) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJob);
        factoryBean.setName("postScoreTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(5 * 60 * 1000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
