package com.assignment.affiliate.infrastructure.scheduler

import org.quartz.SimpleTrigger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.JobDetailFactoryBean

@Configuration
class QuartzSchedulerConfiguration {
    @Bean("handlePendingCommissionsJobDetail")
    fun handlePendingCommissionsJobDetail(): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(HandlePendingCommissionsJob::class.java)
        jobDetailFactory.setDescription("Approve/Reject pending commissions")
        jobDetailFactory.setDurability(true)
        return jobDetailFactory
    }

    @Bean
    fun handlePendingCommissionJobTrigger(
        @Qualifier("handlePendingCommissionsJobDetail") jobDetailFactoryBean: JobDetailFactoryBean
    ): CronTriggerFactoryBean {
        val triggerFactory = CronTriggerFactoryBean()
        triggerFactory.setJobDetail(jobDetailFactoryBean.`object`!!)
        triggerFactory.setCronExpression("0 0 0 * * ?")
        triggerFactory.setDescription("Trigger to Approve/Reject pending commissions")
        triggerFactory.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)
        return triggerFactory
    }

    @Bean("disburseApprovedCommissionsJobDetail")
    fun disburseApprovedCommissionsJobDetail(): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(DisburseApprovedCommissionsJob::class.java)
        jobDetailFactory.setDescription("Disburse approved commissions")
        jobDetailFactory.setDurability(true)
        return jobDetailFactory
    }

    @Bean
    fun disburseApprovedCommissionsJobTrigger(
        @Qualifier("disburseApprovedCommissionsJobDetail") jobDetailFactoryBean: JobDetailFactoryBean
    ): CronTriggerFactoryBean {
        val triggerFactory = CronTriggerFactoryBean()
        triggerFactory.setJobDetail(jobDetailFactoryBean.`object`!!)
        triggerFactory.setCronExpression("0 0 1 * * ?")
        triggerFactory.setDescription("Trigger to disburse approved commissions")
        triggerFactory.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)
        return triggerFactory
    }
}
