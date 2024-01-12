package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class User {
    private Date createdUtc;
    private String displayName;
    private String email;
    private String id;
    private boolean hasSurvey;
    private boolean isDoNotDisturb;
    private boolean enableOpenPathIntegration;
    private boolean enableBrivoIntegration;
    private boolean enablePDKIntegration;
    private boolean allowMaintenanceRequest;
    private boolean allowAmenitiesBooking;
    private boolean allowBulletinBoard;
    private boolean allowInsuranceRequest;
    private boolean allowGeneralChat;
    private boolean enableRentPayment;
    private boolean enableEENIntegration;
    private boolean isHapticOn;
    private boolean isPushSilent;
    private Date leaseRenewalDateUtc;
    private String locationName;
    private String locationId;
    private String phoneNumber;
    private String unitNbr;
    private boolean allowCommunityNotification;
    private boolean allowSurvey;
    private boolean allowBusinessInvitation;
    private boolean hasExtraIntegration;
    private boolean isPaymentEnable;
    private String mediaCenterLogoUrl;
    private String eenUserName;
    private String eenPassword;
    private String brivoDoorAccessClientSecret;
    private String brivoDoorAccessClientId;
    public String openPathEmail;
    public String openPathCredential;
    public String openPathOrganizationId;
    public String openPathUserId;
    public boolean enableAVAIntegration;
    public String avaUserName;
    public String avaPassword;
    public String avaServerName;

    public String bshUserName;

    public String bshPassword;

    public int bshUserId;

    public String getUnitNbr() {
        return unitNbr;
    }

    public void setUnitNbr(String unitNbr) {
        this.unitNbr = unitNbr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Date getLeaseRenewalDateUtc() {
        return leaseRenewalDateUtc;
    }

    public void setLeaseRenewalDateUtc(Date leaseRenewalDateUtc) {
        this.leaseRenewalDateUtc = leaseRenewalDateUtc;
    }

    public boolean isEnableRentPayment() {
        return enableRentPayment;
    }

    public void setEnableRentPayment(boolean enableRentPayment) {
        this.enableRentPayment = enableRentPayment;
    }

    public boolean isAllowGeneralChat() {
        return allowGeneralChat;
    }

    public void setAllowGeneralChat(boolean allowGeneralChat) {
        this.allowGeneralChat = allowGeneralChat;
    }

    public boolean isAllowInsuranceRequest() {
        return allowInsuranceRequest;
    }

    public void setAllowInsuranceRequest(boolean allowInsuranceRequest) {
        this.allowInsuranceRequest = allowInsuranceRequest;
    }

    public boolean isAllowBulletinBoard() {
        return allowBulletinBoard;
    }

    public void setAllowBulletinBoard(boolean allowBulletinBoard) {
        this.allowBulletinBoard = allowBulletinBoard;
    }

    public boolean isAllowAmenitiesBooking() {
        return allowAmenitiesBooking;
    }

    public void setAllowAmenitiesBooking(boolean allowAmenitiesBooking) {
        this.allowAmenitiesBooking = allowAmenitiesBooking;
    }

    public boolean isAllowMaintenanceRequest() {
        return allowMaintenanceRequest;
    }

    public void setAllowMaintenanceRequest(boolean allowMaintenanceRequest) {
        this.allowMaintenanceRequest = allowMaintenanceRequest;
    }

    public boolean isEnableBrivoIntegration() {
        return enableBrivoIntegration;
    }

    public void setEnableBrivoIntegration(boolean enableBrivoIntegration) {
        this.enableBrivoIntegration = enableBrivoIntegration;
    }

    public boolean isEnableOpenPathIntegration() {
        return enableOpenPathIntegration;
    }

    public void setEnableOpenPathIntegration(boolean enableOpenPathIntegration) {
        this.enableOpenPathIntegration = enableOpenPathIntegration;
    }

    public boolean isDoNotDisturb() {
        return isDoNotDisturb;
    }

    public void setDoNotDisturb(boolean doNotDisturb) {
        isDoNotDisturb = doNotDisturb;
    }

    public boolean isHasSurvey() {
        return hasSurvey;
    }

    public void setHasSurvey(boolean hasSurvey) {
        this.hasSurvey = hasSurvey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(Date createdUtc) {
        this.createdUtc = createdUtc;
    }

    public boolean isAllowCommunityNotification() {
        return allowCommunityNotification;
    }

    public void setAllowCommunityNotification(boolean allowCommunityNotification) {
        this.allowCommunityNotification = allowCommunityNotification;
    }

    public boolean isAllowSurvey() {
        return allowSurvey;
    }

    public void setAllowSurvey(boolean allowSurvey) {
        this.allowSurvey = allowSurvey;
    }

    public boolean isAllowBusinessInvitation() {
        return allowBusinessInvitation;
    }

    public void setAllowBusinessInvitation(boolean allowBusinessInvitation) {
        this.allowBusinessInvitation = allowBusinessInvitation;
    }

    public boolean isHasExtraIntegration() {
        return hasExtraIntegration;
    }

    public void setHasExtraIntegration(boolean hasExtraIntegration) {
        this.hasExtraIntegration = hasExtraIntegration;
    }

    public boolean isPaymentEnable() {
        return isPaymentEnable;
    }

    public void setPaymentEnable(boolean paymentEnable) {
        isPaymentEnable = paymentEnable;
    }

    public String getMediaCenterLogoUrl() {
        return mediaCenterLogoUrl;
    }

    public void setMediaCenterLogoUrl(String mediaCenterLogoUrl) {
        this.mediaCenterLogoUrl = mediaCenterLogoUrl;
    }

    public String getEenUserName() {
        return eenUserName;
    }

    public void setEenUserName(String eenUserName) {
        this.eenUserName = eenUserName;
    }

    public String getEenPassword() {
        return eenPassword;
    }

    public void setEenPassword(String eenPassword) {
        this.eenPassword = eenPassword;
    }

    public boolean isEnableEENIntegration() {
        return enableEENIntegration;
    }

    public void setEnableEENIntegration(boolean enableEENIntegration) {
        this.enableEENIntegration = enableEENIntegration;
    }

    public boolean isHapticOn() {
        return isHapticOn;
    }

    public void setHapticOn(boolean hapticOn) {
        isHapticOn = hapticOn;
    }

    public String getBrivoDoorAccessClientSecret() {
        return brivoDoorAccessClientSecret;
    }

    public void setBrivoDoorAccessClientSecret(String brivoDoorAccessClientSecret) {
        this.brivoDoorAccessClientSecret = brivoDoorAccessClientSecret;
    }

    public String getBrivoDoorAccessClientId() {
        return brivoDoorAccessClientId;
    }

    public void setBrivoDoorAccessClientId(String brivoDoorAccessClientId) {
        this.brivoDoorAccessClientId = brivoDoorAccessClientId;
    }

    public boolean isPushSilent() {
        return isPushSilent;
    }

    public void setPushSilent(boolean pushSilent) {
        isPushSilent = pushSilent;
    }

    public String getOpenPathCredential() {
        return openPathCredential;
    }

    public void setOpenPathCredential(String openPathCredential) {
        this.openPathCredential = openPathCredential;
    }

    public boolean isEnablePDKIntegration() {
        return enablePDKIntegration;
    }

    public void setEnablePDKIntegration(boolean enablePDKIntegration) {
        this.enablePDKIntegration = enablePDKIntegration;
    }

    public String getBshUserName() {
        return bshUserName;
    }

    public void setBshUserName(String bshUserName) {
        this.bshUserName = bshUserName;
    }

    public String getBshPassword() {
        return bshPassword;
    }

    public void setBshPassword(String bshPassword) {
        this.bshPassword = bshPassword;
    }

    public int getBshUserId() {
        return bshUserId;
    }

    public void setBshUserId(int bshUserId) {
        this.bshUserId = bshUserId;
    }
}