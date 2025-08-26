package in.wynk.secret.manager.aerospike.controller;

import in.wynk.secret.manager.aerospike.dto.request.AerospikeRequest;
import in.wynk.secret.manager.aerospike.dto.response.PaginatedResponse;
import in.wynk.secret.manager.aerospike.dto.response.StatsResponse;
import in.wynk.secret.manager.aerospike.service.AerospikeClientService;
import java.awt.print.Pageable;
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
    public PaginatedResponse fetch(@RequestBody AerospikeRequest request) {
        return service.fetch(request);
    }

    @PostMapping("/fetchAll")
    public PaginatedResponse fetchAll(@RequestBody AerospikeRequest request) {
        return service.fetchAll(request);
    }


    @PostMapping("/getStats")
    public StatsResponse getStats(@RequestBody AerospikeRequest request) {
        return service.getStats(request);
    }

    @PostMapping("/searchByPrefix")
    public PaginatedResponse searchByPrefix(@RequestBody AerospikeRequest request) {
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
