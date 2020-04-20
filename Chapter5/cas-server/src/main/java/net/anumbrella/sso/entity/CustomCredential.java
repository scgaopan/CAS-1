
package net.anumbrella.sso.entity;

import org.apereo.cas.authentication.UsernamePasswordCredential;

/**
 * @author gaopan
 */
public class CustomCredential extends UsernamePasswordCredential {

    private static final long serialVersionUID = -4166149641561667276L;

    /**第三方登陆的认证code*/
    private String code;

    private String casusername;

    private String caspassword;

    public CustomCredential() {

    }

    public CustomCredential(final String code ) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCasusername() {
        return casusername;
    }

    public void setCasusername(String casusername) {
        this.casusername = casusername;
    }

    public String getCaspassword() {
        return caspassword;
    }

    public void setCaspassword(String caspassword) {
        this.caspassword = caspassword;
    }

    @Override
    protected boolean canEqual(final Object other) {
        return other instanceof CustomCredential;
    }

}
