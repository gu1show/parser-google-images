package processing;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Loader {
    private final String URL = "https://images.google.com/";
    private final String QUERY;
    private final int PAGES_NEEDED;

    public Loader(String query, int pagesNeeded) {
        this.QUERY = query;
        this.PAGES_NEEDED = pagesNeeded;
    }

    public void parseAndLoad() {
        System.setProperty("webdriver.chrome.driver", "selenium/chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(URL);

        String xpathInput = "/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input";
        WebElement element = webDriver.findElement(By.xpath(xpathInput));
        element.sendKeys(QUERY);
        element.sendKeys(Keys.ENTER);

        int elementsIterate = getNumberOfImagesAndNewRequests(PAGES_NEEDED), numberImageNow = 1;
        sleep(1000);
        for (int i = 1; i <= elementsIterate; i++) {
            if (i % 25 != 0) {
                String xpathImageElement = "//*[@id=\"islrg\"]/div[1]/div[" + i +
                        "]/a[1]/div[1]/img";
                WebElement imageElement = webDriver.findElement(By.xpath(xpathImageElement));

                ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true)", imageElement);
                sleep(1000);

                ArrayList<String> tabs = openImageInNewTab(webDriver, imageElement);
                sleep(1000);

                saveImage(webDriver, numberImageNow, QUERY);
                numberImageNow++;

                webDriver.close();
                webDriver.switchTo().window(tabs.get(0));
            }
        }
        webDriver.quit();
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getNumberOfImagesAndNewRequests(int pagesNeeded) {
        return pagesNeeded + pagesNeeded / 25;
    }

    private static void saveImage(WebDriver webDriver, int numberImage, String query) {
        WebElement image = webDriver.findElement(By.tagName("img"));
        File screenshot = image.getScreenshotAs(OutputType.FILE);
        new File("out/" + query).mkdir();
        try {
            FileUtils.copyFile(screenshot, new File("out/" + query + "/" + numberImage + ".jpeg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> openImageInNewTab(WebDriver webDriver, WebElement imageElement) {
        String link = imageElement.getAttribute("src");
        ((JavascriptExecutor)webDriver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(1));
        webDriver.get(link);

        return tabs;
    }
}
