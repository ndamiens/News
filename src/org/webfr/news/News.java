package org.webfr.news;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ListView;
import android.util.Log;
import android.net.Uri;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

public class News extends NetActivity {
	public final static String RELEASE = "7";
	public final static String LOGNAME = "NewsClient";
	private final static String BASEURL = "http://www.web-fr.org/";
	public static Article article_ouvert;
	public static int position;
	public static ArticleAdapter adapter;
	public static int groupe = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOGNAME, "web-fr.org news reader activity create");
		setContentView(R.layout.main);
		List<Article> articles;
		if (!testReseau()) {
			articles = new ArrayList<Article>();
			dlgSansReseau();
		} else {
			articles = getTitres();
		}
		ListView lv = (ListView)findViewById(R.id.lv);
		adapter = new ArticleAdapter(this, articles);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView)findViewById(R.id.lv);
				article_ouvert = (Article)lv.getAdapter().getItem(position);
				Log.i(LOGNAME,"Article "+article_ouvert.getTitre());
				News.position = position;
				Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		List<Groupe> groupes = getGroupes();
		for (int i=0;i<groupes.size();i++) {
			Groupe groupe = groupes.get(i);
			menu.add(Menu.NONE, new Integer(groupe.getId()), Menu.NONE, groupe.getNom());
		}
		menu.add(Menu.NONE, 999, Menu.NONE, "Tous");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		groupe = item.getItemId();
		if (groupe == 999) groupe = -1;
		reloadData();
		return true;
	}

	public void reloadData() {
		List<Article> articles = getTitres();
		ListView lv = (ListView)findViewById(R.id.lv);
		adapter.setArticles(articles);
	}

	public static String downloadDoc(String page, String arguments[]) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet;

		Uri.Builder urib = Uri.parse(BASEURL).buildUpon();
		urib.appendQueryParameter("p", page);
		urib.appendQueryParameter("android_app_version", RELEASE);
		if (arguments != null) {
			for (int i=0; i<arguments.length; i+=2) {
				urib.appendQueryParameter(arguments[i], arguments[i+1]);
			}
		}
		String url = urib.build().toString();
		Log.i(LOGNAME, "Download "+url);
		httpGet	= new HttpGet(url);
		StringBuilder xml_str = new StringBuilder();
		try {
			String l;
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			l = bufferedReader.readLine();
			while (l != null) {
				xml_str.append(l);
				l = bufferedReader.readLine();
			}
			bufferedReader.close();
			Log.e(LOGNAME, "Get terminé");
		} catch (IOException e) {
			Log.e(LOGNAME, e.getMessage());
			Log.e(LOGNAME, e.getLocalizedMessage());
			return null;
		}
		return xml_str.toString();
	}

	private List<Article> readArticles(String xml_str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Article> articles = new ArrayList<Article>();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml_str.getBytes());
			Document doc = builder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList elems = root.getElementsByTagName("article");
			for (int i=0; i<elems.getLength(); i++) {
				articles.add(new Article(elems.item(i)));
			}
		} catch (ParserConfigurationException e) {
			Log.e(LOGNAME, "Erreur de configuration du parseur");
			Log.e(LOGNAME, e.getMessage());
		} catch (org.xml.sax.SAXException e) {
			Log.e(LOGNAME, "Erreur de lecture du document XML");
			Log.e(LOGNAME, e.getMessage());
		} catch (java.io.IOException e) {
			Log.e(LOGNAME, "Erreur entrée/sortie");
			Log.e(LOGNAME, e.getMessage());
		}
		return articles;
	}

	public List<Article> getTitres() {
		String doc;
		TextView tv = (TextView)findViewById(R.id.etat);
		if (tv != null) tv.setText("Récupération des titres");
		if (groupe == -1) 
			doc = downloadDoc("xml_titres", null);
		else {
			String args[] = {"groupe", String.valueOf(this.groupe)};
			doc = downloadDoc("xml_titres", args);
		}

		if (tv != null) tv.setText("Lecture des titres");
		List<Article> articles = readArticles(doc);
		String msg = articles.size()+" articles affichés";
		if (groupe > -1)
			msg += " groupe "+groupe;
		if (tv != null) tv.setText(msg);
		return articles;
	}

	public static List<Groupe> getGroupes() {
		List<Groupe> groupes = new ArrayList<Groupe>();
		groupes.add(new Groupe(1, "Environnement"));
		groupes.add(new Groupe(2, "Sciences Nat."));
		groupes.add(new Groupe(3, "Autres informations"));
		groupes.add(new Groupe(4, "Faits divers"));
		groupes.add(new Groupe(5, "Tourisme Culture"));
		groupes.add(new Groupe(6, "Sports"));
		groupes.add(new Groupe(7, "Internet - Tech"));
		groupes.add(new Groupe(8, "Politique"));
		return groupes;
	}
}
