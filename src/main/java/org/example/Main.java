package org.example;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Main {
    @Test
    public void main() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.amazon.com/");

        List<WebElement> links = driver.findElements(By.tagName("a"));
        System.out.println("Number of links: " + links.size());

        List<String> urlList = new LinkedList<>();

        for (WebElement link : links) {
            String url = link.getAttribute("href");
            urlList.add(url);
        }

        urlList.parallelStream().forEach(this::checkBrokenLinks);

        driver.quit();
    }

    private void checkBrokenLinks(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() >= 400) {
                System.out.println("[BROKEN]" + httpURLConnection.getURL() + " ---> " + httpURLConnection.getResponseCode() + " : " + httpURLConnection.getResponseMessage());
            } else {
                System.out.println("[VALID ]" + httpURLConnection.getURL() + " ---> " + httpURLConnection.getResponseCode() + " : " + httpURLConnection.getResponseMessage());
            }
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
    }
}