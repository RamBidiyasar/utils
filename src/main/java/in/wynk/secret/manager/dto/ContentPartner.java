package in.wynk.secret.manager.dto;



import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ContentPartner {
    DISTROTV("distrotv", false, false, true),
    EROSNOW("erosnow", false, true, true),
    SONYLIV("sonyliv",false,false,true),
    SONYLIV_VOD("sonyliv_vod",false,false,true),
    SONYLIV_LIVE("sonyliv_live",false,false,true),
    HOOQ("hooq"),
    YOUTUBE("youtube"),
    DAILYMOTION("dailymotion"),
    MWTV("mwtv"),
    HUAWEI("mwtv"),
    AIRTEL_TV("airtel_tv"),
    AIRTEL("airtel"),
    ALTBALAJI("altbalaji", false, true, true),
    AMAZON("amazon"),
    HOTSTAR("hotstar"),
    IMDB("imdb"),
    NDTV("ndtv"),
    GRACENOTE("GRACENOTE"),
    TVPROMO("tvpromo"),
    LIVETV("livetv",false, false, true),
    PERFORM("perform"),
    HOICHOI("hoichoi", false, false,  true),
    KALPANIK("kalpanik"),
    NETFLIX("netflix"),
    ZEE5("zee5"),
    ZEE5STICK("zee5stick"),
    MUSIC("music"),
    UNKNOWN("unknown"),
    EDITORJI("editorji"),
    HUNGAMA("hungama"),
    SHEMAROOME("shemaroome",false, false, true),
    CURIOSITYSTREAM("curiositystream"),
    LIONSGATEPLAY("lionsgateplay", false, false, true),
    VOOT("voot"),
    SHORTSTV("shortstv", false, false, true),
    KIDSFLIX("kidsflix"),
    SUNNXT("sunnxt", false, false, true),
    EDITORJIVOD("editorjivod", true),
    SUNNXT_FULL("sunnxt_full"),
    FASTFILMZ("fastfilmz", true),
    TV9VOD("tv9vod", true),
    ZEEVOD("zeevod", true),
    SUNVOD("sunvod", true),
    ULTRA("ultra", false, true, true),
    DEVILS_CIRCUIT("devils_circuit", true),
    AIRTELXSTREAMVOD("airtelxstreamvod", true),
    CREATOR("creator", true),
    SILLYMONKS("sillymonks", true),
    SRIGANESHVIDEO("sriganeshvideo", true),
    KEYENTERTAINMENTS("keyentertainments", true),
    SRIBALAJIVIDEO("sribalajivideo", true),
    VOLGAVIDEOS("volgavideos", true),
    MILLENNIUMVIDEOS("millenniumvideos", true),
    WHACKEDOUTMEDIA("whackedoutmedia", true),
    NODWIN("nodwin", true),
    XSTREAMPREMIUM("xstreampremium"),
    XSTREAMPREMIUM_LITE_TELCO("xstreampremium_lite_telco"),
    XSTREAMPREMIUM_TELCO("xstreampremium_telco"),
    DIVO("divo", false, true, true),
    MUBI("mubi",false, true, true),
    VOOT_KIDS("voot_kids"),
    EPICON("epicon", false, false, true),
    THEQYOU("theqyou", true),
    VOOT_SELECT("voot_select"),
    AMAZON_PRIME("amazon_prime"),
    MINITV("minitv"),
    ASIANET("asianet", false, false, true),
    NAMMAFLIX("nammaflix",false,true, true),
    DOLLYWOOD("dollywood", false, true, true),
    HOTSTAR_DTH("hotstar_dth"),
    MANORAMAMAX("manoramamax", false, false, true),
    NEWJ("newj", false, false, true),
    JUNGO("jungo", false, false,  true),
    KLIKK("klikk", false, false, true),
    DISCOVERFILMS("discoverfilms", false, false, true),
    INDIASCIENCE("indiascience", false, false, true),
    DOCUBAY("docubay", false, false, true),
    SOCIALSWAG("socialswag", false, false, true),
    CHAUPAL("chaupal" , false, false , true),
    KANCCHALANKA("kancchalanka" , false, false , true),
    RAJTV("rajtv", false, true, true),
    XSTREAMADS("xstreamads", false, true, true),
    FANCODE("fancode", false, false, true),
    PLAYFLIX("playflix", false, false, true),
    STAGE("stage", false, false, true),
    AHA("aha", false, false, true),
    HOTSTAR_XPP("hotstar_xpp");

    private static final String REGISTERED = "registered";
    private static final String SUBSCRIBED = "subscribed";
    private static final String EMAIL = "email";
    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "tokenSecret";
    private static final String TOKEN_EXPIRY = "tokenExpiry";
    private static final String SUBSCRIPTION_EXPIRY = "subscriptionExpiry";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String SEPARATOR = ".";
    private boolean isSelfHosted = false;
    private boolean isMspContentPartner = false;
    private boolean isPartnerChannel = false;
    private String name;

    private static List<ContentPartner> partnerChannels;

    ContentPartner(String cp, boolean isSelfHosted) {
        this.name = cp;
        this.isSelfHosted = isSelfHosted;
    }

    ContentPartner(String cp, boolean isSelfHosted, boolean isMspContentPartner){
        this.name = cp;
        this.isSelfHosted = isSelfHosted;
        this.isMspContentPartner = isMspContentPartner;
    }

    ContentPartner(String cp, boolean isSelfHosted, boolean isMspContentPartner, boolean isPartnerChannel){
        this.name = cp;
        this.isSelfHosted = isSelfHosted;
        this.isMspContentPartner = isMspContentPartner;
        this.isPartnerChannel = isPartnerChannel;
    }

    ContentPartner(String name) {
        this.name = name;
    }

    public static ContentPartner getContentPartner(String name) {
        for (ContentPartner cp : values()) {
            if (cp.name.equalsIgnoreCase(name)) {
                return cp;
            }
        }
        return ContentPartner.UNKNOWN;
    }

    public static ContentPartner getContentPartnerFromName(String name) {
        for (ContentPartner cp : values()) {
            if (cp.name().equalsIgnoreCase(name)) {
                return cp;
            }
        }
        return ContentPartner.UNKNOWN;
    }

    public static List<String> selfHostedContentPartners() {
        return Arrays.stream(ContentPartner.values()).filter(partner -> partner.isSelfHosted).map(Enum::name).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getRegKey() {
        return name + SEPARATOR + REGISTERED;
    }

    public String getSubscriptionKey() {
        return name + SEPARATOR + SUBSCRIBED;
    }

    public String getEmailKey() {
        return name + SEPARATOR + EMAIL;
    }

    public String getTokenKey() {
        return name + SEPARATOR + TOKEN;
    }

    public String getTokenSecretKey() {
        return name + SEPARATOR + TOKEN_SECRET;
    }

    public String getTokenExpiryKey() {
        return name + SEPARATOR + TOKEN_EXPIRY;
    }

    public String getSubscriptionExpiryKey() {
        return name + SEPARATOR + SUBSCRIPTION_EXPIRY;
    }

    public String getSubscriptionIdKey(){
        return name + SEPARATOR + SUBSCRIPTION_ID;
    }

    public boolean isSelfHosted() {
        return isSelfHosted;
    }

    public boolean isMspContentPartner(){
        return isMspContentPartner;
    }

    public boolean isPartnerChannel() {
        return isPartnerChannel;
    }

    public static List<ContentPartner> getPartnerChannels() {
        if (CollectionUtils.isEmpty(partnerChannels)) {
            partnerChannels = Arrays.stream(ContentPartner.values())
                    .filter(ContentPartner::isPartnerChannel)
                    .collect(Collectors.toList());
        }
        return partnerChannels;
    }
}