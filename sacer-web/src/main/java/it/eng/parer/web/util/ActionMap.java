/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.eng.parer.web.util;

import java.util.Objects;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author Parucci_M
 */
public class ActionMap {
    private String action;
    private Object id;
    private Object appo;
    private String father = null;

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public Object getAppo() {
        return appo;
    }

    public void setAppo(Object appo) {
        this.appo = appo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ActionMap{" + "Action=" + action + ", id=" + id + '}';
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37).append(action).append(id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActionMap other = (ActionMap) obj;
        if (!Objects.equals(this.action, other.action)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
