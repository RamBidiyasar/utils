package in.wynk.secret.manager.aerospike.controller;

import in.wynk.secret.manager.aerospike.dto.AerospikeRequest;
import in.wynk.secret.manager.aerospike.service.AerospikeClientService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.aerospike.client.Record;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aerospike")
public class AerospikeController {

    private final AerospikeClientService service;

    public AerospikeController(AerospikeClientService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody AerospikeRequest request) {
        service.save(request);
        return ResponseEntity.ok("Saved successfully");
    }

    @PostMapping("/fetch")
    public ResponseEntity<Object> fetch(@RequestBody AerospikeRequest request) {
        Record r = service.fetch(request);
        return r != null ? ResponseEntity.ok(r.bins) : ResponseEntity.status(404).body("Not found");
    }

    @PostMapping("/fetchAll")
    public Map<String, Object> fetchAll(@RequestBody AerospikeRequest request) {
        return service.fetchAll(request);
    }

    @PostMapping("/searchByPrefix")
    public Map<String, Object> searchByPrefix(@RequestBody AerospikeRequest request) {
      return service.fetchByPrefix(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody AerospikeRequest request) {
        boolean deleted = service.delete(request);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Not found");
        }
    }

    @PostMapping("/count")
    public int count(@RequestBody AerospikeRequest request) {
        return service.count(request);
    }
}
