package in.wynk.secret.manager.controller;

import in.wynk.secret.manager.service.SecretManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecretController {

    @Autowired
    private SecretManagerService secretManagerService;

    @GetMapping("/get-secret")
    public String getSecret(@RequestParam(defaultValue = "mongo_user") String secretId, @RequestParam(defaultValue = "latest") String versionId) {
        return secretManagerService.getSecret(secretId, versionId);
    }
}
