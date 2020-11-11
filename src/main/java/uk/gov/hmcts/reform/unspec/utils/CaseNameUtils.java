package uk.gov.hmcts.reform.unspec.utils;

import org.apache.commons.lang.StringUtils;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.Party;

import java.util.function.Function;

public class CaseNameUtils {

    //TODO Need to confirm the case name logic
    public static final Function<CaseData, String> toCaseName = caseData ->
        fetchApplicantName(caseData) + " v " + fetchRespondentName(caseData);

    private CaseNameUtils() {
        //NO-OP
    }

    public static String fetchRespondentName(CaseData caseData) {
        StringBuilder defendantNameBuilder = new StringBuilder();
        if (caseData.getRespondent2() != null) {
            defendantNameBuilder.append("1 ");
            defendantNameBuilder.append(caseData.getRespondent1().getPartyName());
            soleTraderCompany(caseData.getRespondent1(), defendantNameBuilder);
            defendantNameBuilder.append(" & 2 ");
            defendantNameBuilder.append(caseData.getRespondent2().getPartyName());
            soleTraderCompany(caseData.getRespondent2(), defendantNameBuilder);
        } else {
            defendantNameBuilder.append(caseData.getRespondent1().getPartyName());
            soleTraderCompany(caseData.getRespondent1(), defendantNameBuilder);
        }

        return defendantNameBuilder.toString();
    }

    public static String fetchApplicantName(CaseData caseData) {
        StringBuilder applicantNameBuilder = new StringBuilder();

        if (caseData.getApplicant2() != null) {
            applicantNameBuilder.append("1 ");
            applicantNameBuilder.append(caseData.getApplicant1().getPartyName());
            soleTraderCompany(caseData.getApplicant1(), applicantNameBuilder);
            applicantNameBuilder.append(" & 2 ");
            applicantNameBuilder.append(caseData.getApplicant2().getPartyName());
            soleTraderCompany(caseData.getApplicant2(), applicantNameBuilder);
        } else {
            applicantNameBuilder.append(caseData.getApplicant1().getPartyName());
            soleTraderCompany(caseData.getApplicant1(), applicantNameBuilder);
        }

        return applicantNameBuilder.toString();
    }

    private static void soleTraderCompany(Party party, StringBuilder stringBuilder) {
        if (party.getType() == Party.Type.SOLE_TRADER && StringUtils.isNotBlank(party.getSoleTraderTradingAs())) {
            stringBuilder.append(" T/A ").append(party.getSoleTraderTradingAs());
        }
    }
}
