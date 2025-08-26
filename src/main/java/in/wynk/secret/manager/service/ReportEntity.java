package in.wynk.secret.manager.service;

import in.wynk.secret.manager.dto.ContentPartner;
import lombok.Data;

@Data
public class ReportEntity {

    private ContentPartner cp;

    private String source;

    private String scope;

    private String reportType;

    private String severity;

    private String description;

    private String methodName;

    private String metaMap;

    private String contentId;

    private String uid;

    private String requestId;

    private String reportDependency;

    private String deviceType;

    private String contentType;

    private String responseDetails;

    private String title;
    public ReportEntity(ContentPartner cp, String source, String scope, String reportType, String severity, String description, String methodName, String metaMap, String contentId, String uid, String requestId, String reportDependency, String deviceType, String contentType, String responseDetails, String title) {
        this.cp = cp;
        this.source = source;
        this.scope = scope;
        this.reportType = reportType;
        this.severity = severity;
        this.description = description;
        this.methodName = methodName;
        this.metaMap = metaMap;
        this.contentId = contentId;
        this.uid = uid;
        this.requestId = requestId;
        this.reportDependency = reportDependency;
        this.deviceType = deviceType;
        this.contentType = contentType;
        this.responseDetails = (responseDetails!= null)?responseDetails:"NA";  // set default value as "NA"
        this.title = title;
    }
}
