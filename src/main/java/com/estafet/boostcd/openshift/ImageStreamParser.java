package com.estafet.boostcd.openshift;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.openshift.restclient.model.IImageStream;

public class ImageStreamParser {

	private final JSONObject jo;

	public ImageStreamParser(IImageStream imageStream) {
		this(imageStream.toJson());
	}

	public ImageStreamParser(String json) {
		try {
			this.jo = (JSONObject) new JSONParser().parse(new StringReader(json));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getLatestTag() {
		return getShaByTag("latest");
	}

	@SuppressWarnings("unchecked")
	public String getShaByTag(String tag) {
		JSONObject status = (JSONObject) jo.get("status");
		JSONArray tags = (JSONArray) status.get("tags");
		Iterator<JSONObject> tagsIterator = tags.iterator();
		while (tagsIterator.hasNext()) {
			JSONObject tagObject = tagsIterator.next();
			if (tagObject.get("tag").equals(tag)) {
				JSONArray items = (JSONArray) tagObject.get("items");
				JSONObject item = (JSONObject) items.get(0);
				return (String) item.get("image");
			}
		}
		throw new RuntimeException("Cannot find sha for tag - " + tag);
	}

	@SuppressWarnings("unchecked")
	public String getTagBySha(String sha) {
		JSONObject status = (JSONObject) jo.get("status");
		JSONArray tags = (JSONArray) status.get("tags");
		Iterator<JSONObject> tagsIterator = tags.iterator();
		while (tagsIterator.hasNext()) {
			JSONObject tagObject = tagsIterator.next();
			JSONArray items = (JSONArray) tagObject.get("items");
			if (isShaMatch(sha, items)) {
				return (String) tagObject.get("tag");	
			}
		}
		throw new RuntimeException("Cannot find sha for tag - " + sha);
	}
	
	@SuppressWarnings("unchecked")
	private boolean isShaMatch(String sha, JSONArray items) {
		Iterator<JSONObject> itemsIterator = items.iterator();
		while (itemsIterator.hasNext()) {
			JSONObject item = itemsIterator.next();
			if (item.get("image").equals(sha)) {
				return true;
			}
		}
		return false;
	}

}
