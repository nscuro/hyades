package org.hyades.repositories;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class RepositoryAnalyzerFactoryTest {

    @Inject
    RepositoryAnalyzerFactory analyzerFactory;

    @ParameterizedTest
    @CsvSource(value = {
            "pkg:foo/bar, false",
            "pkg:cargo/foo, false",
            "pkg:cocoapods/foo, false",
            "pkg:composer/foo, true",
            "pkg:deb/foo, false",
            "pkg:gem/foo, true",
            "pkg:golang/foo, true",
            "pkg:hex/foo, true",
            "pkg:maven/foo/bar, true",
            "pkg:npm/foo, true",
            "pkg:nuget/foo, true",
            "pkg:pypi/foo, true",
            "pkg:rpm/foo, false",
            "pkg:cpan/foo, true"
    })
    void testHasApplicableAnalyzer(final String purl, final boolean expectedResult) throws MalformedPackageURLException {
        assertThat(analyzerFactory.hasApplicableAnalyzer(new PackageURL(purl))).isEqualTo(expectedResult);
    }

    @Test
    void testCreateAnalyzerWithUnsupportedPurl() throws MalformedPackageURLException {
        assertThat(analyzerFactory.createAnalyzer(new PackageURL("pkg:foo/bar"))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pkg:composer/foo/bar",
            "pkg:gem/foo/bar",
            "pkg:golang/foo/bar",
            "pkg:hex/foo/bar",
            "pkg:maven/foo/bar",
            "pkg:npm/foo/bar",
            "pkg:nuget/foo/bar",
            "pkg:pypi/foo/bar",
            "pkg:cpan/foo/bar"
    })
    void testCreateAnalyzer(final String purl) throws MalformedPackageURLException {
        assertThat(analyzerFactory.createAnalyzer(new PackageURL(purl))).isPresent();
    }

}