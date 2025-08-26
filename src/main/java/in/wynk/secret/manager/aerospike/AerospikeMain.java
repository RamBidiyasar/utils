package in.wynk.secret.manager.aerospike;

import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import in.wynk.secret.manager.aerospike.service.AerospikeService;
import java.util.HashMap;
import java.util.Map;

public class AerospikeMain {
    public static final String NAMESPACE = "ucm_dev";
    public static final String PROD_NAMESPACE = "user-core";
    public static final String STATIC_PACKAGE = "staticPackage";
    public static final String DEFAULT_PAGE = "defaultPage";
    public static final String ORIGAMI_RESPONSE = "origami-user-page";


    public static void main(String[] args) {
        AerospikeEnvironment env = AerospikeEnvironment.PREPROD;

        AerospikeService service = new AerospikeService(env);


        service.delete(NAMESPACE, DEFAULT_PAGE, "defaultPage:WEB_WEBOS-homepage2");

//        service.save(namespace, set, key, data);
//        service.fetch(namespace, set, key);
//        service.fetchAll(namespace, set);
//        service.countInSet(NAMESPACE, DEFAULT_PAGE);
//            service.findAllKeys(NAMESPACE, ORIGAMI_RESPONSE);
//        service.printSetOverview(NAMESPACE, STATIC_PACKAGE);
//        service.fetchByKeyPrefix(namespace, set, "user");
    }
}
