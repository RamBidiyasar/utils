package in.wynk.secret.manager.curl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurlExecutor {
    public static void main(String[] args) {
        // Example curl command
        String curlCommand = """
            curl --location 'https://package-preprod.wynk.in/app/v3/layout?appId=WEB&au=true&bn=74&chromecast=true&city=New%20Delhi&cl=dl&count=30&country=IN&countryCode=IN&deviceId=dc4410e4-5fab-4df5-8624-fc985e916818&dt=BROWSER&dth=false&ip=136.226.251.23&isDth=false&layoutExperimentId=gUlq9_NWl24XoyJGT0&lg=hi&ln=hi&mwTvPack=596542%2C7004%2C25002%2C45003&oSeg=xstreampremium_paid%2Cxstreampremium_free%2Ccreator_free&offset=0&op=AIRTEL&os=WEBOS&pageId=homepage2&paidCp=aha%2Caltbalaji%2Cchaupal%2Cdocubay%2Cdollywood%2Cepicon%2Cerosnow%2Choichoi%2Chungama%2Cklikk%2Clionsgateplay%2Cmanoramamax%2Cnammaflix%2Cplayflix%2Crajtv%2Cshemaroome%2Cshortstv%2Csonyliv_vod%2Cstage%2Csunnxt%2CtimesPlay%2Cultra&prUser=false&ps=xstreampremium%3Aclaimed&refresh=false&sSeg=rajtv_claimed%2Caha_claimed%2Choichoi_claimed%2Cstage_claimed%2Csunnxt_claimed%2Csonyliv_vod_claimed%2Clionsgateplay_claimed%2Cshemaroome_claimed%2Chungama_claimed%2Caltbalaji_claimed%2Cchaupal_claimed%2Cerosnow_claimed%2Cultra_claimed%2Cepicon_claimed%2Cdollywood_claimed%2Cshortstv_claimed%2Cmanoramamax_claimed%2Cdocubay_claimed%2Cplayflix_claimed%2Cnammaflix_claimed%2CtimesPlay_claimed%2Cklikk_claimed&sd=aha%3A596544%3A3%2Caltbalaji%3A8730%3A3%2Cchanajor%3A796545%3A1%2Cchaupal%3A76030%3A3%2Ccreator%3A12001%3A1%2Cdistrotv%3A596542%3A1%2Cdocubay%3A6005%3A3%2Cdollywood%3A37002%3A3%2Cepicon%3A35002%3A3%2Cerosnow%3A1004%3A3%2Cfancode%3A8631%3A1%2Choichoi%3A4004%3A3%2Chungama%3A6003%3A3%2Cklikk%3A43003%3A3%2Clionsgateplay%3A10004%3A3%2Cmanoramamax%3A42003%3A3%2Cminitv%3A596545%3A1%2Cmwtv%3A25002%3A2%2Cnammaflix%3A38002%3A3%2Cplayflix%3A9630%3A3%2Crajtv%3A45003%3A3%2Crunntv%3A376545%3A1%2Cshemaroome%3A7004%3A3%2Cshortstv%3A28003%3A3%2Csonyliv_vod%3A2004%3A3%2Cstage%3A396545%3A3%2Csunnxt%3A10006%3A3%2CtimesPlay%3A991194%3A3%2Cultra%3A10008%3A3%2Cvrott%3A595540%3A1%2Czeedigital%3A991192%3A1&ssp=43003%2C9630%2C42003%2C6003%2C7004%2C38002%2C396545%2C991194%2C8730%2C6005%2C37002%2C45003%2C596544%2C76030%2C10008%2C1004%2C2004%2C35002%2C28003%2C4004%2C10004%2C10006&state=National%20Capital%20Territory%20of%20Delhi&stateCode=DL&svodPlans=ALL%2BN&type=SET_USER_LOCATION&ut=broadband&xEy=2025-12-15&xct=false&xppPId=77081&xpprbe=false' \\
            --header 'accept: application/json, text/plain, */*' \\
            --header 'accept-language: en-GB,en-US;q=0.9,en;q=0.8' \\
            --header 'if-none-match: 1753501476:7e550ef029079aeac2338fb45137d5ea--gzip' \\
            --header 'origin: https://preprod.airtelxstream.in' \\
            --header 'priority: u=1, i' \\
            --header 'referer: https://preprod.airtelxstream.in/' \\
            --header 'request-init-time: 1753501493375' \\
            --header 'sec-ch-ua: "Not)A;Brand";v="8", "Chromium";v="138", "Google Chrome";v="138"' \\
            --header 'sec-ch-ua-mobile: ?0' \\
            --header 'sec-ch-ua-platform: "macOS"' \\
            --header 'sec-fetch-dest: empty' \\
            --header 'sec-fetch-mode: cors' \\
            --header 'sec-fetch-site: cross-site' \\
            --header 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36' \\
            --header 'x-atv-ab: 57924:1|36795:1|63817:1|94798:1|31112:1|104526:1|35626:0|60640:0|41993:0|63696:1' \\
            --header 'x-atv-did: dc4410e4-5fab-4df5-8624-fc985e916818|BROWSER|WEBOS|10.15.7|74|74.2.5|mac|mac' \\
            --header 'x-atv-traceid: 9b152576-1c83-4466-9ab8-514550d7d5df' \\
            --header 'x-atv-utkn: gUlq9_NWl24XoyJGT0:wrmmZjqHK9g1jbk6IjC8yT4UKFg='
            """;


        int tps = 50; // Fixed Transactions Per Second

        executeAtFixedTPS(curlCommand, tps);
    }

    public static void executeAtFixedTPS(String curlCommand, int tps) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(200);
        long interval = 1000 / tps; // Interval in milliseconds

        Runnable task = () -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
                Process process = processBuilder.start();

//                // Read the response
//                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                }
//
////                process.waitFor(); // Wait for process to complete
            } catch (Exception e) {
                System.err.println("Error executing curl: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, interval, TimeUnit.MILLISECONDS);
    }
}
