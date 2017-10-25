package com.uet.datn.helpers.validator;

import com.uet.datn.helpers.JenkinsHelper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@DependsOn("JenkinsHelper")
@Configuration("Credential")
public class Credential {

    public DefaultHttpClient setCredentialForJenkins(String username, String password){
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        client.getCredentialsProvider().setCredentials(authScope, credentials);
        client.addRequestInterceptor(new PreemptiveAuth(), 0);

        return client;
    }

    public BasicHttpContext setContext(){
        BasicScheme basicAuth = new BasicScheme();
        BasicHttpContext context = new BasicHttpContext();
        context.setAttribute("preemptive-auth", basicAuth);
        return context;
    }

}
