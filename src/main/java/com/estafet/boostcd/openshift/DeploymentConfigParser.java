package com.estafet.boostcd.openshift;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.openshift.restclient.model.IDeploymentConfig;

public class DeploymentConfigParser {

	private final JSONObject jo;

	public DeploymentConfigParser(IDeploymentConfig deploymentConfig) {
		this(deploymentConfig.toJson());
	}

	public DeploymentConfigParser(String json) {
		try {
			this.jo = (JSONObject) new JSONParser().parse(new StringReader(json));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public String getDeployedDate() {
		JSONObject status = ((JSONObject) jo.get("status"));
		if (status == null) return null;
		JSONArray conditions = (JSONArray) status.get("conditions");
		if (conditions == null) return null;
		Iterator<JSONObject> itr = conditions.iterator();
		while (itr.hasNext()) {
			JSONObject condition = itr.next();
			if (condition.get("type").equals("Progressing")) {
				return (String) condition.get("lastUpdateTime");
			}
		}
		return null;
	}

	public String getVersion() {
		JSONObject metadata = ((JSONObject) jo.get("metadata"));
		JSONObject labels = ((JSONObject) metadata.get("labels"));
		String environment = (String)labels.get("environment"); 
		String name = (String)metadata.get("name"); 
		name = environment.equals("") ? name : name.replaceFirst(environment, "");
		JSONObject spec = ((JSONObject) jo.get("spec"));
		JSONArray triggers = (JSONArray) spec.get("triggers");
		JSONObject trigger = (JSONObject) triggers.get(0);
		JSONObject imageChangeParams = (JSONObject)trigger.get("imageChangeParams");
		JSONObject from = (JSONObject) imageChangeParams.get("from");
		String image = (String) from.get("name");
		return image.replaceFirst(Pattern.quote(name) + "\\:", "");
	}

	public String getReadinessPort() {
        return getReadinessAttribute("port");
    }

    public String getReadinessPath() {
		return getReadinessAttribute("path");
	}

    private String getReadinessAttribute(String attribute) {
        JSONObject spec1 = (JSONObject) jo.get("spec");
		JSONObject template = (JSONObject) spec1.get("template");
		JSONObject spec2 = (JSONObject) template.get("spec");
		JSONArray containers = (JSONArray) spec2.get("containers");
		JSONObject container =   (JSONObject)containers.get(0);
		JSONObject readinessProbe =   (JSONObject)container.get("readinessProbe");
		JSONObject httpGet = (JSONObject) readinessProbe.get("httpGet");
		return httpGet.get(attribute).toString();
    }

}
