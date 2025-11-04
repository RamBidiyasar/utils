package in.wynk.secret.manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/error")
public class ErrorController {

    @GetMapping("/500")
    public ResponseEntity<String> internalServerError() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500 Internal Server Error - Something went wrong on the server");
    }

    @GetMapping("/501")
    public ResponseEntity<String> notImplemented() {
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body("501 Not Implemented - This feature is not yet implemented");
    }

    @GetMapping("/502")
    public ResponseEntity<String> badGateway() {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body("502 Bad Gateway - Invalid response from upstream server");
    }

    @GetMapping("/503")
    public ResponseEntity<String> serviceUnavailable() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("503 Service Unavailable - Service is temporarily unavailable");
    }

    @GetMapping("/504")
    public ResponseEntity<String> gatewayTimeout() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("504 Gateway Timeout - Upstream server timed out");
    }
}
