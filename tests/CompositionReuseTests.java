package tests;

import com.paulhammant.tnywb.TinyWeb;
import org.forgerock.cuppa.Test;

import static com.paulhammant.tnywb.TinyWeb.Method.GET;
import static org.forgerock.cuppa.Cuppa.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static tests.Suite.bodyAndResponseCodeShouldBe;
import static tests.Suite.httpGet;

@Test
public class CompositionReuseTests {
    TinyWeb.Server webServer;

    {
        describe("Given a TinyWeb server with a reusable composition", () -> {
            before(() -> {
                webServer = new TinyWeb.Server(8080, -1) {{
                    Runnable composition = () -> {
                        endPoint(GET, "/endpoint", (req, res, ctx) -> {
                            res.write("Hello from composed endpoint");
                        });
                    };

                    path("/first", composition);
                    path("/second", composition);
                    path("/third", composition);
                }};
                webServer.start();
            });

            it("Then it should respond correctly to requests at the first composed endpoint", () -> {
                bodyAndResponseCodeShouldBe(httpGet("/first/endpoint"), "Hello from composed endpoint", 200);
            });

            it("Then it should respond correctly to requests at the second composed endpoint", () -> {
                bodyAndResponseCodeShouldBe(httpGet("/second/endpoint"), "Hello from composed endpoint", 200);
            });

            it("Then it should respond correctly to requests at the third composed endpoint", () -> {
                bodyAndResponseCodeShouldBe(httpGet("/third/endpoint"), "Hello from composed endpoint", 200);
            });

            after(() -> {
                webServer.stop();
                webServer = null;
            });
        });
    }
}