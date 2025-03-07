package org.hyades.persistence.repository;

import com.github.packageurl.PackageURL;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.jpa.QueryHints;
import org.hyades.persistence.model.VulnerableSoftware;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class VulnerableSoftwareRepository implements PanacheRepository<VulnerableSoftware> {

    public List<VulnerableSoftware> getAllVulnerableSoftware(final String part, final String vendor, final String product, final PackageURL purl) {
        boolean cpeSpecified = (part != null && vendor != null && product != null);
        if (cpeSpecified && purl != null) {
            return find("part = :part and vendor = :vendor and product = :product or (purlType = :purlType and purlNamespace = :purlNamespace and purlName = :purlName)",
                    Parameters.with("part", part)
                            .and("vendor", vendor)
                            .and("product", product)
                            .and("purlType", purl.getType())
                            .and("purlNamespace", purl.getNamespace())
                            .and("purlName", purl.getName()))
                    .withHint(QueryHints.HINT_READONLY, true)
                    .list();
        } else if (cpeSpecified) {
            return find("part = :part and vendor = :vendor and product = :product",
                    Parameters.with("part", part)
                            .and("vendor", vendor)
                            .and("product", product))
                    .withHint(QueryHints.HINT_READONLY, true)
                    .list();
        } else if (purl != null) {
            if (purl.getNamespace() == null) {
                return find("purlType = :purlType and purlName = :purlName and purlNamespace is null",
                        Parameters.with("purlType", purl.getType())
                                .and("purlName", purl.getName()))
                        .withHint(QueryHints.HINT_READONLY, true)
                        .list();
            } else {
                return find("purlType = :purlType and purlNamespace = :purlNamespace and purlName = :purlName",
                        Parameters.with("purlType", purl.getType())
                                .and("purlNamespace", purl.getNamespace())
                                .and("purlName", purl.getName()))
                        .withHint(QueryHints.HINT_READONLY, true)
                        .list();
            }
        } else {
            return new ArrayList<>();
        }
    }
}
