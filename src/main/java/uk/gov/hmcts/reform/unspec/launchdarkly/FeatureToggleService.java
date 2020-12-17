package uk.gov.hmcts.reform.unspec.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class FeatureToggleService {

    public static final LDUser UNSPEC_SERVICE_USER = new LDUser.Builder("civil-unspec-service")
        .anonymous(true)
        .build();

    private final LDClientInterface internalClient;

    @Autowired
    public FeatureToggleService(LDClientInterface internalClient) {
        this.internalClient = internalClient;
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public boolean isFeatureEnabled(String feature) {
        return internalClient.boolVariation(feature, UNSPEC_SERVICE_USER, false);
    }

    public boolean isFeatureEnabled(String feature, LDUser user) {
        return internalClient.boolVariation(feature, user, false);
    }

    private void close() {
        try {
            internalClient.close();
        } catch (IOException e) {
            // can't do anything clever here because things are being destroyed
            log.error("Error in closing the Launchdarkly client::", e);
        }
    }
}
