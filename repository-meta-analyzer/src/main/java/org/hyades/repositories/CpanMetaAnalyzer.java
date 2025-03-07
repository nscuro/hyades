package org.hyades.repositories;


import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.hyades.model.MetaModel;
import org.hyades.persistence.model.Component;
import org.hyades.persistence.model.RepositoryType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * An IMetaAnalyzer implementation that supports CPAN.
 */
public class CpanMetaAnalyzer extends AbstractMetaAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpanMetaAnalyzer.class);
    private static final String DEFAULT_BASE_URL = "https://fastapi.metacpan.org/v1";
    private static final String API_URL = "/module/%s";

    CpanMetaAnalyzer() {
        this.baseUrl = DEFAULT_BASE_URL;
    }

    @Override
    public RepositoryType supportedRepositoryType() {
        return RepositoryType.CPAN;
    }

    @Override
    public boolean isApplicable(Component component) {
        return component.getPurl() != null && "cpan".equals(component.getPurl().getType());
    }

    @Override
    public MetaModel analyze(final Component component) {
        final MetaModel meta = new MetaModel(component);
        if (component.getPurl() != null) {

            final String packageName;
            if (component.getPurl().getNamespace() != null) {
                packageName = component.getPurl().getNamespace() + "%2F" + component.getPurl().getName();
            } else {
                packageName = component.getPurl().getName();
            }

            final String url = String.format(baseUrl + API_URL, packageName);
            try (final CloseableHttpResponse response = processHttpRequest(url)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (response.getEntity()!=null) {
                        String responseString = EntityUtils.toString(response.getEntity());
                        var jsonObject = new JSONObject(responseString);
                        final String latest = jsonObject.optString("version");
                        if (latest != null) {
                            meta.setLatestVersion(latest);
                        }
                        final String published = jsonObject.optString("date");
                        if (published != null) {
                            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                meta.setPublishedTimestamp(dateFormat.parse(published));
                            } catch (ParseException e) {
                                LOGGER.warn("An error occurred while parsing upload time", e);
                            }
                        }
                    }
                } else {
                    handleUnexpectedHttpResponse(LOGGER, url, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), component);
                }
            } catch (IOException e) {
                handleRequestException(LOGGER, e);
            }
        }
        return meta;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}