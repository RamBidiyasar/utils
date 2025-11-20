```mermaid
timeline
    title "Complete Chronological Flow for User +919810992024"
    section "20:39 IST - Pre-Purchase & Purchase"
        "20:39:28" : System provisions initial free plans (wcfKafkaEvent)
        "20:39:35" : <strong>User Purchases a New Subscription Plan (Root Cause)</strong>
    section "20:41 IST - Primary Activation & First Benefit Claim"
        "20:41:05.326" : System notifies partner 'Darts' of the purchase (dartsSolaceProducer)
        "20:41:05.352" : System starts auto-claiming <strong>Xstream Premium</strong> (claimSubscription)
        "20:41:05.353" : System broadcasts claim update via Solace (updatePartnerClaimsViaSolace)
        "20:41:05.354" : Main 'SUBSCRIBE' event for Xstream Premium is processed (wcfKafkaEvent)
        "20:41:05.942" : System auto-claims the first bundled plan (planId: 77081) & sends a push notification (wcfKafkaEvent)
        "20:41:09.613" : Partner 'Darts' acknowledges the request (dartsSolaceConsumer)
        "20:41:10.041" : System receives 'Thanks' app event to activate <strong>Hotstar</strong> (thanksSolaceConsumer)
        "20:41:15.246" : Partner 'Darts' sends final confirmation of update (dartsSolaceConsumer)
        "20:41:15.560" : System looks up service endpoints (serviceDiscover)
        "20:41:15.639" : System finalizes Hotstar claim based on 'Thanks' event (claimSubscription)
        "20:41:15.640" : System confirms all 'Thanks' claims are synced (thanksSyncedClaims)
        "20:41:15.643" : System broadcasts final partner claim status (updatePartnerClaimsViaSolace)
        "20:41:16.313" : System processes the Hotstar claim (planId: 99115) & sends a push notification (wcfKafkaEvent)
    section "21:14 IST - Netflix Activation"
        "21:14:03" : System auto-claims <strong>Netflix</strong> benefit (planId: 77051) & sends a push notification (wcfKafkaEvent)
    section "21:39 IST - Zee Activation"
        "21:39:32" : System auto-claims <strong>Zee</strong> benefit (planId: 77091) & sends a push notification (wcfKafkaEvent)
```