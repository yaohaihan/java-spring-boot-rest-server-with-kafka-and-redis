package oxus.lib.common.spring;


import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TraceResponseFilter implements WebFilter {
    private static final List<String> TRACING_HEADERS =
            List.of(
                    "X-B3-TraceId",
                    "X-B3-SpanId",
                    "X-B3-ParentSpanId",
                    "X-Request-Id",
                    "X-B3-Sampled",
                    "X-B3-Flags",
                    "X-Ot-Span-Context"
            );

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        TRACING_HEADERS.forEach(headerName -> copyHeader(
                serverWebExchange.getRequest().getHeaders(), serverWebExchange.getResponse().getHeaders(), headerName
        ));


        return webFilterChain.filter(serverWebExchange);
    }

    private void copyHeader(HttpHeaders requestHeaders, HttpHeaders responseHeaders, String headerName) {
        if (requestHeaders.containsKey(headerName)) {
            responseHeaders.add(headerName, requestHeaders.getFirst(headerName));
        }
    }
}