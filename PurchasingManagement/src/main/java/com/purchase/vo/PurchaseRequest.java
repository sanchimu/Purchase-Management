package com.purchase.vo;

import java.util.Date;

public class PurchaseRequest {
    private String requestId;
    private String requesterName;
    private Date requestDate;
    
    // 기본 생성자
    public PurchaseRequest() {}
    
    // 매개변수 생성자
    public PurchaseRequest(String requestId, String requesterName, Date requestDate) {
        this.requestId = requestId;
        this.requesterName = requesterName;
        this.requestDate = requestDate;
    }
    
    // Getter/Setter
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
}