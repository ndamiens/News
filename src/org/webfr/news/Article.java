package org.webfr.news;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import android.util.Log;

import java.util.Date;

public class Article {
	private String id;
	private String titre;
	private String site;
	private Date date;

	public Article(Node elem) {
		NodeList props = elem.getChildNodes();
		for(int j=0; j<props.getLength(); j++) {
			Node prop = props.item(j);
			String propname = prop.getNodeName();
			if (propname.equalsIgnoreCase("titre")) {
				titre = prop.getFirstChild().getNodeValue();
			} else if (propname.equalsIgnoreCase("site")) {
				site = prop.getFirstChild().getNodeValue();
			} else {
				Log.i(News.LOGNAME, "Tag inconnu : "+propname);
			}
		}	
		Element e = (Element)elem;
		id = e.getAttribute("id");
	}

	public Article(String id, String titre, String site) {
		this.titre = titre;
		this.site = site;
		this.id = id;
	}

	public String getTitre() {
		return this.titre;
	}

	public String getSite() {
		return this.site;
	}

	public String getId() {
		return this.id;
	}

	public String getUrlRedirect() {
		return "http://www.web-fr.org/?p=r&a="+this.id;
	}

	public String association(int id_categorie) {
		String args[] = {"a", this.id, "g", String.valueOf(id_categorie)};
		return News.downloadDoc("asso", args);
	}

	public String getDesc() {
		String args[] = {"id", this.id};
		return News.downloadDoc("article_description", args);
	}
}
