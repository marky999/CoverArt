package org.test;

import org.openqa.selenium.By;

public class LocatorProvider {

    public static By getElementLocator(String elementName) {
        String platform = System.getProperty("platformName", "Android");  // Or use a config file
        platform = platform.trim();

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
                    By.xpath("//android.widget.TextView[@resource-id=\"com.amazon.mp3:id/tab_bar_text_three\"]");
            case "playListPill" ->
                    By.xpath("//android.widget.Button[@resource-id=\"com.amazon.mp3:id/pill_button\" and @text=\"Playlists\"]");
            case "allPlayLists" -> By.xpath("//android.widget.TextView[@resource-id='com.amazon.mp3:id/PlaylistName']");
            case "editButton" -> By.xpath("//android.widget.Button[@resource-id=\"IconButton,Toolbar_EditButton\"]");
            case "descriptionField" -> By.id("com.amazon.mp3:id/TextArea_text");
            case "descriptionField2" -> By.xpath("//android.widget.EditText[@resource-id=\"TextArea\"]");
            case "saveButton" -> By.xpath("//android.widget.TextView[@text=\"SAVE\"]");
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
            default -> throw new IllegalArgumentException("Unknown element: " + elementName);
        };
    }
}
