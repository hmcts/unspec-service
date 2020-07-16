package uk.gov.hmcts.reform.ucmc.controllers;

import uk.gov.hmcts.reform.ucmc.model.documents.DocumentType;

public interface DocumentService {
    byte[] generateDocument(String externalId, DocumentType claimDocumentType, String authorisation);
}
