/**
 *
 */
package yahoo_finance;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author nimda
 *
 */
public class Yahoo_Finance {

    private static Connection c;
    private static Statement stm;

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager
                    .getConnection(String.format("jdbc:sqlite:%s/stocks.db", home()));
            stm = c.createStatement();
            String cmd = "select symb from tickers order by symb";
            ResultSet rS = stm.executeQuery(cmd);
            List<String> stocks = new ArrayList();
            while (rS.next()) {
                stocks.add(rS.getString("symb"));
            }

            /*
            System.setProperty("webdriver.gecko.driver","E:/Selenium/geckodriver.exe");
            WebDriver browser = new FirefoxDriver();
             */
            System.setProperty("webdriver.chrome.driver", String.format("%s/chromedriver",
                    home()));
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized"); // open Browser in maximized mode
            options.addArguments("disable-infobars"); // disabling infobars
            options.addArguments("--disable-extensions"); // disabling extensions
            options.addArguments("--disable-gpu"); // applicable to windows os only
            options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
            options.addArguments("--no-sandbox"); // Bypass OS security model
            WebDriver browser = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(browser, 60);
//		browser.get("https://finance.yahoo.com/portfolio/p_0/view/v1"); 
//		browser.get("https://login.yahoo.com/?.src=finance&.intl=us&.done=https%3A%2F%2Ffinance.yahoo.com%2Fportfolios&add=1");

            for (String tick : stocks) {
                String url = String.format("https://finance.yahoo.com/quote/%s/history?p=%s", tick, tick);
                browser.get(url);
                if (browser.getTitle().contains("Oath")) {
                    WebElement oath = browser.findElement(By.name("agree"));
                    oath.click();
                }
                /*
    * Uncomment if more than 22 days ago
    
    String xpath =
    "//*[@id=\"Col1-1-HistoricalDataTable-Proxy\"]/section/div[1]/div[1]/button";
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    WebElement apply = browser.findElement(By.xpath(xpath));
    apply.click();
    try {
    Thread.sleep(4000);
    } catch (InterruptedException e) {
    e.printStackTrace();
    }
    *
                 */
                String slcr = "#Col1-1-HistoricalDataTable-Proxy > section > div.Mt\\28 15px\\29.drop-down-selector.historical > div.C\\28 \\24 c-fuji-grey-j\\29.Mt\\28 20px\\29.Mb\\28 15px\\29 > span.Fl\\28 end\\29.Pos\\28 r\\29.T\\28 -6px\\29 > a";
                String xpath = "//*[@id=\"Col1-1-HistoricalDataTable-Proxy\"]/section/div[1]/div[2]/span[2]/a";
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(slcr)));
                WebElement download = browser.findElement(By.cssSelector(slcr));
                download.click();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            browser.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Yahoo_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String home() {
        File here = new File(".");
        return here.getAbsolutePath();
    }
}
