package com.bobajingo.crunchyrepo;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// extends from Anikku - handles video player
public class Crunchyroll extends AnimeHttpSource{

	//sets extension name
    	@Override
    	public String getName(){
    	    return "Crunchy";
    	}

    	//sets extension base url
    	@Override
    	public String getBaseUrl(){
        	return "https://crunchyroll.com";
    	}

    	// gets mainpage results when extension opened (browse)
    	@Override
    	public Request popularAnimeRequest(int page){
        	String url = getBaseUrl() + "/content/v2/discover/browse?size=20";        
        	return new Request.Builder().url(url).build();
    	}

    	// Takes in the returned json file (response) and extracts show titles and image URLs
     	@Override
    	public AnimesPage popularAnimeParse(Response response) throws IOException{
        	String jsonResponse = response.body().string();     

        	List<SAnime> animeList = new ArrayList<>();
        	try{
            		JSONObject root = new JSONObject(jsonResponse);
            		JSONArray items = root.getJSONArray("items");
            		for(int i = 0; i < items.length(); i++){
                		JSONObject currentShow = items.getJSONObject(i);
                		// Blank template object for Anikku specifically
                		SAnime anime = new SAnime();
                		// Get Anime name
                		String titleText = currentShow.getString("title");
                		anime.setTitle(titleText);
                		// Unique ID Crunchyroll uses to identify show
                		String uniqueId = currentShow.getString("id");
                		anime.setUrl(uniqueId);
                		// Get thumbnail
                		JSONObject imagesObj = currentShow.getJSONObject("images");
                		JSONArray posterArray = imagesObj.getJSONArray("poster_tall");
                		JSONObject firstPosterSize = posterArray.getJSONArray(0).getJSONObject(0);
                		String imageUrl = firstPosterSize.getString("source");
                		anime.setThumbnailUrl(imageUrl); 
                		animeList.add(anime);
            		}
        	} 
        	catch (Exception e){
            		// If Crunchyroll changes API format unexpectedly, catch so app doesn't crash
            		e.printStackTrace(); 
        	}
        	// Returns list back to Anikku. True if next page of entries to load if scroll down
        	return new AnimesPage(animeList, true);
    	}

}