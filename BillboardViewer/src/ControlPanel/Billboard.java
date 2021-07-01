package ControlPanel;

import java.util.ArrayList;
import java.util.regex.*;
import java.nio.file.*;

public class Billboard{
    public String name;
    public String creator;
    public String backgroundColour;
    public String message;
    public String messageColour;
    public String pictureURL;
    public String pictureData;
    public String infoMessage;
    public String infoColour;

    /**
     *
     * @param backgroundColour The colour of the background. Default is #dbdbdb.
     * @param message The contents of the message. Required.
     * @param messageColour The colour of the message. Default is #000000.
     * @param pictureURL The URL to a picture. This or the data are required.
     * @param pictureData The Base64 encoded representation of a picture. This or the URL are required.
     * @param infoMessage The information message. Required.1
     * @param infoColour The colour of the information message. Default is #000000.
     */
    public boolean fromStrings(String name,
                               String backgroundColour,
                               String message,
                               String messageColour,
                               String pictureURL,
                               String pictureData,
                               String infoMessage,
                               String infoColour,
                               String creator){
        if (name.length() > 0){
            this.name = name;
        } else {
            return false;
        }
        if (message.length() == 0 && pictureData.length() == 0 && pictureURL.length() == 0 && infoMessage.length() == 0){
            return false;
        }
        if (backgroundColour.length() > 0) {
            this.backgroundColour = backgroundColour;
        } else {
            this.backgroundColour = "#dbdbdb";
        }
        this.message = message;
        if (messageColour.length() > 0) {
            this.messageColour = messageColour;
        } else {
            this.messageColour = "#000000";
        }
        if (pictureURL.length() > 0) {
            this.pictureURL = pictureURL;
        } else if (pictureData.length() > 0) {
            this.pictureData = pictureData;
        } else {
            this.pictureData = pictureData;
            this.pictureURL = pictureURL;
        }
        this.infoMessage = infoMessage;
        if (infoColour.length() > 0) {
            this.infoColour = infoColour;
        } else {
            this.infoColour = "#000000";
        }
        if (creator.length() > 0){
            this.creator = creator;
        } else {
            return false;
        }
        return true;
    }

    /***
     * @param filepath The absolute filepath to the XML file.
     */
    public void fromXML(String filepath) throws Exception {
        String data = Files.readString(Paths.get(filepath));
        Matcher backgroundColourMatcher = Pattern.compile("background=\"(.*?)\"").matcher(data);
        if (backgroundColourMatcher.find()){
            this.backgroundColour = backgroundColourMatcher.group(1);
        }

        Matcher messageMatch = Pattern.compile("message.colour=\".*?\">(.*?)<", Pattern.DOTALL).matcher(data);
        if (messageMatch.find()){
            this.message = messageMatch.group(1);
        } else {
            throw new Exception("No message found!");
        }

        Matcher messageColourMatcher = Pattern.compile("message.colour=\"(.*?)\">", Pattern.DOTALL).matcher(data);
        if (messageColourMatcher.find()){
            this.messageColour = messageColourMatcher.group(1);
        }

        Matcher pictureURLMatcher = Pattern.compile("picture.url=\"(.*?)\"").matcher(data);
        Matcher pictureDataMatcher = Pattern.compile("picture.data=\"(.*?)\"").matcher(data);
        if (pictureURLMatcher.find()) {
            this.pictureURL = pictureURLMatcher.group(1);
        } else if (pictureDataMatcher.find()){
            this.pictureData = pictureDataMatcher.group(1);
        } else {
            throw new Exception("No picture data or URL found!");
        }

        Matcher infoMessageMatcher = Pattern.compile("information.colour=\".*?\">(.*?)<", Pattern.DOTALL).matcher(data);
        if (infoMessageMatcher.find()){
            this.infoMessage = infoMessageMatcher.group(1);
        } else {
            throw new Exception("No information message found!");
        }

        Matcher infoMessageColourMatcher = Pattern.compile("information.colour=\"(.*?)\"").matcher(data);
        if (infoMessageColourMatcher.find()){
            this.infoColour = infoMessageColourMatcher.group(1);
        }
    }

    /**
     * @param filepath The absolute filepath to the XML file.
     * @return Returns the string version of the XML file written.
     */
    public String toXML(String filepath) throws Exception{
        Path file = Paths.get(filepath);
        ArrayList<String> XMLLines = new ArrayList<>();
        XMLLines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        XMLLines.add("<billboard background=\"" + this.backgroundColour + "\">");
        XMLLines.add("<message colour=\"" + this.messageColour + "\">" + this.message + "</message>");
        if (this.pictureURL.length() > 0){
            XMLLines.add("<picture url=\"" + this.pictureURL + "\" />");
        } else {
            XMLLines.add("<picture data=\"" + this.pictureData + "\" />");
        }
        XMLLines.add("<information colour=\"" + this.infoColour + "\">" + this.infoMessage + "</information>");
        XMLLines.add("</billboard>");
        Path path = Files.write(file, XMLLines);
        return path.toString();
    }
}