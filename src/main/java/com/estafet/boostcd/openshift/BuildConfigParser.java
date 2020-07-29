package com.estafet.boostcd.openshift;

import java.io.IOException;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.openshift.restclient.model.IBuildConfig;

public class BuildConfigParser {

	private final JSONObject jo;

	public BuildConfigParser(IBuildConfig buildConfig) {
		this(buildConfig.toJson());
	}

	public BuildConfigParser(String json) {
		try {
			this.jo = (JSONObject) new JSONParser().parse(new StringReader(json));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getGitRepository() {
		JSONObject spec = (JSONObject) jo.get("spec");
		JSONObject source = (JSONObject) spec.get("source");
		JSONObject git = (JSONObject) source.get("git");
		return (String)git.get("uri");
	}

}
