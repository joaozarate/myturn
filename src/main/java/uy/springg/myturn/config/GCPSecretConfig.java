package uy.springg.myturn.config;

import com.google.cloud.secretmanager.v1.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

@CommonsLog
@Configuration
public class GCPSecretConfig {

    public static final String GCP_PROJECT = "snappy-catcher-389415";

    @Bean
    public Map<String, String> secrets() {
        return getSecretManager().map(this::fetchSecrets)
                .orElseThrow(() -> {
                    log.error("Secret Manager not found!");
                    return new RuntimeException("Secret Manager not found!");
                });
    }

    private Map<String, String> fetchSecrets(SecretManagerServiceClient client) {
        log.info("Fetching all secrets form Google Secret Manager...");

        ProjectName projectName = ProjectName.of(GCP_PROJECT);
        Map<String, String> secrets = new HashMap<>();

        client.listSecrets(projectName).iterateAll().forEach(s -> {
            String name = SecretName.parse(s.getName()).getSecret();

            log.debug(String.format("Secret: %s", name));

            var sv = SecretVersionName.newBuilder().setSecret(name).setSecretVersion("latest").setProject(GCP_PROJECT).build();
            AccessSecretVersionResponse response = client.accessSecretVersion(sv);

            if (computeChecksum(response)) return;

            String payload = response.getPayload().getData().toStringUtf8();

            secrets.put(SecretName.parse(s.getName()).getSecret(), payload);
        });

        return secrets;
    }

    private Optional<SecretManagerServiceClient> getSecretManager() {

        try {

            log.info("Creating GCP secret manager client.");
            return Optional.of(SecretManagerServiceClient.create());

        } catch (IOException ex) {
            log.error("Error creating GCP secret manager client: " + ex.getMessage());
            log.error(ex);
        }

        return Optional.empty();
    }

    private boolean computeChecksum(AccessSecretVersionResponse response) {
        Checksum checksum = new CRC32C();
        byte[] data = response.getPayload().getData().toByteArray();

        checksum.update(data, 0, data.length);

        if (response.getPayload().getDataCrc32C() != checksum.getValue()) {
            log.error(String.format("The secret %s obtained from the Secret Manager was corrupted in-transit.", response.getName()));
            return true;
        }
        return false;
    }

}
