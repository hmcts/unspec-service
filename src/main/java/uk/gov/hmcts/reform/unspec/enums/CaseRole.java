package uk.gov.hmcts.reform.unspec.enums;

public enum CaseRole {
    APPLICANTSOLICITORONE,
    APPLICANTSOLICITORTWO,
    RESPONDENTSOLICITORONE,
    RESPONDENTSOLICITORTWO;

    private String formattedName;

    CaseRole() {
        this.formattedName = String.format("[%s]", name());
    }

    public String formattedName() {
        return formattedName;
    }
}
