package com.dbgo.platform.login.app.config;

import com.dbgo.framework.core.filter.SessionTimeoutFilter;
import com.dbgo.platform.login.app.config.redisSessionShared.RedisCacheManager;
import com.dbgo.platform.login.app.config.redisSessionShared.RedisSessionDAO;
import com.dbgo.platform.login.app.service.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 */
@Configuration
public class ShiroConfig {

    @Bean(name="sessionDAO")
    public SessionDAO sessionDAO(){
        return new RedisSessionDAO();
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        return new RedisCacheManager();
    }

    @Bean(name = "sessionManager")
    public SessionManager sessionManager(SessionDAO sessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //设置session过期时间为3小时(单位：毫秒)，默认为30分钟
        sessionManager.setGlobalSessionTimeout(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT * 6);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setDeleteInvalidSessions(true);// 删除过期的session
        sessionManager.setCacheManager(redisCacheManager());

        //设置cookie httpOnly属性
        sessionManager.setSessionIdCookieEnabled(true);
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        simpleCookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(simpleCookie);
        return sessionManager;
    }


    @Bean(name = "securityManager")
    public SecurityManager securityManager(UserRealm userRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login.html");
        shiroFilter.setUnauthorizedUrl("/");

        Map map = new HashMap<String, Filter>();
        SessionTimeoutFilter sessionTimeoutFilter =  new SessionTimeoutFilter();
        map.put("authc",sessionTimeoutFilter);
        shiroFilter.setFilters(map);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/static/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/Ext/**", "anon");
        filterMap.put("/assets/**", "anon");


        filterMap.put("/login.html", "anon");
        filterMap.put("/doLogin", "anon");
        //验证码 qiweb 2017年10月18日20:12:35
        filterMap.put("/captcha**", "anon");
        filterMap.put("/rsa", "anon");
        filterMap.put("/invalidateinvoice/invalidInvoiceCallBack", "anon");
        filterMap.put("/soap/*", "anon");
        filterMap.put("/checkinvoice/getBase64String", "anon");
        filterMap.put("/invoice/invoiceHXCallBack", "anon");
        filterMap.put("/invoice/electronCallBack", "anon");
        filterMap.put("/printinvoice/printlistback", "anon");
        filterMap.put("/printinvoice/printinvoiceback", "anon");
        filterMap.put("/invoice/stockHXCallBack", "anon");
        filterMap.put("/ininovoicecheck/tokenCallBack","anon");
        filterMap.put("/ininovoicecheck/submitCheckedCallBack","anon");
        filterMap.put("/ininovoicecheck/cancelCheckedCallBack","anon");
        filterMap.put("/ininovoicecheck/confirmCheckedCallBack","anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/**", "authc");
        filterMap.put("/logout", "anon");
        filterMap.put("/index.html", "authc");
        filterMap.put("/druid/*", "anon");
        filterMap.put("/error", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

   /* @Bean(name="sessionDAO")
    public SessionDAO sessionDAO(){
        return new MemorySessionDAO();
    }

    @Bean(name = "sessionManager")
    public SessionManager sessionManager(SessionDAO sessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //设置session过期时间为3小时(单位：毫秒)，默认为30分钟
        sessionManager.setGlobalSessionTimeout(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT * 6);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setSessionDAO(sessionDAO);

        //设置cookie httpOnly属性
        sessionManager.setSessionIdCookieEnabled(true);
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        simpleCookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(simpleCookie);
        return sessionManager;
    }


    @Bean(name = "securityManager")
    public SecurityManager securityManager(UserRealm userRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login.html");
        shiroFilter.setUnauthorizedUrl("/");

        Map map = new HashMap<String, Filter>();
        SessionTimeoutFilter sessionTimeoutFilter =  new SessionTimeoutFilter();
        map.put("authc",sessionTimeoutFilter);
        shiroFilter.setFilters(map);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/static*//**", "anon");
        filterMap.put("/app*//**", "anon");
        filterMap.put("/Ext*//**", "anon");
        filterMap.put("/assets*//**", "anon");


        filterMap.put("/login.html", "anon");
        filterMap.put("/doLogin", "anon");
        //验证码 qiweb 2017年10月18日20:12:35
        filterMap.put("/captcha**", "anon");
        filterMap.put("/rsa", "anon");
        filterMap.put("/invalidateinvoice/invalidInvoiceCallBack", "anon");
        filterMap.put("/soap*//*", "anon");
        filterMap.put("/checkinvoice/getBase64String", "anon");
        filterMap.put("/invoice/invoiceHXCallBack", "anon");
        filterMap.put("/invoice/electronCallBack", "anon");
        filterMap.put("/printinvoice/printlistback", "anon");
        filterMap.put("/printinvoice/printinvoiceback", "anon");
        filterMap.put("/invoice/stockHXCallBack", "anon");
        filterMap.put("/ininovoicecheck/tokenCallBack","anon");
        filterMap.put("/ininovoicecheck/submitCheckedCallBack","anon");
        filterMap.put("/ininovoicecheck/cancelCheckedCallBack","anon");
        filterMap.put("/ininovoicecheck/confirmCheckedCallBack","anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("*//**", "authc");
        filterMap.put("/logout", "anon");
        filterMap.put("/index.html", "authc");
        filterMap.put("/druid*//*", "anon");
        filterMap.put("/error", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }*/

}
