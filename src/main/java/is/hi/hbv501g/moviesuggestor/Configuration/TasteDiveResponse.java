package is.hi.hbv501g.moviesuggestor.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TasteDiveResponse {
    private Similar similar;

    public Similar getSimilar() {
        return similar;
    }

    public void setSimilar(Similar similar) {
        this.similar = similar;
    }

    public static class Similar {
        private List<Info> info;
        private List<Result> results;

        public List<Info> getInfo() {
            return info;
        }

        public void setInfo(List<Info> info) {
            this.info = info;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }
    }

    public static class Info {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Result {
        private String name;
        private String type;
        private String wUrl;
        private String yUrl;
        private String yID;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getwUrl() {
            return wUrl;
        }

        public void setwUrl(String wUrl) {
            this.wUrl = wUrl;
        }

        public String getyUrl() {
            return yUrl;
        }

        public void setyUrl(String yUrl) {
            this.yUrl = yUrl;
        }

        public String getyID() {
            return yID;
        }

        public void setyID(String yID) {
            this.yID = yID;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}