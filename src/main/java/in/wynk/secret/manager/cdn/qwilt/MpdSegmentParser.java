package in.wynk.secret.manager.cdn.qwilt;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MpdSegmentParser {

    public static void main(String[] args) throws Exception {
        String cdnUrl = "https://iptv-prd-new-main.dlt.qwilted-cds.cqloud.com/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";
        List<String> chunkUrls = parseChunkList(cdnUrl);

        for (String url : chunkUrls) {
            System.out.println(url);
        }
    }

    public static List<String> parseChunkList(String cdnUrl) throws Exception {
        List<String> chunkUrls = new ArrayList<>();
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
                if (segmentTemplate != null) {
                    String mediaTemplate = segmentTemplate.getAttribute("media");
                    if (mediaTemplate != null && !mediaTemplate.isEmpty()) {
                        int startNumber = 1;
                        if (!segmentTemplate.getAttribute("startNumber").isEmpty()) {
                            startNumber = Integer.parseInt(segmentTemplate.getAttribute("startNumber"));
                        }
                        
                        NodeList segmentTimeline = segmentTemplate.getElementsByTagName("SegmentTimeline");
                        if (segmentTimeline != null && segmentTimeline.getLength() > 0) {
                            NodeList sList = ((Element) segmentTimeline.item(0)).getElementsByTagName("S");
                            long currentTime = 0;
                            
                            for (int k = 0; k < sList.getLength(); k++) {
                                Element sElement = (Element) sList.item(k);
                                long t = sElement.hasAttribute("t") ? Long.parseLong(sElement.getAttribute("t")) : currentTime;
                                long d = Long.parseLong(sElement.getAttribute("d"));

                                String chunkUrl = cdnUrl.substring(0, cdnUrl.lastIndexOf('/') + 1) + mediaTemplate
                                        .replace("$RepresentationID$", representationId)
                                        .replace("$Time$", String.valueOf(t));

                                chunkUrls.add("Format: " + (width.isEmpty() ? "Audio" : width + "p") + " -> " + chunkUrl);
                                currentTime = t + d;
                            }
                        }
                    }
                }
            }
        }
        return chunkUrls;
    }
}
