package in.wynk.secret.manager.solr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class SolrSyncCurl {

    enum ContentPartner {
        RUNNTV, DISTROTV, EROSNOW, SONYLIV, SONYLIV_VOD, SONYLIV_LIVE,
        MWTV, AIRTEL_TV, AIRTEL, ALTBALAJI,
        HOTSTARLIVETV, AAJTAKLIVETV, AMAZON, HOTSTAR, IMDB, NDTV, GRACENOTE,
        TVPROMO, LIVETV, PERFORM, HOICHOI, KALPANIK, NETFLIX, ZEE5, ZEE5STICK,
        MUSIC, UNKNOWN, EDITORJI, HUNGAMA, SHEMAROOME, CURIOSITYSTREAM,
        LIONSGATEPLAY, VOOT, SHORTSTV, KIDSFLIX, SUNNXT, EDITORJIVOD, SUNNXT_FULL,
        FASTFILMZ, TV9VOD, ZEEVOD, SUNVOD, ULTRA, DEVILS_CIRCUIT, AIRTELXSTREAMVOD,
        CREATOR, SILLYMONKS, SRIGANESHVIDEO, KEYENTERTAINMENTS, SRIBALAJIVIDEO,
        VOLGAVIDEOS, MILLENNIUMVIDEOS, WHACKEDOUTMEDIA, NODWIN, XSTREAMPREMIUM,
        XSTREAMPREMIUM_LITE_TELCO, XSTREAMPREMIUM_TELCO, DIVO, MUBI, VOOT_KIDS,
        EPICON, THEQYOU, VOOT_SELECT, AMAZON_PRIME, MINITV, ASIANET, NAMMAFLIX,
        DOLLYWOOD, HOTSTAR_DTH, MANORAMAMAX, NEWJ, JUNGO, KLIKK, DISCOVERFILMS,
        INDIASCIENCE, DOCUBAY, SOCIALSWAG, CHAUPAL, KANCCHALANKA, RAJTV, XSTREAMADS,
        FANCODE, PLAYFLIX, STAGE, AHA, VROTT, ZEEFIVE, HOTSTAR_XPP, APPLETV, ZEEDIGITAL,
        CHANAJOR
    }

    public static void main(String[] args) {

        for (ContentPartner cpEnum : ContentPartner.values()) {
            String cp = cpEnum.name(); // use capitalized enum name
//            String url = "https://batch-preprod.wynk.in/test/cache/ingestion/solrsync/solr_playable?cp=" + cp;
            String url = "https://batch.airtel.tv/test/cache/ingestion/solrsync/solr_playable?cp=" + cp;

            try {
                System.out.println("Calling: " + url);
                ProcessBuilder pb = new ProcessBuilder(
                        "curl",
                        "--location",
                        url,
                        "--header",
                        "Content-Type: application/json"
                );
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("Response finished for " + cp + " with exit code: " + exitCode);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("All CPs processed.");
    }
}
