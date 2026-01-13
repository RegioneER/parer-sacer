/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.util.ejb;

import javax.ejb.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Singleton
@LocalBean
@Startup
public class AppServerInstanceTest {

    @Lock(LockType.READ)
    public String getName() {
        return "arquillian";
    }

    enum AddressTypes {
        SITE_LOCAL_WITH_NAME, NON_SITE_LOCAL_WITH_NAME, SITE_LOCAL_WITHOUT_NAME_IPV4,
        NON_SITE_LOCAL_WITHOUT_NAME_IPV4, SITE_LOCAL_WITHOUT_NAME_IPV6,
        NON_SITE_LOCAL_WITHOUT_NAME_IPV6, LOOPBACK_IPV4, LOOPBACK_IPV6
    }

    private InetAddress getMyHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    private AddressTypes decodeAddrType(InetAddress inetAddress) {
        return AddressTypes.SITE_LOCAL_WITHOUT_NAME_IPV4;
    }

    private boolean isAddressWithHostName(InetAddress inetAddress) {
        return false;
    }
}
