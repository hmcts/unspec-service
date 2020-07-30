package uk.gov.hmcts.reform.unspec.repositories;

import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface ReferenceNumberRepository {

    @SqlQuery("SELECT next_legal_rep_reference_number()")
    String getReferenceNumber();

}
