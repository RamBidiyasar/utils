package in.wynk.secret.manager.utils;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UniqueBaseUrlsExtractor {

    public static void main(String[] args) {
        String paragraph = """
                {
                  "_id": "EROSNOW",
                  "_class": "in.wynk.config.entity.CpConfig",
                  "cp": "EROSNOW",
                  "searchBuildLimit": {
                    "IPTV|ANDROID|STB": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|ANDROID|PHONE": {
                      "min": {
                        "$numberLong": "12678"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|ANDROID|TABLET": {
                      "min": {
                        "$numberLong": "12678"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|IOS|PHONE": {
                      "min": {
                        "$numberLong": "118"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|IOS|TABLET": {
                      "min": {
                        "$numberLong": "118"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "WEB|WEBOS|BROWSER": {
                      "min": {
                        "$numberLong": "36"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "WEB|MWEBOS|BROWSER": {
                      "min": {
                        "$numberLong": "36"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|ANDROID|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "FIRESTICK|ANDROID|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "CHROMECAST|IOS|PHONE": {
                      "min": {
                        "$numberLong": "-1"
                      },
                      "max": {
                        "$numberLong": "10000000"
                      }
                    },
                    "CHROMECAST|IOS|TABLET": {
                      "min": {
                        "$numberLong": "-1"
                      },
                      "max": {
                        "$numberLong": "1000000"
                      }
                    },
                    "CHROMECAST|ANDROID|PHONE": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "1000000"
                      }
                    },
                    "CHROMECAST|ANDROID|TABLET": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000"
                      }
                    },
                    "SDK|LINUX|STB": {
                      "min": {
                        "$numberLong": "40000000"
                      },
                      "max": {
                        "$numberLong": "50000000"
                      }
                    },
                    "SDK|ANDROID|STB": {
                      "min": {
                        "$numberLong": "20000000"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|TIZENOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|LGOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|TVOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000"
                      }
                    }
                  },
                  "appBuildLimit": {
                    "IPTV|ANDROID|STB": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|ANDROID|PHONE": {
                      "min": {
                        "$numberLong": "12678"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|ANDROID|TABLET": {
                      "min": {
                        "$numberLong": "12678"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|IOS|PHONE": {
                      "min": {
                        "$numberLong": "118"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "MOBILITY|IOS|TABLET": {
                      "min": {
                        "$numberLong": "118"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "WEB|WEBOS|BROWSER": {
                      "min": {
                        "$numberLong": "36"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "WEB|MWEBOS|BROWSER": {
                      "min": {
                        "$numberLong": "36"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|ANDROID|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "FIRESTICK|ANDROID|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "CHROMECAST|ANDROID|PHONE": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "CHROMECAST|ANDROID|TABLET": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "CHROMECAST|IOS|PHONE": {
                      "min": {
                        "$numberLong": "-1"
                      },
                      "max": {
                        "$numberLong": "1000000"
                      }
                    },
                    "CHROMECAST|IOS|TABLET": {
                      "min": {
                        "$numberLong": "-1"
                      },
                      "max": {
                        "$numberLong": "1000000"
                      }
                    },
                    "SDK|LINUX|STB": {
                      "min": {
                        "$numberLong": "40000000"
                      },
                      "max": {
                        "$numberLong": "50000000"
                      }
                    },
                    "SDK|ANDROID|STB": {
                      "min": {
                        "$numberLong": "20000000"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|TIZENOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|LGOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000000"
                      }
                    },
                    "LARGESCREEN|TVOS|TV": {
                      "min": {
                        "$numberLong": "1"
                      },
                      "max": {
                        "$numberLong": "100000"
                      }
                    }
                  },
                  "cms": [
                    "SDK",
                    "LARGESCREEN",
                    "FIRESTICK",
                    "PTMITRA",
                    "MOBILITY",
                    "CHROMECAST",
                    "PRIMETIMEWEB",
                    "PRIMETIME",
                    "XTREME",
                    "THANKS",
                    "WEB"
                  ],
                  "deviceLimit": "4",
                  "downloadConfig": {
                    "quality": "MEDIUM",
                    "licenseExpiry": {
                      "message": "License has expired",
                      "errorCode": "E1"
                    },
                    "contentEviction": {
                      "message": "Content expired cp side",
                      "errorCode": "E2"
                    },
                    "contentEvictionATV": {
                      "message": "Content has expired ATV side",
                      "errorCode": "E3"
                    },
                    "cpValidity": {
                      "message": "CP validity has expired",
                      "errorCode": "E4"
                    },
                    "airtelValidity": {
                      "message": "Airtel validity has expired",
                      "errorCode": "E5"
                    },
                    "encType": "SELF",
                    "gracePeriod": 2
                  },
                  "expiryTimeEnabled": false,
                  "playValidation": true,
                  "immediateExpiration": true,
                  "defaultAppValue": -1,
                  "active": true,
                  "googleSearchEnabled": true,
                  "chromecastEnabled": true,
                  "defaultIngestionHierarchy": "3",
                  "old_mspConfig": {
                    "secretKey": "nKdFhjFlDHtb4j0hGRJS",
                    "accessKeyID": "ZBpeXlINwE",
                    "sPartnerID": "EROSNOW-XSTREAM-PREPROD"
                  },
                  "mspConfig": {
                    "secretKey": "6G7ynD0kjpsuPuVuIRZ3",
                    "accessKeyID": "Y6ys6a8B4r",
                    "sPartnerID": "EROSNOW-XSTREAM"
                  },
                  "ingestDownloadConfig": [
                    "MOVIE",
                    "TVSHOW",
                    "EPISODE",
                    "SEASON"
                  ],
                  "ingestionUrls": {
                    "episodeUrl": "https://catalog-api.streamready.in/catalog/v1/list/episodes?seasonId={0}&page={1}",
                    "showUrl": "https://catalog-api.streamready.in/catalog/v1/list/seasons?seriesId={0}&page={1}",
                    "catalogListUrl": "https://catalog-api.streamready.in/catalog/v1/list/category?type={0}&category={1}&page={2}"
                  },
                  "ingestionEnabled": false,
                  "language": "HINDI",
                  "isSSOEnable": false,
                  "cronConfig": {
                    "INGESTION": "0 10 7,12,17 * * *",
                    "DELETION": "0 10 3 * * *"
                  },
                  "ca": {
                    "$numberLong": "1631170295284"
                  },
                  "ua": {
                    "$numberLong": "1679913269148"
                  },
                  "ub": "snehil.raghav@wynk.in",
                  "autoRenewalEnabled": true,
                  "playbackConfig": {
                    "playbackUrl": "https://play-api.streamready.in/play/v1/playback",
                    "registerUrl": "",
                    "loginUrl": "",
                    "subscribeUrl": "https://play-api.streamready.in/play/v1/registerAndSubscribe",
                    "subscriptionStatusUrl": "https://play-api.streamready.in/play/v1/userInfo",
                    "authKey": "",
                    "partnerId": "",
                    "certificateUrl": "",
                    "additionalConfig": {
                      "mspSubscriptionId": "60f7de70a3a47d6ca639d295"
                    }
                  }
                }
                """;

        HashSet<String> baseUrls = extractUniqueBaseUrls(paragraph);

        System.out.println("Unique Base URLs:");
        for (String url : baseUrls) {
            System.out.println(url);
        }
    }

    public static HashSet<String> extractUniqueBaseUrls(String paragraph) {
        HashSet<String> baseUrls = new HashSet<>();

        // Regular expression to find URLs in the text
        String regex = "(https?://[\\w.-]+)(?:/[^\\s]*)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(paragraph);

        while (matcher.find()) {
            String url = matcher.group(1);
            baseUrls.add(url);
        }

        return baseUrls;
    }
}

