package in.wynk.secret.manager.service;

import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient.ListSecretsPagedResponse;

import java.io.IOException;

public class ListSecrets {


    public static void main(String[] args) {
        try {
            listSecrets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void listSecrets() throws IOException {
        String projectId = "prj-wynk-prd-xstrm-svc-01";
//        String projectId = "883538315078";
        listSecrets(projectId);
    }

    // List all secrets for a project
    public static void listSecrets(String projectId) throws IOException {
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            // Build the parent name.
            ProjectName projectName = ProjectName.of(projectId);

            // Get all secrets.
            ListSecretsPagedResponse pagedResponse = client.listSecrets(projectName);

            // List all secrets.
            pagedResponse
                    .iterateAll()
                    .forEach(
                            secret -> {
                                System.out.printf("Secret %s\n", secret.getName());
                            });
        }
    }
}
