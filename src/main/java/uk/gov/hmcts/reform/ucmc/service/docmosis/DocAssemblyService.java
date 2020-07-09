package uk.gov.hmcts.reform.ucmc.service.docmosis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.docassembly.DocAssemblyClient;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyRequest;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyResponse;
import uk.gov.hmcts.reform.docassembly.domain.OutputType;
import uk.gov.hmcts.reform.docassembly.exception.DocumentGenerationFailedException;
import uk.gov.hmcts.reform.ucmc.model.DocmosisDocument;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;

@Service
@ConditionalOnProperty(prefix = "doc_assembly", name = "url")
public class DocAssemblyService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthTokenGenerator authTokenGenerator;
    private final DocAssemblyClient docAssemblyClient;

    @Autowired
    public DocAssemblyService(AuthTokenGenerator authTokenGenerator, DocAssemblyClient docAssemblyClient) {
        this.authTokenGenerator = authTokenGenerator;
        this.docAssemblyClient = docAssemblyClient;
    }

    public DocmosisDocument generateDocument(String authorisation,
                                             DocAssemblyTemplateBody formPayload,
                                             DocmosisTemplates docmosisTemplates) {

        var docAssemblyResponse = renderTemplate(authorisation, docmosisTemplates, formPayload);

        return DocmosisDocument.builder()
            .documentUrl(docAssemblyResponse.getRenditionOutputLocation())
            .documentTitle(docmosisTemplates.getDocumentTitle())
            .build();
    }

    public DocAssemblyResponse renderTemplate(String authorisation,
                                              DocmosisTemplates docmosisTemplates,
                                              DocAssemblyTemplateBody payload) {
        logger.info("Creating document request for template: {}, for title : {}",
                    docmosisTemplates.getTemplate(), docmosisTemplates.getDocumentTitle()
        );

        DocAssemblyRequest docAssemblyRequest = DocAssemblyRequest.builder()
            .templateId(docmosisTemplates.getTemplate())
            .outputType(OutputType.PDF)
            .formPayload(payload)
            .build();

        logger.info("Sending document request for template: {} with title : {}",
                    docmosisTemplates.getTemplate(), docmosisTemplates.getDocumentTitle()
        );
        try {
            return docAssemblyClient.generateOrder(
                authorisation,
                authTokenGenerator.generate(),
                docAssemblyRequest
            );
        } catch (Exception e) {
            logger.error(
                "Error while trying to generate a document for external id: {}",
                docmosisTemplates.getDocumentTitle()
            );

            throw new DocumentGenerationFailedException(e);
        }
    }
}
