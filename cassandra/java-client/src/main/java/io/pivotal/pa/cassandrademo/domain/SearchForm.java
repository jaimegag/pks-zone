package io.pivotal.pa.cassandrademo.domain;


public class SearchForm {
    private String commonName;
    private String county;

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public String toString() {
        return "SearchForm{" +
                "commonName='" + commonName + '\'' +
                ", county='" + county + '\'' +
                '}';
    }
}
