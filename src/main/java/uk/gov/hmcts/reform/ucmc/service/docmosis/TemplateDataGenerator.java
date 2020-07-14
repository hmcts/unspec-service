package uk.gov.hmcts.reform.ucmc.service.docmosis;

import uk.gov.hmcts.reform.ucmc.model.CaseData;

import java.io.IOException;

public abstract class TemplateDataGenerator<T> {

    public abstract T getTemplateData(CaseData caseData) throws IOException;
}
