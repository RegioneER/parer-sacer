/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

/**
 *
 * @author fioravanti_f
 */
public class MonFakeSessn {

    private String ipChiamante;
    private String loginName;
    private String password;

    public String getIpChiamante() {
        return ipChiamante;
    }

    public void setIpChiamante(String ipChiamante) {
        this.ipChiamante = ipChiamante;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
