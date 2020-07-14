package uk.gov.hmcts.reform.ucmc.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.List;

@Slf4j
public class CaseStayedFinder implements Job {

    @Autowired
    private CaseSearchService caseSearchService;

    @Autowired
    private CoreCaseDataService coreCaseDataService;

    @Override
    public void execute(JobExecutionContext context) {
        final String jobName = context.getJobDetail().getKey().getName();
        log.info("Job {} started", jobName);

        List<CaseDetails> cases = caseSearchService.getCasesOver112Days();

        if (cases.isEmpty()) {
            log.info("Job '{}' did not find any cases", jobName);
        } else {
            log.info("Job '{}' found {} case(s)", jobName, cases.size());

            // logic to update cases
            cases.forEach(x -> coreCaseDataService.triggerEvent(
                "CIVIL",
                "UNSPECIFIED_CLAIMS",
                x.getId(),
                "MOVE_TO_STAYED",
                x.getData()));
        }

        log.info("Job '{}' finished", jobName);
    }
}
