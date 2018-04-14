package io.pivotal.pa.cassandrademo.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Created by jaguilar on 4/7/18.
 */
@Table(value="species")
public class Species {
    @PrimaryKey(value = "id")
    int id;

    @Column(value = "county")
    String county;

    @Column(value = "category")
    String category;

    @Column(value = "taxonomy_g")
    String taxonomy_g;

    @Column(value = "taxonomy_sg")
    String taxonomy_sg;

    @Column(value = "sci_name")
    String sci_name;

    @Column(value = "common_name")
    String common_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTaxonomy_g() {
        return taxonomy_g;
    }

    public void setTaxonomy_g(String taxonomy_g) {
        this.taxonomy_g = taxonomy_g;
    }

    public String getTaxonomy_sg() {
        return taxonomy_sg;
    }

    public void setTaxonomy_sg(String taxonomy_sg) {
        this.taxonomy_sg = taxonomy_sg;
    }

    public String getSci_name() {
        return sci_name;
    }

    public void setSci_name(String sci_name) {
        this.sci_name = sci_name;
    }

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }

    @Override
    public String toString() {
        return "Species{" +
                "id=" + id +
                ", county='" + county + '\'' +
                ", category='" + category + '\'' +
                ", taxonomy_g='" + taxonomy_g + '\'' +
                ", taxonomy_sg='" + taxonomy_sg + '\'' +
                ", sci_name='" + sci_name + '\'' +
                ", common_name='" + common_name + '\'' +
                '}';
    }
}
