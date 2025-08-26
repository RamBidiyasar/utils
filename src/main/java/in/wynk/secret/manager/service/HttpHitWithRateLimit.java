package in.wynk.secret.manager.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpHitWithRateLimit {

    private static final String REQUEST_URL = "http://discovery-api-preprod.wynk.in/origami-service/v1/layout/homepage2?contentFree=false&addToBillEnabled=false&adsAllowed=false&freeStreamingEnded=false&xppPlanId=6188&buildNo=279&airtelUser=true&layoutVersion=3&ottClaimOnThanks=false&uid=00ZI-j1-M59WEjU890&loginUser=true&osVersion=17.6.1&xppExpiry=2024-10-11&clientKey=MOBILITY_IOS&xmpClaimPending=false&appKey=MOBILITY&lang=en%2Chi%2Chv%2Cpa&isPrepaidUser=false&xstreamClaimOnThanks=false&deviceType=PHONE&os=IOS&firstForwardedFor=125.22.37.20&contentAutoPlay=false&xppClaimPending=false&xppRenewEligible=false&fmfSIEnabled=false&xppRenewBannerEligible=false&realm=XSTREAM&userType=broadband&offerSegment=newj_free%2Cdistrotv_free%2Cxstreampremium_paid%2Casianet_free%2Cindiascience_free%2Cdivo_free%2Cdiscoverfilms_free%2Cminitv_free%2Ccreator_free%2Csocialswag_free";
    private static final int TPS = 200;
    private static final long REQUEST_INTERVAL_MS = 1000 / TPS;

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        Runnable requestTask = () -> {
            try {
                URL url = new URL(REQUEST_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(500);
                conn.setReadTimeout(500);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json, application/*+json");
                conn.setRequestProperty("x-atv-traceid", "7F839628-1E41-4883-8762-3A5588D4DC9E-1727948716702");
                conn.setRequestProperty("x-bsy-ab", "1727948717067|36795:1|63817:1|94798:0|31112:1|104526:0|35626:0|60640:1|41993:1|63696:0");
                conn.setRequestProperty("Content-Length", "0");

                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Schedule tasks to run at the specified rate (50 TPS)
        for (int i = 0; i < TPS; i++) {
            executor.scheduleAtFixedRate(requestTask, i * REQUEST_INTERVAL_MS, REQUEST_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }

        // Keep the executor running indefinitely
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdownNow()));
    }
}
