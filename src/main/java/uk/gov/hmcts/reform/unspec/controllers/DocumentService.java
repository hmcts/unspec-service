package uk.gov.hmcts.reform.unspec.controllers;

import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;

public interface DocumentService {
    byte[] generateDocument(String externalId, DocumentType claimDocumentType, String authorisation);
}
