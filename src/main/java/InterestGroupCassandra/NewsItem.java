package InterestGroupCassandra;

import java.util.List;

public class NewsItem {

    private String id;
    private Language language;
    private List<Double> docVector300Dim;
    private List<Double> docVector;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Double> getDocVector300Dim() {
        return docVector300Dim;
    }

    public void setDocVector300Dim(List<Double> docVector300Dim) {
        this.docVector300Dim = docVector300Dim;
    }

    public List<Double> getDocVector() {
        return docVector;
    }

    public void setDocVector(List<Double> docVector) {
        this.docVector = docVector;
    }
}
