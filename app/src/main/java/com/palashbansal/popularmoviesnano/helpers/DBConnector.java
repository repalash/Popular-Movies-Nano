package com.palashbansal.popularmoviesnano.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.palashbansal.popularmoviesnano.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Palash on 3/8/2016.
 */

public class DBConnector {
	private static final String BASE_URL = "https://api.themoviedb.org/3/";
	private static final String MOVIE_PARAM = "movie/";
	private static final String DISCOVER_PARAM = "discover/movie";
	private static final String KEY_PARAM = "?api_key=" + APIKeys.TMDB_KEY;
	private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

	public enum SortOrder { POPULAR, TOP_RATED }

	public static void getMovie(int id, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + id + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}

	public static void discover(SortOrder order, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + order.toString().toLowerCase() + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}

	private static String generatePosterPath(String posterName){
		return IMAGE_BASE_URL + posterName + KEY_PARAM;
	}

	public static void generateMovieObjects(JSONObject json, List<MovieItem> movieList, MovieListActivity.SimpleItemRecyclerViewAdapter recyclerViewAdapter){
		try {
			JSONArray results = json.getJSONArray("results");
			for(int i=0; i<results.length();i++){
				JSONObject obj = results.getJSONObject(i);
				movieList.add(new MovieItem(obj.getInt("id"), obj.getString("original_title"), generatePosterPath(obj.getString("poster_path")),
						obj.getString("overview"), obj.getInt("vote_average"), ""));
				recyclerViewAdapter.notifyItemInserted(i);
			}
		} catch (JSONException ignored) {
		}
	}

	public static void getOtherDetails(ArrayList<MovieItem> movieList, Context context){
		for(final MovieItem movie: movieList){
			getOtherDetails(movie, context);
		}
	}

	public static void getOtherDetails(final MovieItem movie, Context context){
		getMovie(movie.getId(), context,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							movie.setRelease_date(response.getString("release_date"));
						} catch (JSONException ignored) {
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}
		);
	}

	private static void getJSONFromGet(String url, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		VolleyRequestQueue.addToRequestQueue(
				new JsonObjectRequest(Request.Method.GET, url, "",
						listener, errorListener
				),
				context);
	}
}
