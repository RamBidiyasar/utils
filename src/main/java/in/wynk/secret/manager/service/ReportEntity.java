package in.wynk.secret.manager.service;

import in.wynk.secret.manager.dto.ContentPartner;

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
        this.responseDetails = (responseDetails!= null)?responseDetails:"NA";
        this.title = title;
    }

    public ContentPartner getCp() { return cp; }
    public void setCp(ContentPartner cp) { this.cp = cp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getMetaMap() { return metaMap; }
    public void setMetaMap(String metaMap) { this.metaMap = metaMap; }
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getReportDependency() { return reportDependency; }
    public void setReportDependency(String reportDependency) { this.reportDependency = reportDependency; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getResponseDetails() { return responseDetails; }
    public void setResponseDetails(String responseDetails) { this.responseDetails = responseDetails; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}