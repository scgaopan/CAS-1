package net.anumbrella.sso.authentication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.anumbrella.sso.entity.CustomCredential;
import net.anumbrella.sso.entity.User;
import net.anumbrella.sso.util.HttpConnectionUtils;
import net.anumbrella.sso.util.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gaopan
 */
@Component
public class CustomerHandlerAuthentication extends AbstractPreAndPostProcessingAuthenticationHandler {

    private static  final Logger logger= LoggerFactory.getLogger(CustomerHandlerAuthentication.class);

    public CustomerHandlerAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Value("${thrid.validateUrl}")
    private String  validateUrl;


    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof CustomCredential;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException {

        CustomCredential customCredential = (CustomCredential) credential;

        String casusername = customCredential.getCasusername();
        String caspassword = customCredential.getCaspassword();
        String code = customCredential.getCode();
        logger.warn("cas登陆接口：casusername={},caspassword={},code={}",casusername,caspassword,code);

        /***调用主数据接口去认证用户名密码start***/
        User info =null;

        if(StringUtils.isNotEmpty(casusername)&&StringUtils.isNotEmpty(caspassword)){
            try {
                String s = HttpConnectionUtils.doGet(String.format(validateUrl, StringUtils.trim(casusername), MD5.nomalSign(StringUtils.trim(caspassword), "UTF-8")));

                JSONObject jsonObject = JSON.parseObject(s);
                String retCode = jsonObject.getString("code");
                if(StringUtils.equals(retCode,"01")){
                    info = new User();
                    info.setUsername(StringUtils.trim(casusername));
                }

            } catch (IOException e) {
                logger.error("调用MDM登陆接口出错，username={},password={},code={}",casusername,caspassword,code,e);
            }
        }else if(StringUtils.isNotEmpty(code)){
            logger.info("调用code={}去换取userid==================");
            String userid="09000852";
            info=new User();
            info.setUsername(userid);
        }else{
            logger.warn("登陆时用户名和密码、code都为空");
            throw new AccountException("用户名密码不匹配");
        }
        /***调用主数据接口去认证用户名密码start***/

        if (info == null) {
            throw new AccountException("用户名密码不匹配");
        }else {
            final List<MessageDescriptor> list = new ArrayList<>();
            return createHandlerResult(customCredential,
                    this.principalFactory.createPrincipal(casusername, Collections.emptyMap()), list);
        }

    }
}
