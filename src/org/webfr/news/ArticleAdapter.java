package org.webfr.news;

import java.util.List;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ArticleAdapter extends BaseAdapter {
	List<Article> articles;
	LayoutInflater inflater;

	public ArticleAdapter(Context context, List<Article> articles) {
		this.articles = articles;
		this.inflater = LayoutInflater.from(context);
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Object getItem(int position) {
		return articles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView tv_titre;
		TextView tv_site;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.article_item, null);
			holder.tv_titre = (TextView)convertView.findViewById(R.id.articleTitre);
			holder.tv_site = (TextView)convertView.findViewById(R.id.articleSite);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_titre.setText(articles.get(position).getTitre());
		holder.tv_site.setText(articles.get(position).getSite());
		return convertView;
	}
}
