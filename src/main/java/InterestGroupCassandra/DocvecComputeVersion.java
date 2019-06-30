package InterestGroupCassandra;

public enum DocvecComputeVersion {
    DOCVEC_COMPUTE_VERSION_150("1_x_150"),DOCVEC_COMPUTE_VERSION_300("1_x_300");

    private String docVecComputeVersion;

    public static DocvecComputeVersion getDocvecComputeVersion(String version) {
        for (DocvecComputeVersion docvecComputeVersion: DocvecComputeVersion.values()) {
            if (docvecComputeVersion.getDocvecComputeVersion().equalsIgnoreCase(version)) {
                return docvecComputeVersion;
            }
        }
        return null;
    }

    private DocvecComputeVersion(String docVecComputeVersion) {
        this.docVecComputeVersion = docVecComputeVersion;
    }

    public String getDocvecComputeVersion() {
        return this.docVecComputeVersion;
    }

}