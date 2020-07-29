package com.estafet.boostcd.openshift;

import java.io.IOException;
import java.io.StringReader;

import com.openshift.restclient.model.IService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServiceParser {

    private final JSONObject jo;
    
    public ServiceParser(IService service) {
        this(service.toJson());
    }

    public ServiceParser(String json) {
		try {
			this.jo = (JSONObject) new JSONParser().parse(new StringReader(json));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}        
    }

    public String clusterIP() {
        JSONObject spec = (JSONObject) jo.get("spec");
        return (String)spec.get("clusterIP");
    }

}