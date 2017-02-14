package com.gmoonxs.www.orangehome;

import com.gmoonxs.www.orangehome.Weather;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by 威 on 2016/6/26.
 */
public class WeatherXmlParse {
    public Weather parseWeatherXml(String xmlData) {
        Weather weather=new Weather();
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")){
                            eventType=xmlPullParser.next();
                            weather.setCityName(xmlPullParser.getText());
                        }
                        else if(xmlPullParser.getName().equals("updatetime")){
                            eventType=xmlPullParser.next();
                            weather.setUpdateTime(xmlPullParser.getText());
                        }

                        else if(xmlPullParser.getName().equals("wendu")){
                            eventType=xmlPullParser.next();
                            weather.setWendu(xmlPullParser.getText());
                        }

                        else if(xmlPullParser.getName().equals("shidu")){
                            eventType=xmlPullParser.next();
                            weather.setShidu(xmlPullParser.getText());
                        }
                        else if(xmlPullParser.getName().equals("aqi")){
                            eventType=xmlPullParser.next();
                            weather.setAqi(xmlPullParser.getText());
                        }
                        else if(xmlPullParser.getName().equals("pm25")){
                            eventType=xmlPullParser.next();
                            weather.setPm2_5(xmlPullParser.getText());
                        }
                        else if(xmlPullParser.getName().equals("quality")){
                            eventType=xmlPullParser.next();
                            weather.setQuality(xmlPullParser.getText());
                        }
                        else if(xmlPullParser.getName().equals("pm10")){
                            eventType=xmlPullParser.next();
                            weather.setPm10(xmlPullParser.getText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }

        }catch (Exception e){

        }
        return weather;
    }
}
