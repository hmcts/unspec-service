package uk.gov.hmcts.reform.unspec.helpers;

import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class CaseDocumentHelper {

    private CaseDocumentHelper() {
        //Utility class
    }

    public static Optional<CaseDocument> findDocument(
        List<Element<CaseDocument>> documents,
        DocumentType documentType
    ) {
        return Stream.ofNullable(documents)
            .flatMap(Collection::stream)
            .map(Element::getValue)
            .filter(Objects::nonNull)
            .filter(caseDocument -> caseDocument.getDocumentType().equals(documentType))
            .findFirst();
    }
}
