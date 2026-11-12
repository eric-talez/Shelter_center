package org.royshelter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiRoutingTest {
    @LocalServerPort
    private int port;

    @Test void unknownRouteIsNotFoundInsteadOfServerFailure() throws Exception {
        var response = request("GET", "/api/animal");
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("\"code\":\"NOT_FOUND\"")
                .doesNotContain("NoResourceFoundException", "org.royshelter");
    }

    @Test void unsupportedMethodPreservesAllowedMethods() throws Exception {
        var response = request("POST", "/api/animals");
        assertThat(response.statusCode()).isEqualTo(405);
        assertThat(response.headers().firstValue("Allow")).hasValue("GET");
        assertThat(response.body()).contains("\"code\":\"METHOD_NOT_ALLOWED\"")
                .doesNotContain("HttpRequestMethodNotSupportedException", "org.royshelter");
    }

    private HttpResponse<String> request(String method, String path) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5)).method(method, HttpRequest.BodyPublishers.noBody()).build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
