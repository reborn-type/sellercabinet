package com.example.sellercabinet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductResponse {
        private Long productId;
        private String name;
        private Double price;
        private Integer count;

        @JsonProperty("countOfSales")
        private Integer countOfSales;

        @JsonProperty("averageEstimation")
        private Double averageEstimation;

        public ProductResponse() {}

        public Long getProductId() {
                return productId;
        }

        public void setProductId(Long productId) {
                this.productId = productId;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Double getPrice() {
                return price;
        }

        public void setPrice(Double price) {
                this.price = price;
        }

        public Integer getCount() {
                return count;
        }

        public void setCount(Integer count) {
                this.count = count;
        }

        public Integer getCountOfSales() {
                return countOfSales;
        }

        public void setCountOfSales(Integer countOfSales) {
                this.countOfSales = countOfSales;
        }

        public Double getAverageEstimation() {
                return averageEstimation;
        }

        public void setAverageEstimation(Double averageEstimation) {
                this.averageEstimation = averageEstimation;
        }
}