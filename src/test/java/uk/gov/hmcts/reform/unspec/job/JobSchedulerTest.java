package uk.gov.hmcts.reform.unspec.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.config.scheduler.JobScheduler;
import uk.gov.hmcts.reform.unspec.config.scheduler.SchedulerConfiguration;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@ExtendWith(SpringExtension.class)
public class JobSchedulerTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private SchedulerConfiguration schedulerConfig;

    @InjectMocks
    private JobScheduler jobScheduler;

    @Captor
    private ArgumentCaptor<JobDetail> jobDetailsCaptor;

    @Captor
    private ArgumentCaptor<Trigger> triggerCaptor;

    private final SchedulerConfiguration.Schedule enabledSchedule1 = SchedulerConfiguration.Schedule.builder()
        .enabled(true)
        .description("Job 1 run at night")
        .cron("0 0 1 ? * * *")
        .name("Job 1")
        .jobClass(Job.class)
        .cronGroup("Night")
        .build();

    private final SchedulerConfiguration.Schedule enabledSchedule2 = SchedulerConfiguration.Schedule.builder()
        .enabled(true)
        .description("Job 2 run at night")
        .cron("0 0 2 ? * * *")
        .name("Job 2")
        .jobClass(Job.class)
        .cronGroup("Night")
        .build();

    private final SchedulerConfiguration.Schedule disabledSchedule = SchedulerConfiguration.Schedule.builder()
        .enabled(false)
        .description("Job 3 run at night")
        .cron("0 0 3 ? * * *")
        .name("Job 3")
        .jobClass(Job.class)
        .cronGroup("Night")
        .build();

    private final SchedulerConfiguration.Schedule invalidSchedule = SchedulerConfiguration.Schedule.builder()
        .enabled(true)
        .description("Job run at night")
        .cron("0")
        .name("Job invalid")
        .jobClass(Job.class)
        .cronGroup("Invalid")
        .build();

    @Test
    void shouldCreateJobs_WhenEnabledSchedules() throws Exception {
        when(schedulerConfig.getSchedulerConf()).thenReturn(of(enabledSchedule1, enabledSchedule2, disabledSchedule));
        when(scheduler.getJobKeys(any())).thenReturn(emptySet());

        jobScheduler.scheduleCronJobs();

        verify(scheduler, never()).deleteJob(any());
        verify(scheduler, times(2)).scheduleJob(jobDetailsCaptor.capture(), triggerCaptor.capture());

        List<JobDetail> jobs = jobDetailsCaptor.getAllValues();
        List<Trigger> triggers = triggerCaptor.getAllValues();

        assertJobIsEqual(jobs.get(0), enabledSchedule1);
        assertTriggerIsEqual(triggers.get(0), enabledSchedule1);

        assertJobIsEqual(jobs.get(1), enabledSchedule2);
        assertTriggerIsEqual(triggers.get(1), enabledSchedule2);
    }

    @Test
    void shouldNotCreateAnyJob_WhenAllSchedulesAreDisabled() throws Exception {
        when(schedulerConfig.getSchedulerConf()).thenReturn(List.of(disabledSchedule));
        when(scheduler.getJobKeys(any())).thenReturn(emptySet());

        jobScheduler.scheduleCronJobs();

        verify(scheduler, never()).deleteJob(any());
        verify(scheduler, never()).scheduleJob(any(), any());
    }

    @Test
    void shouldThrowException_WhenCronExpressionIsInvalid() throws Exception {
        when(schedulerConfig.getSchedulerConf()).thenReturn(List.of(invalidSchedule));
        when(scheduler.getJobKeys(any())).thenReturn(emptySet());

        assertThrows(Exception.class, jobScheduler::scheduleCronJobs);
    }

    @Test
    void shouldRecreateJobs() throws Exception {
        when(schedulerConfig.getSchedulerConf()).thenReturn(List.of(enabledSchedule2));
        when(scheduler.getJobKeys(any())).thenReturn(Set.of(jobKeyOf(enabledSchedule1), jobKeyOf(disabledSchedule)));

        jobScheduler.scheduleCronJobs();

        verify(scheduler).deleteJob(jobKeyOf(enabledSchedule1));
        verify(scheduler).deleteJob(jobKeyOf(disabledSchedule));
        verify(scheduler).scheduleJob(jobDetailsCaptor.capture(), triggerCaptor.capture());

        assertJobIsEqual(jobDetailsCaptor.getValue(), enabledSchedule2);
        assertTriggerIsEqual(triggerCaptor.getValue(), enabledSchedule2);
    }

    private static JobKey jobKeyOf(SchedulerConfiguration.Schedule schedule) {
        return JobKey.jobKey(schedule.getName(), schedule.getCronGroup());
    }

    private static void assertJobIsEqual(JobDetail jobDetail, SchedulerConfiguration.Schedule schedule) {
        assertThat(jobDetail.getDescription()).isEqualTo(schedule.getDescription());
        assertThat(jobDetail.getJobClass()).isEqualTo(schedule.getJobClass());
        assertThat(jobDetail.getKey()).isEqualTo(JobKey.jobKey(schedule.getName(), schedule.getCronGroup()));
    }

    private static void assertTriggerIsEqual(Trigger trigger, SchedulerConfiguration.Schedule schedule) {
        assertThat(trigger).isEqualTo(newTrigger()
            .withIdentity(schedule.getName(), schedule.getCronGroup())
            .withDescription(schedule.getDescription())
            .withSchedule(cronSchedule(schedule.getCron()))
            .build());
    }
}
