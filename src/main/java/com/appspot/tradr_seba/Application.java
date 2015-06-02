package com.appspot.tradr_seba;

import twirl.api.Html;

public class Application {
    
    public static String index() {
        return html.index.render().toString();
    }
    
}