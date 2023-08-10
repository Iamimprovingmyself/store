import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",  // Интеграция Allure
                "json:target/cucumber.json"
        },
        glue = {"ru.sogaz.steps"},
        features = {"src/test/resources/features/"}
)
public class CucumberTestRunner {
}