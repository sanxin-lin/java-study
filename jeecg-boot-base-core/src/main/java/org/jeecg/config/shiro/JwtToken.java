/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 15:54
 **/

package org.jeecg.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author Sunshine_Lin
 * @desc
 **/
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
