package InterestGroupCassandra;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DocVector {

    private static final Logger LOG = LoggerFactory.getLogger(DocVector.class);

    private String id;
    private List<Double> vector;
    private long npPk;

    public DocVector() {

    }

    public DocVector(String id, List<Double> vector) {
        this.id = id;
        this.vector = vector;
    }

    public DocVector(String id, long npPk, List<Double> vector) {
        this.id = id;
        this.vector = vector;
        this.npPk = npPk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getVector() {
        return vector;
    }

    public void setVector(List<Double> vector) {
        this.vector = vector;
    }

    public long getNpPk() {
        return npPk;
    }

    public void setNpPk(long npPk) {
        this.npPk = npPk;
    }

    public int getVectorSize(){
        return (vector == null) ? 0 : this.vector.size();
    }

    public static String getVectorAsString(List<Double> vector) {
        StringBuilder builder = new StringBuilder();
        if (vector != null && !vector.isEmpty()) {
            for (double scalar : vector) {
                builder.append(scalar);
                builder.append(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
            }
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public static List<Double> getVectorAsDoubles(String docVectorString) {
        List<Double> resultVector = null;
        try {
            if (StringUtils.isNotEmpty(docVectorString)) {
                String[] scalars = docVectorString.split(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
                resultVector = new ArrayList<Double>();
                for (String scalar : scalars) {
                    resultVector.add(Double.parseDouble(scalar));
                }
            }
        } catch (Exception e) {
            resultVector = null;
            LOG.error("Exception caught in converting doc vector from string", e);
        }
        return resultVector;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (vector != null && !vector.isEmpty()) {
            builder.append(id + ":");
            if(npPk >0){
                builder.append(npPk + ":");
            }
            for (double scalar : vector) {
                builder.append(scalar);
                builder.append(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
            }
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public static DocVector fromString(String docVectorString) {
        DocVector resultVector = null;
        try {
            String[] tokens = docVectorString.split(":");
            if(tokens.length == 2){
                String id = tokens[0];
                String[] scalars = tokens[1].split(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
                List<Double> vector = new ArrayList<Double>();
                for (String scalar : scalars) {
                    vector.add(Double.parseDouble(scalar));
                }
                resultVector = new DocVector(id, vector);
            }else if(tokens.length == 3){
                String id = tokens[0];
                long npPk = Long.parseLong(tokens[1]);
                String[] scalars = tokens[2].split(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
                List<Double> vector = new ArrayList<Double>();
                for (String scalar : scalars) {
                    vector.add(Double.parseDouble(scalar));
                }
                resultVector = new DocVector(id, npPk, vector);
            }else{
                String[] scalars = docVectorString.split(RelatedNewsConstants.VECTOR_NUMBERS_DELIMITER_FOR_CACHE);
                List<Double> vector = new ArrayList<Double>();
                for (String scalar : scalars) {
                    vector.add(Double.parseDouble(scalar));
                }
                resultVector = new DocVector();
                resultVector.setVector(vector);
            }

        } catch (Exception e) {
            resultVector =  null;
        }
        return resultVector;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocVector other = (DocVector) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}