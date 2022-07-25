/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

/**
 *
 * @author sinatti_s
 */
public class DLQMsgInfoExt extends DLQMsgInfo {

    private int countMsg = 1; // default

    public int getCountMsg() {
        return countMsg;
    }

    public void incCountMsg() {
        this.countMsg = this.countMsg + 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DLQMsgInfo other = (DLQMsgInfo) obj;
        if (payloadType == null) {
            if (other.payloadType != null)
                return false;
        } else if (!payloadType.equals(other.payloadType))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

}
