package net.anumbrella.sso.config;

import net.anumbrella.sso.entity.CustomCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * @author gaopan
 */
public class CustomWebflowConfigurer extends AbstractCasWebflowConfigurer {


    public CustomWebflowConfigurer(FlowBuilderServices flowBuilderServices,
                                   FlowDefinitionRegistry flowDefinitionRegistry,
                                   ApplicationContext applicationContext,
                                   CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
    }


    @Override
    protected void doInitialize() {
        final Flow flow = super.getLoginFlow();
        bindCredential(flow);
    }

    /**
     * 绑定自定义的Credential信息
     *
     * @param flow
     */
    protected void bindCredential(Flow flow) {

        // 重写绑定自定义cred重写绑定自定义credentialential
        //
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomCredential.class);

        // 登录页绑定新参数
        final ViewState state = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
        // 由于用户名以及密码已经绑定，所以只需对新加系统参数绑定即可
        // 字段名 code 为企业微信登陆时的code
        cfg.addBinding(new BinderConfiguration.Binding("code", null, false));

        //cfg.addBinding(new BinderConfiguration.Binding("username", null, false));
        //cfg.addBinding(new BinderConfiguration.Binding("password", null, false));
        //由于cas登陆用户名密码是必填字段，故新新字段来接受用户名和密码
        cfg.addBinding(new BinderConfiguration.Binding("casusername", null, false));
        cfg.addBinding(new BinderConfiguration.Binding("caspassword", null, false));

    }
}
