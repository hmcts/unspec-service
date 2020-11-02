package uk.gov.hmcts.reform.unspec.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDocumentBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.helpers.CaseDocumentHelper.findDocument;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.SEALED_CLAIM;

class CaseDocumentHelperTest {

    @ParameterizedTest
    @EnumSource(value = DocumentType.class)
    void shouldReturnExpectedCaseDocument_whenValidDocumentTypeIsPresent(DocumentType documentType) {
        List<Element<CaseDocument>> documents = List.of(Element.<CaseDocument>builder()
                                                            .value(CaseDocumentBuilder.builder()
                                                                       .of(documentType, LocalDateTime.now())
                                                                       .build())
                                                            .build());
        Optional<CaseDocument> caseDocument = findDocument(documents, documentType);

        assertThat(caseDocument).isPresent();
        assertThat(caseDocument).map(CaseDocument::getDocumentType).hasValue(documentType);
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = DocumentType.class, mode = EnumSource.Mode.EXCLUDE, names = {"SEALED_CLAIM"})
    void shouldReturnEmptyResult_whenProvidedDocumentTypeIsNotPresent(DocumentType documentType) {
        List<Element<CaseDocument>> documents = List.of(Element.<CaseDocument>builder()
                                                            .value(CaseDocumentBuilder.builder()
                                                                       .of(SEALED_CLAIM, LocalDateTime.now())
                                                                       .build())
                                                            .build());
        Optional<CaseDocument> caseDocument = findDocument(documents, documentType);

        assertThat(caseDocument).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = DocumentType.class)
    void shouldReturnEmptyResult_whenCaseDocumentCollectionIsNull(DocumentType documentType) {
        Optional<CaseDocument> caseDocument = findDocument(null, documentType);

        assertThat(caseDocument).isEmpty();
    }

    @Test
    void shouldReturnEmptyResult_whenElementValueOfCaseDocumentIsNull() {
        List<Element<CaseDocument>> documents = List.of(Element.<CaseDocument>builder()
                                                            .value(null)
                                                            .build());
        Optional<CaseDocument> caseDocument = findDocument(documents, SEALED_CLAIM);

        assertThat(caseDocument).isEmpty();
    }
}
