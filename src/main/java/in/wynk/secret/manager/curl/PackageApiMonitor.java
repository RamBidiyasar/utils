package in.wynk.secret.manager.curl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.*;

public class PackageApiMonitor {

    private static final int TPS = 5;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String URL = "https://package.airtel.tv/app/v5.1/package?appId=MOBILITY&au=true&audienceType=ADULT&bn=454&chromecast=1&city=New%20Delhi&cl=dl&count=50&country=IN&d2cAr=1&d2cDle=28&d2cEd=1775814773475&d2cP=79007&deviceId=D63A335C-1A9F-497A-B342-7EC1A0F5BFB8&dt=phone&dth=1&hotstarExpiry=2026-04-10&id=axsta_q35a73791608795938226&isDth=1&layoutExperimentId=y_RUVAdLRzfpXfvoi0&lg=en%2Chi%2Cpa&ln=en%2Chi%2Cpa&mwTvPack=45003%2C25002&oSeg=hotstar_dth_paid%2Cxstreampremium_paid%2Cjungo_free%2Cxstreampremium_free%2Czeefive_paid%2Ccreator_free&obDate=1768388468277&offset=0&op=AIRTEL&os=IOS&paidCp=addatimes%2Caha%2Cchaupal%2Cdocubay%2Cdollywood%2Cepicon%2Cerosnow%2Choichoi%2Chotstar_dth%2Chungama%2Cklikk%2Clionsgateplay%2Cmanoramamax%2Cnammaflix%2Cplayflix%2Crajtv%2Cshemaroome%2Cshortstv%2Csonyliv_vod%2Csunnxt%2CtimesPlay%2Cultra%2Czeefive&prUser=false&ps=netflix%3Apending_activation%2Cxstreampremium%3Aclaimed%2Cjiohotstar%3Aclaimed%2Czeefive%3Aclaimed&refresh=true";

    public static void main(String[] args) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(TPS);

        Runnable task = () -> {

            Request request = new Request.Builder()
                    .url(URL)
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("x-os-id", "IOS")
                    .addHeader("x-org-id", "atv")
                    .addHeader("accept", "*/*")
                    .addHeader("x-atv-utkn", "y_RUVAdLRzfpXfvoi0:6K3l043AWe7usnwJzDKEn6js1wE=")
                    .addHeader("x-app-id", "mobility")
                    .addHeader("x-version-id", "454")
                    .addHeader("x-atv-adid", "C04A378C-E490-4C64-BC12-5469771B0167")
                    .addHeader("cache-control", "no-cache")
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    System.out.println("Request failed: " + response.code());
                    return;
                }

                String body = response.body().string();

                JsonNode root = mapper.readTree(body);

                JsonNode contentNode = root.get("content");

                if (contentNode == null || !contentNode.isArray() || contentNode.isEmpty()) {
                    System.out.println("⚠️ Empty content detected at " + System.currentTimeMillis());
                }

            } catch (Exception e) {
                System.out.println("Error calling API: " + e.getMessage());
            }
        };

        // schedule 5 TPS
        scheduler.scheduleAtFixedRate(
                task,
                0,
                1000 / TPS,
                TimeUnit.MILLISECONDS
        );
    }
}