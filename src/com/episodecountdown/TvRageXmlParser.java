package com.episodecountdown;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.format.Time;
import android.util.Xml;

public class TvRageXmlParser {
	
	// We don't use namespaces
	private static final String ns = null;
//	private static final String LOG_TAG = "TVRageXmlParser";
	private static final String EST_TIMEZONE = Utility.EST_TIMEZONE;
	private static final String LONDON_TIMEZONE = Utility.LONDON_TIMEZONE;
	private String mTimeZoneString;
	
	public TvRageShowModel parse(InputStream srReader) throws XmlPullParserException, IOException
	{
		try
		{
			//To Return
			TvRageShowModel returnShowContainer = new TvRageShowModel();
			//Setting up XMLPullParser
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(srReader, null);
			parser.nextTag();
			
			//If tag is not show, will through an exception
			parser.require(XmlPullParser.START_TAG, ns, "Show");
			
			while (parser.next() != XmlPullParser.END_TAG)
			{
				if (parser.getEventType() != XmlPullParser.START_TAG)
				{
					//If its an end tag skip to next tag
					continue;
				}
				
				String tagName = parser.getName();
				if (tagName.equals("name"))
				{
					returnShowContainer.setName(readText(parser));
				}
				else if (tagName.equals("totalseasons"))
				{
					returnShowContainer.setTotalseasons(Integer.parseInt(readText(parser)));
				}
				else if (tagName.equals("started"))
				{
					returnShowContainer.setStarted(readText(parser));
				}
				else if (tagName.equals("ended"))
				{
					returnShowContainer.setEnded(readText(parser));
				}
				else if (tagName.equals("status"))
				{
					// show status
					returnShowContainer.setStatus(readText(parser));
				}
				else if (tagName.equals("runtime"))
				{
					returnShowContainer.setRuntime(Integer.parseInt(readText(parser)));
				}
				else if (tagName.equals("network"))
				{
					returnShowContainer.setNetwork(readText(parser));
				}
				else if (tagName.equals("airtime"))
				{
					returnShowContainer.setAirtime(readText(parser));
				}
				else if (tagName.equals("airday"))
				{
					returnShowContainer.setAirday(readText(parser));
				}
				else if (tagName.equals("timezone"))
				{
					//Modify Timezome
					String timeZone = readText(parser);
					if (timeZone.equals(""))
					{
						//Set default TimeZone if no info available, chances are looking at local show ;)
						mTimeZoneString = Time.getCurrentTimezone();
						returnShowContainer.setTimezone(mTimeZoneString);
					} else if (timeZone.equalsIgnoreCase("GMT+0 +DST") || timeZone.equalsIgnoreCase("GMT+0")) {
						mTimeZoneString = LONDON_TIMEZONE;
						returnShowContainer.setTimezone(mTimeZoneString);
					} else if (timeZone.equalsIgnoreCase("GMT-5 +DST") || timeZone.equalsIgnoreCase("GMT-5")) {
						mTimeZoneString = EST_TIMEZONE;
						returnShowContainer.setTimezone(mTimeZoneString);
					} else if (timeZone.contains(" +DST")) {
						mTimeZoneString = timeZone.replace(" +DST", "");
						returnShowContainer.setTimezone(mTimeZoneString);
					} else if (timeZone.contains(" -DST")) {
						mTimeZoneString = timeZone.replace(" -DST", "");
						returnShowContainer.setTimezone(mTimeZoneString);
					}
				}
				else if( tagName.equals("genres")) {
					ArrayList<String> genres = readGenres(parser);
					returnShowContainer.setGenres(genres);
				}
				else if (tagName.equals("Episodelist"))
				{
					// full list of all episodes
					ArrayList<TvRageEpisodeModel> episodeList = readEpisodeList(parser);
					returnShowContainer.setEpisodeList(episodeList);
				}
				else
				{
					skip(parser);
				}
			}
			return returnShowContainer;

		} finally
		{
			srReader.close();
		}
	}
	
	private ArrayList<String> readGenres(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<String> genresList = new ArrayList<String>();
		parser.require(XmlPullParser.START_TAG, ns, "genres");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String tag = parser.getName();
			if (tag.equals("genre"))
			{
				genresList.add(readText(parser));
			} else
			{
				skip(parser);
			}
		}
		return genresList;
	}

	private ArrayList<TvRageEpisodeModel> readEpisodeList(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		ArrayList<TvRageEpisodeModel> returnArray = new ArrayList<TvRageEpisodeModel>();

		int currentSeason = 0;
		parser.require(XmlPullParser.START_TAG, ns, "Episodelist");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String tag = parser.getName();
			if (tag.equals("Season"))
			{
				String number = parser.getAttributeValue(null, "no");
				currentSeason = Integer.parseInt(number);
				returnArray.addAll(readSeason(parser, currentSeason));
			} else
			{
				skip(parser);
			}
		}
		return returnArray;
	}

	private ArrayList<TvRageEpisodeModel> readSeason(XmlPullParser parser, int currentSeason) throws XmlPullParserException,
			IOException
	{
		ArrayList<TvRageEpisodeModel> ret = new ArrayList<TvRageEpisodeModel>();
		parser.require(XmlPullParser.START_TAG, ns, "Season");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("episode"))
			{
				TvRageEpisodeModel eInfo = readEpisode(parser);
				eInfo.setSeason(currentSeason);
				ret.add(eInfo);
			} else
			{
				skip(parser);
			}
		}
		return ret;
	}
	
	private TvRageEpisodeModel readEpisode(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		TvRageEpisodeModel returnEpisode = new TvRageEpisodeModel();
		parser.require(XmlPullParser.START_TAG, ns, "episode");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			if (name.equals("seasonnum"))
			{
				String number = readText(parser);
				int episode = Integer.parseInt(number);
				returnEpisode.setEpisode(episode);
			} else if (name.equals("airdate"))
			{
				Date airDate = null;
				try
				{
					// 2013-05-12
					SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					sdfToDate.setTimeZone(TimeZone.getTimeZone(mTimeZoneString));
					airDate = sdfToDate.parse(readText(parser));
				} catch (ParseException e)
				{
//					Log.e(LOG_TAG, e.toString());
				}

				returnEpisode.setAirDate(airDate);
			} else if (name.equals("title"))
			{
				String title = readText(parser);
				returnEpisode.setTitle(title);
			} else if (name.equals("epnum"))
			{
				int epnum = Integer.parseInt(readText(parser));
				returnEpisode.setEpisodeFromStart(epnum);
			} else if (name.equals("screencap"))
			{
				String screenCap = readText(parser);
				returnEpisode.setScreenCap(screenCap);
			}
			else
			{
				skip(parser);
			}
		}
		return returnEpisode;
	}

	//Helper Functions
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		String result = "";
		if (parser.next() == XmlPullParser.TEXT)
		{
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if (parser.getEventType() != XmlPullParser.START_TAG)
		{
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0)
		{
			switch (parser.next())
			{
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}