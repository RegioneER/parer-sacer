/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author sinatti_s
 */
public class DLQMsgInfo {

    private String messageID;
    protected String payloadType;
    protected String fromApplication;
    protected String state;
    private Date sentTimestamp;
    private int deliveryCount;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    public Date getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(Date sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public int getDeliveryCount() {
        return deliveryCount;
    }

    public void setDeliveryCount(int deliveryCount) {
        this.deliveryCount = deliveryCount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFromApplication() {
        return fromApplication;
    }

    public void setFromApplication(String fromApplication) {
        this.fromApplication = fromApplication;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryCount, fromApplication, messageID, payloadType, sentTimestamp, state);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DLQMsgInfo)) {
            return false;
        }
        DLQMsgInfo other = (DLQMsgInfo) obj;
        return deliveryCount == other.deliveryCount && Objects.equals(fromApplication, other.fromApplication)
                && Objects.equals(messageID, other.messageID) && Objects.equals(payloadType, other.payloadType)
                && Objects.equals(sentTimestamp, other.sentTimestamp) && Objects.equals(state, other.state);
    }
}
