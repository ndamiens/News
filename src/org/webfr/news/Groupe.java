package org.webfr.news;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import android.util.Log;

import java.util.Date;

public class Groupe {
	private String id;
	private String nom;

	public Groupe(Node elem) {
		Element e = (Element)elem;
		id = e.getAttribute("id");
		nom = e.getAttribute("nom");
	}

	public Groupe(String id, String nom) {
		this.nom = nom;
		this.id = id;
	}

	public Groupe(int id, String nom) {
		this.nom = nom;
		this.id = String.valueOf(id);
	}

	public String getId() {
		return this.id;
	}

	public String getNom() {
		return this.nom;
	}
}
