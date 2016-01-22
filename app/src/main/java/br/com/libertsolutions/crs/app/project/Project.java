package br.com.libertsolutions.crs.app.project;

/**
 * Entity project abstraction.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class Project {
    String projectId;

    String customerName;

    long deliveryForecast;

    public String getProjectId() {
        return projectId;
    }

    public Project setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Project setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public long getDeliveryForecast() {
        return deliveryForecast;
    }

    public Project setDeliveryForecast(long deliveryForecast) {
        this.deliveryForecast = deliveryForecast;
        return this;
    }
}
