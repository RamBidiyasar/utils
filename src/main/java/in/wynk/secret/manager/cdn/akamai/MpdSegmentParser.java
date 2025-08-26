package in.wynk.secret.manager.cdn.akamai;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MpdSegmentParser {
    public static void main(String[] args) throws Exception {
        String cdnUrl = "https://iptv-production-auth.dlt.qwilted-cds.cqloud.com/qsig=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjZG4tYWNjZXNzIiwiaWF0IjoxNzQyNTM1MDcwLCJleHAiOjE3NDI2MjE0NzAsImtpZCI6MSwidHlwIjoic2duIiwiY250Ijo0LCJoc2giOiI1OGQwZjhmNmZmMTEyODNhNTEwZWQ4OWU0Mjc5YjkwNiJ9.KBVIefAct1dWAClmbI-QISvBeOc5zQMEUxRFPkqlyeM/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";
        List<String> segmentUrls = parseMpd(cdnUrl);

        for (String url : segmentUrls) {
            System.out.println(url);
        }
    }

    public static List<String> parseMpd(String cdnUrl) throws Exception {
        List<String> segmentUrls = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new URL(cdnUrl).openStream());
        doc.getDocumentElement().normalize();

        NodeList adaptationSets = doc.getElementsByTagName("AdaptationSet");
        for (int i = 0; i < adaptationSets.getLength(); i++) {
            Element adaptationSet = (Element) adaptationSets.item(i);
            NodeList representations = adaptationSet.getElementsByTagName("Representation");
            String mimeType = adaptationSet.getAttribute("mimeType");

            for (int j = 0; j < representations.getLength(); j++) {
                Element representation = (Element) representations.item(j);
                String representationId = representation.getAttribute("id");
                String width = representation.getAttribute("width");

                Element segmentTemplate = (Element) adaptationSet.getElementsByTagName("SegmentTemplate").item(0);
                String initTemplate = segmentTemplate.getAttribute("initialization");
                String initUrl = cdnUrl.substring(0, cdnUrl.lastIndexOf('/') + 1) + initTemplate.replace("$RepresentationID$", representationId);

                if ("audio/mp4".equals(mimeType) || "video/mp4".equals(mimeType)) {
                    segmentUrls.add("Format: " + (width.isEmpty() ? "Audio" : width + "p") + " -> " + initUrl);
                }
            }
        }
        return segmentUrls;
    }
}
