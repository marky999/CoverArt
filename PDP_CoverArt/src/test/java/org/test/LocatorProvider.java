package org.test;

import org.openqa.selenium.By;

public class LocatorProvider {
    public static String platform = System.getProperty("platformName", "Android");  // Or use a config file

    public static String getStringLocator(String str){
        System.out.println("platform -> " + platform);
        switch (platform){
            case "Android":
                return getAndroidString(str);
            case "iOS":
                return getiOSString(str);
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    public static String getAndroidString(String str){
        return switch (str) {
            case "characterLimitReached" -> "//android.widget.TextView[@text=\"Character limit reached\"]";
            default -> throw new IllegalArgumentException("Unknown element: " + str);
        };
    }

    public static String getiOSString(String str){
        return switch (str) {
            case "characterLimitReached" -> "//XCUIElementTypeStaticText[@name=\"Character limit reached\"]";
            default ->  throw new IllegalArgumentException("Unsupported platform: " + str);
        };
    }

    public static By getElementLocator(String elementName) {
        switch (platform) {
            case "Android":
                return getAndroidLocator(elementName);
            case "iOS":
                return getiOSLocator(elementName);
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    private static By getAndroidLocator(String elementName) {
        return switch (elementName) {
            case "libraryButton" ->
                    By.xpath("//android.widget.ImageView[@resource-id=\"com.amazon.mp3:id/tab_bar_image_three\"]");
            case "playListPill" ->
                    By.xpath("//android.widget.Button[@resource-id=\"com.amazon.mp3:id/pill_button\" and @text=\"Playlists\"]");
            case "allPlayLists" -> By.xpath("//android.widget.TextView[@resource-id='com.amazon.mp3:id/PlaylistName']");
            case "editButton" -> By.xpath("//android.widget.Button[@resource-id=\"IconButton,Toolbar_EditButton\"]");
            case "descriptionField" -> By.id("com.amazon.mp3:id/TextArea_text");
            case "descriptionField2" -> By.xpath("//android.widget.EditText[@resource-id=\"TextArea\"]");
            case "saveButton" -> By.xpath("//android.widget.TextView[@text=\"SAVE\"]");
            case "chooseButton" -> By.xpath("//XCUIElementTypeStaticText[@name=\"Choose\"]");
            case "backButton" -> By.xpath("//android.widget.Button[@content-desc=\"Go back\"]/android.view.ViewGroup");
            case "cancelText" -> By.xpath("//android.widget.Button[@content-desc=\"Go back\"]/android.view.ViewGroup");
            case "cancelButton" -> By.xpath("//XCUIElementTypeButton[@name=\"Cancel\"]");
            default -> throw new IllegalArgumentException("Unknown element: " + elementName);
        };
    }

    private static By getiOSLocator(String elementName) {
        return switch (elementName) {
            case "libraryButton" ->
                    By.xpath("//XCUIElementTypeButton[@name=\"AMMyMusicNavigationTabIconAccessibilityIdentifier\"]");
            case "playListPill" ->
                    By.xpath("//XCUIElementTypeButton[@name=\"PillNavigatorTextButton\" and @label=\"Playlists\"]");
            case "allPlayLists" -> By.xpath("//XCUIElementTypeCollectionView//XCUIElementTypeCell");
            case "editButton" -> By.xpath("//XCUIElementTypeButton[@name=\"IconButton,Toolbar_EditButton\"]");
            case "descriptionField" -> By.xpath("//XCUIElementTypeStaticText[@name=\"TextArea_text\"]");
            case "descriptionField2" -> By.xpath("//XCUIElementTypeTextView[@name=\"TextArea\"]");
            case "saveButton" -> By.xpath("//XCUIElementTypeButton[@name=\"BauhausTextButton,TopAppBar_Done\"]");
            case "chooseButton" -> By.xpath("//XCUIElementTypeStaticText[@name=\"Choose\"]");
            case "backButton" -> By.xpath("//XCUIElementTypeButton[contains(@name, 'Back')]");
            case "cancelText" -> By.xpath("//XCUIElementTypeStaticText[@name=\"Cancel\"]");
            case "cancelButton" -> By.xpath("//XCUIElementTypeButton[@name=\"Cancel\"]");
            default -> throw new IllegalArgumentException("Unknown element: " + elementName);
        };
    }
}
