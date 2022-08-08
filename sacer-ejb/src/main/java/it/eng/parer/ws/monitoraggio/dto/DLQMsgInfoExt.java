/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

import java.util.Objects;

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
        int result = super.hashCode();
        result = prime * result + Objects.hash(countMsg);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof DLQMsgInfoExt)) {
            return false;
        }
        DLQMsgInfoExt other = (DLQMsgInfoExt) obj;
        return countMsg == other.countMsg;
    }

}
