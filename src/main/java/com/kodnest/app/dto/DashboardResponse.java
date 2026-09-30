
package com.kodnest.app.dto;

public class DashboardResponse {

    private long openingBalance;
    private long closingBalance;
    private long netMovement;
    private long assigned;
    private long expended;

    public long getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(long openingBalance) {
        this.openingBalance = openingBalance;
    }

    public long getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(long closingBalance) {
        this.closingBalance = closingBalance;
    }

    public long getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(long netMovement) {
        this.netMovement = netMovement;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getExpended() {
        return expended;
    }

    public void setExpended(long expended) {
        this.expended = expended;
    }
}